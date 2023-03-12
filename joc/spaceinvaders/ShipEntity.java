package joc.spaceinvaders;

public class ShipEntity extends Entity {
	//O entitate ce reprezinta nava jucatorului
	
	private Game game;//jocul in care aceasta entitate exista
	
	public ShipEntity(Game game,String ref,int x,int y) {
		//creeaza entitatea ship
		super(ref,x,y);//ref = sprite-ul ce trebuie afisat pentru nava
						//x = locatia initiala pe axa x a navei
						//y = locatia initiala pe axa y a navei
		
		this.game = game;// jocul in care aceasta entitate a fost creata
	}
	
	public void move(long delta) {
		//solicita ca aceasta entitate sa se miste pe baza timpului scurs
		//delta = timpul care a trecut de la ultima miscare
		
		if ((dx < 0) && (x < 10)) {
			return;//daca am ajuns in partea stanga a ecranului, opreste miscarea in acea directie
		}
		
		if ((dx > 0) && (x > 750)) {
			return;//daca am ajuns in partea dreapta a ecranului, opreste miscarea in acea directie
		}
		
		super.move(delta);//continua miscarea normala
	}
	
	public void collidedWith(Entity other) {
		//notifica jocul ca aceasta entitate a intrat in coliziune cu alta entitate
		//other = entitatea cu care a intrat in coliziune
	
		if (other instanceof AlienEntity) {
			game.notifyDeath();//daca un alien intra in coliziune cu nava, jucatorul a pierdut
		}
		if (other instanceof BombEntity) {
			game.notifyDeath();//daca nava e lovita de o bomba, jucatorul a pierdut
		}
	}
}