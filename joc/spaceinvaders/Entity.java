package joc.spaceinvaders;

import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class Entity {
	//O entitate reprezinta orice element care apare in joc.
	//Entitatea este responsabila pentru rezolvarea coliziunilor si a miscarilor pe baza unui
	//set de proprietati definite fie de subclasa, fie din exterior.
	
	protected double x;//locatia curenta pe axa x a acestei entitati
	
	protected double y;//locatia curenta pe axa y a acestei entitati
	
	protected Sprite sprite;//sprite-ul ce reprezinta aceasta entitate
	
	protected double dx;//viteza curenta cu care entitatea se misca orizintal (pixeli/secunda)
	
	protected double dy;//viteza curenta cu care entitatea se misca vertical (pixeli/secunda)
	
	//pentru pozitii am folosit double si nu int deoarece double este mai precis in a numara pixelii in timpul miscarii
	
	private Rectangle me = new Rectangle();//patrulaterul utilizat pentru aceasta entitate in timpul rezolvarii coliziunilor
	
	private Rectangle him = new Rectangle();//patrulaterul utilizat pentru alte entitati in timpul rezolvarii coliziunilor
	
	public Entity(String ref,int x,int y) {
		//creeaza o entitate pe baza unei imagini si a unei locatii
		this.sprite = SpriteStore.get().getSprite(ref);//sprite-ul ce trebuie afisat pentru entitate
		this.x = x;//locatia initiala pe axa x a entitatii
		this.y = y;//locatia initiala pe axa y a entitatii
	}
	
	public void move(long delta) {
		//solicita ca entitatea sa se miste pe baza unui interval de timp
		//delta = intervalul de timp in milisecunde
		
		x += (delta * dx) / 1000;
		y += (delta * dy) / 1000;
		//updateaza locatia entitatii pe baza vitezei acesteia
	}
	
	public void setHorizontalMovement(double dx) {
		//seteaza viteza orizontala a entitatii
		this.dx = dx;//viteza orizontala a acestei entitati (pixeli/secunda)
	}

	public void setVerticalMovement(double dy) {
		//seteaza viteza verticala a entitatii
		this.dy = dy;//viteza verticala a acestei entitati (pixeli/secunda)
	}
	
	public double getHorizontalMovement() {
		//returneaza viteza orizontala a entitatii
		return dx;
	}

	public double getVerticalMovement() {
		//returneaza viteza verticala a entitatii
		return dy;
	}
	
	public void draw(Graphics g) {
		//genereaza entitatea in contextul grafic
		sprite.draw(g,(int) x,(int) y);//g = contextul grafic in care este generata entitatea
	}
	
	public void doLogic() {
		//rezolva logica asociata entitatii
		//aceasta metoda va fi apelata periodic in timpul jocului
	}
	
	public int getX() {
		return (int) x;//returneaza locatia pe axa x a entitatii
	}

	public int getY() {
		return (int) y;//returneaza locatia pe axa y a entitatii
	}
	
	public boolean collidesWith(Entity other) {
		//verifica daca entitatea a intrat in coliziune cu alta entitate
		//other = entitatea cu care a intrat in coliziune
		me.setBounds((int) x,(int) y,sprite.getWidth(),sprite.getHeight());
		him.setBounds((int) other.x,(int) other.y,other.sprite.getWidth(),other.sprite.getHeight());

		return me.intersects(him);//va returna "true" daca a avut loc coliziunea
	}
	
	public abstract void collidedWith(Entity other);//notificarea ca o coliziune a avut loc
}