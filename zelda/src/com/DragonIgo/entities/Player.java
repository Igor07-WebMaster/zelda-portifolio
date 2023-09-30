package com.DragonIgo.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.DragonIgo.Main.Game;
import com.DragonIgo.Main.Sound;
import com.DragonIgo.graficos.Spritesheet;
import com.DragonIgo.graficos.UI;
import com.DragonIgo.world.Camara;
import com.DragonIgo.world.World;

public class Player extends Entity{

	public static boolean right,left,down,up;
	public double spd=2;
	public int right_dir=0,left_dir=1;
	public int dir=right_dir;
	private boolean moved=false;
	public double life=100,maxLife=100;
	public boolean shooted=false;
	public boolean mouseShooted=false;
	public int mx,my;
	
	
	
	private int frames=0,maxFrames=15,index=0,maxIndex=3;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	private BufferedImage playerDamage;
	public int anmo=0;
	public boolean isDamaged=false;
	private int damagedFrames=0;
	public boolean hasGun=false;
	
	public Player(int x, int y, int width, int height, BufferedImage Sprite) {
		super(x, y, width, height, Sprite);
		
		leftPlayer = new BufferedImage[4];
		rightPlayer =new BufferedImage[4];
		playerDamage=Game.spritesheet.getSprite(0, 16, 16, 16);
		for(int i =0; i <4;i++) {
			rightPlayer[i] = Game.spritesheet.getSprite(32+(i*16), 0, 16, 16);
		}
		for(int i = 0;i<4;i++) {
			leftPlayer[i] = Game.spritesheet.getSprite(32+(i*16), 16, 16, 16);
		}
	}
	
	
	
	public void tick() {
		moved=false;
		if(right&& World.isFree((int) (x+spd),y)) {
			moved=true;
			dir=right_dir;
			x+=spd;
		}else if(left&& World.isFree((int) (x-spd),y)) {
			moved=true;
			dir=left_dir;
			x-=spd;
		}
		if(down && World.isFree(x,(int) (y+spd))) {
			moved=true;
			y+=spd;
		}else if(up && World.isFree(x,(int) (y-spd))) {
			moved=true;
			y-=spd;
		}
		
		if(moved) {
			frames++;
			if(frames==maxFrames) {
				frames=0;
				index++;
				if(index>maxIndex) {
					index=0;
				}
			}
		}
		checkItems();
		checkBullet();
		checkGun();
		
		if(isDamaged) {
			damagedFrames++;
			if(this.damagedFrames==16) {
				damagedFrames=0;
				isDamaged=false;
			}
			
		}
		
		if(shooted && hasGun) {
			Sound.shootEffect.play();
			shooted=false;
			int px=9;
			int py=4;
			int dx=0;
			if(anmo>0) {
				anmo--;
				
				if(dir==right_dir) {
					px=6;
					dx=1;
				}else {
					py=5;
					dx=-1;
				}
				Shoot bullet = new Shoot(this.getX()+px,this.getY()+py,3,3,null,dx,0);
				Game.bullets.add(bullet);
			}
		}
		
		if(mouseShooted) {
			Sound.shootEffect.play();
			double angle=0;
			mouseShooted=false;
			
			if(hasGun && anmo>0) {
				anmo--;
				
				int px=18;
				int py=4;
				
				
				if(dir==right_dir) {
					px=16;
					angle=Math.atan2( my - ( Game.player.getY()+py-Camara.y),mx-( Game.player.getX()+px-Camara.x) );
				}else {
					px=5;
					py=5;
					angle=Math.atan2( my - ( Game.player.getY()+py-Camara.y),mx-( Game.player.getX()+px-Camara.x) );
				}
				
				double dx=Math.cos(angle);
				double dy=Math.sin(angle);
				
				Shoot bullet = new Shoot(this.getX()+px,this.getY()+py,3,3,null,dx,dy);
				Game.bullets.add(bullet);
				}
			}
        
		
		if(life <=0) {
			life=0;
			Game.gameState="Game_Over";
			
		}
		
		updateCamara();
	}
	
	public void updateCamara() {
		Camara.x=Camara.clamp(this.getX()-(Game.WIDTH/2),0,World.WIDTH*16-Game.WIDTH);
		Camara.y=Camara.clamp(this.getY()-(Game.HEIGHT/2),0,World.HEIGHT*16-Game.HEIGHT);
	}
	
	public void checkGun() {
		for(int i =0;i<Game.entities.size();i++) {
			Entity atual=Game.entities.get(i);
			if(atual instanceof Weapon ) {
				if(Entity.isColidding(this, atual)) {
					hasGun=true;
					//System.out.println("Pegou a arma ");
					Game.entities.remove(atual);
					Game.weapons.remove(atual);
					return;
				}
			}
		}
	}
	
	public void checkBullet() {
		for(int i =0;i<Game.entities.size();i++) {
			Entity atual=Game.entities.get(i);
			if(atual instanceof Bullet ) {
				if(Entity.isColidding(this, atual)) {
					anmo+=100;
					//System.out.println("Munição: "+anmo);
					Game.entities.remove(atual);
					return;
				}
			}
		}
	}
	
	public void checkItems() {
		for(int i =0;i<Game.entities.size();i++) {
			Entity atual=Game.entities.get(i);
			if(atual instanceof LifePack ) {
				if(Entity.isColidding(this, atual)) {
					life+=10;
					if(life>100)
						life=100;
					Game.entities.remove(atual);
					return;
				}
			}
		}
	}
	
	public void render(Graphics g) {
		if(isDamaged==false) {
			if(dir==right_dir) {
				g.drawImage(rightPlayer[index],this.getX()-Camara.x,this.getY()-Camara.y,null);
				if(hasGun) {
					//direita
					g.drawImage(Entity.WEAPON_EN, this.getX()-Camara.x,this.getY()-Camara.y, null);
				}
			}else if(dir==left_dir) {
				g.drawImage(leftPlayer[index],this.getX()-Camara.x,this.getY()-Camara.y,null);
				if(hasGun) {
					//esquerda
					g.drawImage(WEAPONLEFT_EN, this.getX()-Camara.x,this.getY()-Camara.y, null);
				}
			}
		}else {
			g.drawImage(playerDamage, this.getX()-Camara.x,this.getY()-Camara.y,null);
			if(hasGun) {
				if(dir==left_dir) {
					g.drawImage(GUN_DAMAGE_LEFT, this.getX()-Camara.x,this.getY()-Camara.y, null);
				}else {
					g.drawImage(GUN_DAMAGE_RIGHT, this.getX()-Camara.x,this.getY()-Camara.y, null);
				}
			}
		}
		
		
	}
	
}

