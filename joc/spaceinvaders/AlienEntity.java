package joc.spaceinvaders;

public class AlienEntity extends Entity {
	//O entitate ce reprezinta unul dintre extraterestrii.
	
	private double moveSpeed = 75;//Viteza cu care un extraterestru se misca orizontal.
	
	private Game game;//Jocul in care entitatea exista.
	
	public AlienEntity(Game game,String ref,int x,int y) {
		//Creeaza entitatea alien
		super(ref,x,y);//ref = sprite-ul ce trebuie afisat pentru extraterestru
						//x = locatia initiala pe axa x a extraterestrului
						//y = locatia initiala pe axa y a extraterestrului
		
		this.game = game;// jocul in care aceasta entitate a fost creata
		dx = -moveSpeed;
	}

	public void move(long delta) {
		//solicita ca aceasta entitate sa se miste pe baza timpului scurs
		//delta = timpul care a trecut de la ultima miscare
		
		if ((dx < 0) && (x < 10)) {
			game.updateLogic();
			//daca a ajuns la partea din stanga a ecranului
			//incepe miscarea in cealalta directie
		}
		
		if ((dx > 0) && (x > 750)) {
			game.updateLogic();
			//daca a ajuns la partea din dreapta a ecranului 
			//incepe miscarea in cealalta directie
		}
		
		super.move(delta);//continua miscarea normala
	}
	
	public void doLogic() {
		//updateaza logica jocului in legatura cu extraterestrii
		dx = -dx;
		y += 10;
		//schimba miscarea orizontala si misca extraterestrii putin in jos
		if (y > 570) {
			game.notifyDeath();//daca extraterestrii ajung in partea de jos a ecranului, am pierdut
		}
	}
	
	public void collidedWith(Entity other) {
		//notifica jocul ca aceasta entitate a intrat in coliziune cu alta entitate
		//other = entitatea cu care a intrat in coliziune
		//coliziunile sunt tratate altundeva
	}
}