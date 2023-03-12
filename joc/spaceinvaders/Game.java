package joc.spaceinvaders;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.util.Random;

public class Game extends Canvas {
	//Clasa principala a jocului; va controla display-ul si 
	//evenimentele jocului (e.g. alien distrus, jucator distrus) si
	//va lua deciziile corespunzatoare.
	
	private BufferStrategy strategy;//ne permite sa folosim page-flipping
	
	private boolean gameRunning = true;//"true" daca jocul ruleaza
	
	private ArrayList entities = new ArrayList();//lista tuturor entitatilor din joc
	
	private ArrayList removeList = new ArrayList();//lista entitatilor ce trebuie eliminate
	
	private Entity ship;//entitatea ce reprezinta nava jucatorului
	
	private double moveSpeed = 300;//viteza cu care se misca nava (pixeli/secunda)
	
	private long lastFire = 0;//timpul la care jucatorul a tras ultima oara
	
	private long firingInterval = 500;//intervalul dintre proiectilele jucatorului (ms)
	
	private long lastalienFire = 0;//timpul la care extraterestrul a tras ultima oara
	
	private long alienfireInteval = 500;//intervalul dintre proiectilele extraterestrului (ms)
	
	private int alienCount;//numarul de extraterestrii ramasi pe ecran

	private String message = "";//mesajul ce trebuie afisat în asteptarea apasarii unei taste
	
	private boolean waitingForKeyPress = true;//"true" daca asteptam apasarea unei taste
	
	private boolean leftPressed = false;//"true" daca sageata stanga de la tastatura a fost apasata
	
	private boolean rightPressed = false;//"true" daca sageata dreapta de la tastatura a fost apasata
	
	private boolean firePressed = false;//"true" daca jucatorul trage
	
	private boolean logicRequiredThisLoop = false;//"true" daca logica trebuie aplicata in aceasta bucla
	
	public Game() {
		//Construieste jocul si il porneste
		
		JFrame container = new JFrame("Space Invaders");//creaza cadrul ce contine jocul
		
		JPanel panel = (JPanel) container.getContentPane();
		panel.setPreferredSize(new Dimension(800,600));
		panel.setLayout(null);
		//obtine continutul cadrului si seteaza rezolutia jocului
		
		setBounds(0,0,800,600);
		panel.add(this);//seteaza dimensiunea canvas-ului si il pune in cadru
		
		setIgnoreRepaint(true);//canvas-ul nu se va reseta automat
		
		container.pack();
		container.setResizable(false);
		container.setVisible(true);//genereaza vizibilitatea ferestrei
		
		container.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);//va inchide fereastra la cererea utilizatorului
			}
		});
		
		addKeyListener(new KeyInputHandler());//creeaza input pentru a raspunde la comenzile utilizatorului
		
		requestFocus();//solicita focalizarea astfel incat evenimentele au loc in fereastra

		createBufferStrategy(2);
		strategy = getBufferStrategy();//creeaza strategia de buffering pentru a permite AWT sa gestioneze graficile
		
		initEntities();//initializeaza entitatile la deschiderea ferestrei
	}
	
	private void startGame() {
		//incepe un joc nou

		entities.clear();//elimina entitatile existente
		initEntities();//intializeaza un nou set de entitati
		
		leftPressed = false;
		rightPressed = false;
		firePressed = false;
		//sterge orice setari de la tastatura am putea avea in prezent
	}
	
	private void initEntities() {
		//initializeaza starea initiala a entitatilor
		ship = new ShipEntity(this,"sprites/ship.png",370,550);
		entities.add(ship);//creeaza nava jucatorului si o plaseaza aproape de centrul ecranului
		
		alienCount = 0;
		for (int row=0;row<5;row++) {
			for (int x=0;x<12;x++) {
				Entity alien = new AlienEntity(this,"sprites/alien.png",100+(x*50),(50)+row*30);
				entities.add(alien);
				alienCount++;
				//creeaza blocul de extraterestrii (5 randuri a cate 12 extraterestrii)
			}
		}
	}
	
	public void updateLogic() {
		logicRequiredThisLoop = true;
		//notificare de la o entitate ca logica jocului trebuie rulata la urmatoarea ocazie
		//de obicei, ca urmare a unor evenimente din joc
	}

	public void removeEntity(Entity entity) {
		removeList.add(entity);//elimina o entitate din joc
	}
	
	public void notifyDeath() {
		message = "Ai pierdut!";//notifica jucatorul ca a pierdut
		waitingForKeyPress = true;
	}
	
	public void notifyWin() {
		message = "Ai castigat!";//notifica jucatorul ca a castigat
		waitingForKeyPress = true;
	}
	
	public void notifyAlienKilled() {
		
		alienCount--;//reduce numarul extraterestrilor pe masura ce sunt distrusi
		
		if (alienCount == 0) {
			notifyWin();//daca nu mai exista extraterestrii, jucatorul a castigat
		}
		
		for (int i=0;i<entities.size();i++) {
			Entity entity = (Entity) entities.get(i);
			
			if (entity instanceof AlienEntity) {
				entity.setHorizontalMovement(entity.getHorizontalMovement() * 1.02);
				//daca mai exista extraterestrii pe ecran, mareste viteza cu 2% pentru 
				//fiecare extraterestru ramas
			}
		}
	}
	
	public void tryToFire() {
		//incercarea de a lansa un proiectil din partea jucatorului
		
		if (System.currentTimeMillis() - lastFire < firingInterval) {
			return;//verifica daca jucatorul a asteptat destul inainte sa trage
		}
		
		lastFire = System.currentTimeMillis();
		ShotEntity shot = new ShotEntity(this,"sprites/shot.png",ship.getX()+10,ship.getY()-30);
		entities.add(shot);
		//daca a asteptat destul, trage si memoreaza momentul tragerii
	}
	
	public void alienFire() {
		//incercarea de a lansa un proiectil din partea extraterestrilor
		for (int i=0;i<entities.size();i++) {
			Entity entity = (Entity) entities.get(i);
			
			if (entity instanceof AlienEntity) {
				if (System.currentTimeMillis() - lastalienFire < alienfireInteval) {
					return;//verifica daca extraterestrul a asteptat destul inainte sa traga
				}
				Random rand = new Random();
				int a = rand.nextInt(25);//fiecare extraterestru are o sansa de 1 in 25 de a trage
				lastalienFire = System.currentTimeMillis();
				BombEntity bomb = new BombEntity(this,"sprites/bomb.png",entity.getX()+(a*60),entity.getY()+(a*12));
				entities.add(bomb);
				//daca a asteptat destul, trage si memoreaza momentul tragerii
				
			}
		}
	}

	public void gameLoop() {
		//bucla principala a jocului; este activa pe toata durata jocului;
		//este responsabila pentru miscarea si generarea entitatilor;
		//updateaza evenimentele si verifica input-ul;
		long lastLoopTime = System.currentTimeMillis();
		
		while (gameRunning) {
			//mentine bucla pana la sfarsitul jocului;
			
			long delta = System.currentTimeMillis() - lastLoopTime;
			lastLoopTime = System.currentTimeMillis();
			//calculeaza cat timp a trecut de la ultimul update;
			
			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
			g.setColor(Color.black);
			g.fillRect(0,0,800,600);
			//goleste background-ul
			
			if (!waitingForKeyPress) {
				for (int i=0;i<entities.size();i++) {
					Entity entity = (Entity) entities.get(i);
					entity.move(delta);//solicita fiecarei entitati sa se deplaseze in aceasta bucla
				}
			}
			
			for (int i=0;i<entities.size();i++) {
				Entity entity = (Entity) entities.get(i);
				entity.draw(g);//regenereaza entitatile in aceasta bucla
			}
			
			for (int p=0;p<entities.size();p++) {
				//aceasta metoda se ocupa de coliziuni
				for (int s=p+1;s<entities.size();s++) {
					Entity me = (Entity) entities.get(p);
					Entity him = (Entity) entities.get(s);
					//inante de coliziune, compara entitatile
					
					if (me.collidesWith(him)) {
						me.collidedWith(him);
						him.collidedWith(me);
						//daca coliziunea a avut loc, notifica fiecare entitate de coliziune
					}
				}
			}
			
			entities.removeAll(removeList);
			removeList.clear();//sterge entitatile ce trebuie eliminate

			if (logicRequiredThisLoop) {
				for (int i=0;i<entities.size();i++) {
					Entity entity = (Entity) entities.get(i);
					entity.doLogic();
					//daca un eveniment a indicat ca logica trebuie revizuita, solicita ca logica entitatilor sa fie reconsiderata
				}
				
				logicRequiredThisLoop = false;
			}
			
			if (waitingForKeyPress) {
				g.setColor(Color.white);
				g.drawString(message,(800-g.getFontMetrics().stringWidth(message))/2,250);
				g.drawString("Apasa orice tasta",(800-g.getFontMetrics().stringWidth("Apasa orice tasta"))/2,300);
				//jocul asteapta input
			}
			
			g.dispose();
			strategy.show();
			//goleste buffer-ul
			
			ship.setHorizontalMovement(0);//presupunem ca nava nu se misca
			
			if ((leftPressed) && (!rightPressed)) {
				ship.setHorizontalMovement(-moveSpeed);//muta nava la stanga
			} 
			else if ((rightPressed) && (!leftPressed)) {
				ship.setHorizontalMovement(moveSpeed);//muta nava la dreapta
			}
			
			if (firePressed) {
				tryToFire();//daca dam comanda sa traga, va incerca sa traga
			}
			
			alienFire();//extraterestrii vor trage la intamplare
			
			try { Thread.sleep(10); } catch (Exception e) {} //mica pauza la finalul buclei
		}
	}
	
	private class KeyInputHandler extends KeyAdapter {
		//aceasta clasa gestioneaza input-ul utilizatorului
		
		private int pressCount = 1;//numarul de taste pe care trebuie sa le apasam ca sa incepem jocul
		
		public void keyPressed(KeyEvent e) {
			//o tasta a fost apasata
			if (waitingForKeyPress) {
				return;
			}
			
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				leftPressed = true;
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				rightPressed = true;
			}
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				firePressed = true;
			}
		} 
		
		public void keyReleased(KeyEvent e) {
			//o tasta a fost eliberata
			if (waitingForKeyPress) {
				return;
			}
			
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				leftPressed = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				rightPressed = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				firePressed = false;
			}
		}

		public void keyTyped(KeyEvent e) {
			//o tasta a fost "tastata" (apasata+eliberata)
			if (waitingForKeyPress) {
				if (pressCount == 1) {
					waitingForKeyPress = false;
					startGame();//daca tasta a fost tastata, incepe jocul
					pressCount = 0;
				} else {
					pressCount++;
				}
			}
			
			if (e.getKeyChar() == 27) {
				System.exit(0);//daca am apasat Esc, inchide jocul
			}
		}
	}
	
	public static void main(String argv[]) {
		Game g =new Game();//creeaza o noua instanta a clasei Game

		g.gameLoop();//incepe bucla jocului
	}
}
