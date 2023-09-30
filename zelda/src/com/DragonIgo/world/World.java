package com.DragonIgo.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import com.DragonIgo.Main.Game;
import com.DragonIgo.entities.*;
import com.DragonIgo.graficos.Spritesheet;
import com.DragonIgo.graficos.UI;

public class World {

	public static Tile[] tiles;
	public static int WIDTH,HEIGHT;
	public Player player=new Player(0, 0, 0, 0, null);
	public static final int TILE_SIZE=16;
	
	public World(String path) {
		try {
			BufferedImage map=ImageIO.read(getClass().getResource(path));
			//pegando a quantidade de pixels
			int[] pixels = new int[map.getWidth()*map.getHeight()];
			tiles = new Tile[map.getWidth()*map.getHeight()];
			WIDTH=map.getWidth();
			HEIGHT=map.getHeight();
			//quantidade de paredes
			map.getRGB(0, 0,map.getWidth(),map.getHeight(),pixels,0,map.getWidth());
			
			for(int xx=0;xx<map.getWidth();xx++) {
				for(int yy=0;yy<map.getHeight();yy++) {
					int pixelAtual=pixels[xx+(yy*map.getHeight())];
					tiles[xx+(yy*WIDTH)]=new TileFloor(xx*16,yy*16,Tile.TILE_FLOOR);
					if(pixelAtual==0xFF000000) {
						tiles[xx+(yy*WIDTH)]=new TileFloor(xx*16,yy*16,Tile.TILE_FLOOR);
					}else if(pixelAtual==0xFFFFFFFF) {
						//parede
						tiles[xx+(yy*WIDTH)]=new WallTile(xx*16,yy*16,Tile.TILE_WALL);
					}else if(pixelAtual==0xFF4CFF00) {
						//Player
						Game.player.setX(xx*16);
						Game.player.setY(yy*16);
					}else if(pixelAtual == 0xFFFF0026) {
						//enemy
						Inimigo en =new Inimigo(xx*16,yy*16,16,16,Entity.ENEMY_EN);
						Game.entities.add(en);
						Game.enemies.add(en);
						
					}else if(pixelAtual== 0xFF4800FF) {
						//weapon
						
						Game.entities.add(new Weapon(xx*16,yy*16,16,16,Entity.WEAPON_EN));
					}else if(pixelAtual == 0xFFFF00DC) {
						//life pack
						LifePack pack=new LifePack(xx*16,yy*16,16,16,Entity.LIFEPACK_EN);
						Game.entities.add(pack);
					}else if(pixelAtual == 0xFFFFD800) {
						//BULLEt
						Game.entities.add(new Bullet(xx*16,yy*16,16,16,Entity.BULLET_EN ));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	
	}
	
	public static void restartGame(String level) {
		Game.entities.clear();
		Game.enemies.clear();
		Game.ui=new UI();
		Game.image = new BufferedImage(Game.WIDTH,Game.HEIGHT,BufferedImage.TYPE_INT_RGB);
		Game.entities = new ArrayList<Entity>();
		Game.enemies = new ArrayList<Inimigo>();
		Game.spritesheet = new Spritesheet("/Spritesheet.png");
		Game.player = new Player(0,0,16,16,Game.spritesheet.getSprite(32, 0, 16, 16));
		Game.entities.add(Game.player);
		Game.world = new World("/"+level);
		return;
	}
	
	public static boolean isFree(int xnext, int ynext) {
		int x1=xnext/TILE_SIZE;
		int y1=ynext/TILE_SIZE;
		
		int x2=(xnext+TILE_SIZE-1)/TILE_SIZE;
		int y2=ynext/TILE_SIZE;
		
		int x3=xnext/TILE_SIZE;
		int y3=(ynext+TILE_SIZE-1)/TILE_SIZE;
		
		int x4=(xnext+TILE_SIZE-1)/TILE_SIZE;
		int y4=(ynext+TILE_SIZE-1)/TILE_SIZE;
		return !((tiles[x1+(y1*World.WIDTH)] instanceof WallTile)  ||
				(tiles[x2+(y2*World.WIDTH)] instanceof WallTile)  ||
				(tiles[x3+(y3*World.WIDTH)] instanceof WallTile)  ||
				(tiles[x4+(y4*World.WIDTH)] instanceof WallTile));
		
	}
	
	
	
	public void render(Graphics g) {
		int xStart=Camara.x>>4;
		int yStart=Camara.y>>4;
		
		int xFinal=xStart+(Game.WIDTH>>4);
		int yFinal=yStart+(Game.HEIGHT>>4);
		
		for(int xx=xStart;xx<= xFinal;xx++) {
			for(int yy= yStart; yy<= yFinal;yy++) {
				if(xx<0||yy<0||xx>=WIDTH||yy>=HEIGHT)
					continue;
				Tile tile=tiles[xx+(yy*WIDTH)];
				tile.render(g);
			}
		}
	}
	
}
