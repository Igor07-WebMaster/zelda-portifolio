package com.DragonIgo.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.DragonIgo.Main.Game;
import com.DragonIgo.Main.Sound;
import com.DragonIgo.world.Camara;
import com.DragonIgo.world.World;

public class Inimigo extends Entity {

	private double spd=1;
	
	private int frames=0,maxFrames=15,index=0,maxIndex=1;
	private BufferedImage[] sprites;
	private int maskx=8,masky=8,maskw=10,maskh=10;
	
	public double lifeEnemy=10,maxlifeEnemy=10;
	private boolean isDamaged=false;
	private int damageFrames=10,damageCurrent=0;
	
	public Inimigo(int x, int y, int width, int height, BufferedImage Sprite) {
		super(x, y, width, height, null);
		sprites = new BufferedImage[2];
		sprites[0] = Game.spritesheet.getSprite(32, 32, 16, 16);
		sprites[1] = Game.spritesheet.getSprite(32+16, 32, 16, 16);
		
	}
	
	public void tick() {
		if(this.isColiddingwithPlayer() == false) {
			if((int)x < Game.player.getX() && World.isFree((int)(x+spd),this.getY())
					&& !isColidding((int)(x+spd),this.getY())) {
				x+=spd;
			}else if((int)x > Game.player.getX() && World.isFree((int)(x-spd),this.getY())
					&& !isColidding((int)(x-spd),this.getY())) {
				x-=spd;
			}
			
			if((int)y < Game.player.getY() && World.isFree(this.getX(),(int)(y+spd))
					&& !isColidding(this.getX(),(int)(y+spd))) {
				y+=spd;
			}else if((int)y > Game.player.getY() && World.isFree(this.getX(),(int)(y+spd))
					&& !isColidding(this.getX(),(int)(y-spd))) {
				y-=spd;
				
			}
		}else {
			if(Game.rand.nextInt(100)<10) {
				Sound.hurtEffect.play();
				Game.player.life-=Game.rand.nextInt(3);
				//System.out.println("Vida: "+Game.player.life);
				Game.player.isDamaged=true;
				
			}
		}
		
		
			frames++;
			if(frames==maxFrames) {
				frames=0;
				index++;
				if(index>maxIndex) {
					index=0;
				}
			}
		ColiddingBullet();
		if(lifeEnemy <= 0) {
			destroySelf();
			return;
		}
		if(isDamaged) {
			damageCurrent++;
			if(damageFrames==damageCurrent) {
				damageCurrent=0;
				isDamaged=false;
			}
		}
	}
	
	public void destroySelf() {
		Game.enemies.remove(this);
		Game.entities.remove(this);
	}
	
	public void ColiddingBullet() {
		for(int i=0;i<Game.bullets.size();i++) {
			Entity e=Game.bullets.get(i);
			if(e instanceof Shoot) {
				if(Entity.isColidding(this, e)) {
					Sound.hurtEffect.play();
					lifeEnemy--;
					isDamaged=true;
					Game.bullets.remove(i);
					return;
				}
			}
		}
	}
	
	public boolean isColiddingwithPlayer() {
		Rectangle enemyCurrent = new Rectangle(this.getX()+maskx, this.getY()+masky,maskw,maskh);
		Rectangle player = new Rectangle(Game.player.getX(),Game.player.getY(),16,16);
		
		return enemyCurrent.intersects(player);
	}
	
	public boolean isColidding(int xnext, int ynext) {
		Rectangle enemyCurrent = new Rectangle(xnext,ynext, World.TILE_SIZE,World.TILE_SIZE);
		for(int i =0;i<Game.enemies.size();i++) {
			Inimigo e = Game.enemies.get(i);
			if(e==this)
				continue;
			
			Rectangle enemyTarget = new Rectangle(e.getX(), e.getY(), World.TILE_SIZE,World.TILE_SIZE);
			if(enemyCurrent.intersects(enemyTarget)) {
				return true;
				}
		}
		return false;
	}
	
	public void render(Graphics g) {
		if(!isDamaged) {
			
			g.drawImage(sprites[index], this.getX()-Camara.x, this.getY()-Camara.y, null);
		}
		else {
			g.drawImage(Entity.ENEMY_FEEDBACK_EN, this.getX()-Camara.x, this.getY()-Camara.y, null);
		}
		g.setColor(Color.red);
		g.fillRect(this.getX()-2-Camara.x, this.getY()-8-Camara.y, 20, 4);
		g.setColor(Color.green);
		g.fillRect(this.getX()-2-Camara.x, this.getY()-8-Camara.y, (int)((this.lifeEnemy/this.maxlifeEnemy)*20),4);
		
	}
	
}
