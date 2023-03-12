package joc.spaceinvaders;

public class BombEntity extends Entity {
	//O entitate ce reprezinta proiectilul lansat de extraterestru
	
	private double moveSpeed = 150;//viteza cu care proiectilul se misca vertical
	
	private Game game;//jocul in care entitatea exista
	
	private boolean used = false;//va deveni "true" cand proiectilul loveste jucatorul
	
	public BombEntity(Game game,String sprite,int x,int y) {
		//creeaza entitatea bomb
		super(sprite,x,y);//sprite = sprite-ul ce trebuie afisat pentru proiectil
						  //x = locatia initiala pe axa x a proiectilului
						  //y = locatia initiala pe axa y a proiectilului
		
		this.game = game;//jocul in care aceasta entitate a fost creata
		
		dy = moveSpeed;
	}

	public void move(long delta) {
		//solicita ca aceasta entitate sa se miste pe baza timpului scurs
		//delta = timpul care a trecut de la ultima miscare
		
		super.move(delta);//continua miscarea normala
		
		if (y < -100) {
			game.removeEntity(this);//daca proiectilul iese de pe ecran, se auto-elimina
		}
	}
	
	public void collidedWith(Entity other) {
		//notifica jocul ca aceasta entitate a intrat in coliziune cu alta entitate
		//other = entitatea cu care a intrat in coliziune
		
		if (used) {
			return;//previne duble eliminari; daca deja a lovit ceva, nu mai intra in coliziune
		}
		
		if (other instanceof ShipEntity) {
			//daca a lovit nava jucatorului, o distruge
			game.notifyDeath();//notifica jucatorul ca a pierdut
		}
	}
}