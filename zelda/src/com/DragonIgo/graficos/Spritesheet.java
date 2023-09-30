package com.DragonIgo.graficos;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Spritesheet {

	private static BufferedImage Spritesheet;
	
	public Spritesheet(String path) {
		try {
			Spritesheet= ImageIO.read(getClass().getResource(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	public static BufferedImage getSprite(int x,int y,int width,int height) {
		return Spritesheet.getSubimage(x, y, width, height);
		
	}
}
