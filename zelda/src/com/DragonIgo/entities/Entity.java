package com.DragonIgo.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.DragonIgo.Main.Game;
import com.DragonIgo.world.Camara;

public class Entity {

	public static BufferedImage LIFEPACK_EN=Game.spritesheet.getSprite(6*16, 0, 16, 16);
	public static BufferedImage WEAPON_EN=Game.spritesheet.getSprite(7*16, 0, 16, 16);
	public static BufferedImage BULLET_EN=Game.spritesheet.getSprite(6*16, 16, 16, 16);
	public static BufferedImage ENEMY_EN=Game.spritesheet.getSprite(32, 32, 16, 16);
	public static BufferedImage ENEMY_FEEDBACK_EN=Game.spritesheet.getSprite(128, 16, 16, 16);
	public static BufferedImage WEAPONLEFT_EN=Game.spritesheet.getSprite(7*16+16, 0, 16, 16);
	public static BufferedImage GUN_DAMAGE_RIGHT=Game.spritesheet.getSprite(0, 64, 16, 16);
	public static BufferedImage GUN_DAMAGE_LEFT=Game.spritesheet.getSprite(16, 64, 16, 16);
	
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	
	private BufferedImage Sprite;
	
	private int maskx,masky,mwidth,mheight;
	
	public Entity(int x,int y,int width,int height,BufferedImage Sprite) {
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		this.Sprite=Sprite;
		
		this.maskx=0;
		this.masky=0;
		this.mwidth=width;
		this.mheight=height;
	}
	
	public void setMask(int maskx,int masky,int mwidth, int mheight) {
		this.maskx=maskx;
		this.masky=masky;
		this.mwidth=mwidth;
		this.mheight=mheight;
	}
	
	public void setX(int newX) {
		this.x=newX;
	}
	
	public void setY(int newY) {
		this.y=newY;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public void tick() {
		
	}
	
	public static boolean isColidding(Entity e1,Entity e2) {
		Rectangle e1Mask = new Rectangle(e1.getX()+e1.maskx,e1.getY()+e1.masky,e1.mwidth,e1.mheight);
		Rectangle e2Mask = new Rectangle(e2.getX()+e2.maskx,e2.getY()+e2.masky,e2.mwidth,e2.mheight);
		return e1Mask.intersects(e2Mask);
	}
	
	public void render(Graphics g) {
		g.drawImage(Sprite, this.getX()-Camara.x, this.getY()-Camara.y, null);
		
	}
	
}
