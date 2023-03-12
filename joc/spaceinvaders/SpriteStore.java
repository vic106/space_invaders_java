package joc.spaceinvaders;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class SpriteStore {
	//Aceasta clasa este un manager pentru sprite-urile din joc.
	
	private static SpriteStore single = new SpriteStore();//Singura instantiere a acestei clase.
	
	public static SpriteStore get() {
		return single;//Returneaza singura instantiere a clasei.
	}
	
	private HashMap sprites = new HashMap();//HashMap-ul (texturile) sprite-ului memorat in cache,de la referinta la instantiere.
	
	public Sprite getSprite(String ref) {
		//Returneaza un sprite din Store.
		if (sprites.get(ref) != null) {
			//Daca avem deja sprite-ul in cache, returneaza versiunea existenta.
			return (Sprite) sprites.get(ref);//ref = referinta la imaginea ce va fi folosita pentru sprite
		}
		
		BufferedImage sourceImage = null;//Altfel, va lua sprite-ul din loader.
		
		try {
			URL url = this.getClass().getClassLoader().getResource(ref);
			//Classloader.getResource() se asigura ca sprite-ul este luat din fisierul corect.
			if (url == null) {
				fail("Nu a fost gasita referinta: "+ref);
			}
			
			sourceImage = ImageIO.read(url);//ImageIO citeste imaginea.
		} catch (IOException e) {
			fail("Eroare de incarcare: "+ref);
		}
		
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		Image image = gc.createCompatibleImage(sourceImage.getWidth(),sourceImage.getHeight(),Transparency.BITMASK);
		//Creeaza o imagine de marimea potrivita in care se va stoca sprite-ul.
		
		image.getGraphics().drawImage(sourceImage,0,0,null);//Genereaza imaginea sursa in imaginea noastra.
		
		Sprite sprite = new Sprite(image);
		sprites.put(ref,sprite);//Creeaza un sprite, il adauga in cache si apoi il returneaza.
		
		return sprite;
	}
	
	private void fail(String message) {
		System.err.println(message);//Mesaj de eroare in cazul in care imaginea nu poate fi incarcata.
		System.exit(0);//Iese din joc.
	}
}