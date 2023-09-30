package com.DragonIgo.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.DragonIgo.Main.Game;
import com.DragonIgo.world.Camara;

public class Shoot extends Entity {
	
	private double dx,dy;
	private double spd=4;
	private int lifeGun=45,curlife=0;

	public Shoot(int x, int y, int width, int height, BufferedImage Sprite,double dx,double dy) {
		super(x, y, width, height, Sprite);
		this.dx=dx;
		this.dy=dy;
	}
	
	public void tick() {
		x+=dx*spd;
		y+=dy*spd;
		curlife++;
		if(lifeGun==curlife) {
			Game.bullets.remove(this);
		}
	}
	
	public void render(Graphics g) {
		g.setColor(Color.YELLOW);
		g.fillOval(this.getX()-Camara.x, this.getY()-Camara.y, width, height);
	}

}
