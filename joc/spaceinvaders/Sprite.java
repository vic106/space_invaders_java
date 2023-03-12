package joc.spaceinvaders;

import java.awt.Graphics;
import java.awt.Image;

public class Sprite {
	//Un sprite este o imagine ce poate fi afisata pe ecran; nu contine informatii (e.g. locatie).
	//Acest lucru ne permite sa folosim un singur sprite in locuri diferite fara a fi nevoiti
	//sa stocam mai multe copii ale imaginii.
	
	private Image image;//imaginea ce va fi generata pentru acest sprite
	
	public Sprite(Image image) {
		//Creeaza un nou sprite pe baza imaginii.
		this.image = image;//Imaginea ce reprezinta sprite-ul.
	}
	
	public int getWidth() {
		return image.getWidth(null);//Returneaza latimea sprite-ului generat.
	}

	public int getHeight() {
		return image.getHeight(null);//Returneaza inaltimea sprite-ului generat.
	}
	
	public void draw(Graphics g,int x,int y) {
		//Genereaza sprite-ul in contextul grafic furnizat.
		g.drawImage(image,x,y,null);//g = contextul grafic
									//x = locatia pe axa x la care sprite-ul este generat
									//y = locatia pe axa y la care sprite-ul este generat
	}
}