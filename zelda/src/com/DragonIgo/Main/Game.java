package com.DragonIgo.Main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
//import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import com.DragonIgo.entities.*;
import com.DragonIgo.graficos.Spritesheet;
import com.DragonIgo.graficos.UI;
import com.DragonIgo.world.*;


public class Game extends Canvas implements Runnable,KeyListener,MouseListener{

	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	private Thread thread;
	public static boolean isRunning;
	public static final int WIDTH = 320,HEIGHT = 240,SCALE = 2;
	private int CUR_LEVEL=1,MAX_LEVEL=2;
	public static BufferedImage image;
	public static List<Entity> entities;
	public static List<Inimigo> enemies;
	public static Spritesheet spritesheet;
	public static List<Shoot> bullets;
	public static List<Weapon> weapons;
	
	public static World world;
	public static Player player;
	public static Random rand;
	public static UI ui;
	public static String gameState="menu";
	private boolean showMessageGameOver=false;
	private int FramesGameOver=0;
	private boolean restartGame=false;
	public Menu menu;
	public boolean saveGame=false;
	
	public Game() {
		Sound.backGround.loop();
		rand=new Random();
		addKeyListener(this);
		addMouseListener(this);
		setPreferredSize(new Dimension(WIDTH*SCALE,HEIGHT*SCALE));
		initFrame();
		//Inicializando objetos.
		ui=new UI();
		image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Inimigo>();
		bullets = new ArrayList<Shoot>();
		weapons = new ArrayList<Weapon>();
		spritesheet = new Spritesheet("/Spritesheet.png");
		player = new Player(0,0,16,16,Spritesheet.getSprite(32, 0, 16, 16));
		entities.add(player);
		world = new World("/level1.png");
		menu = new Menu();
		
	}
	
	public void initFrame() {
		frame = new JFrame();
		
		frame.setTitle("Game #02");
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public synchronized void  start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}
	
	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		Game game = new Game();
		game.start();
	}
	
	public void tick() {

		if(gameState=="NORMAL") {
			if(this.saveGame) {
				this.saveGame=false;
				String[] opt1={"level"};
				int[] opt2= {this.CUR_LEVEL};
				Menu.saveGame(opt1, opt2, 10);
				System.out.println("jogo salvo");
			}
			this.restartGame=false;
			for(int i=0;i < entities.size();i++) {
				Entity e = entities.get(i);
				e.tick();
			}
			
			for(int i=0 ;i<bullets.size();i++) {
				bullets.get(i).tick();
			}
			
			if(enemies.size()==0) {
				CUR_LEVEL++;
				if(CUR_LEVEL>MAX_LEVEL) {
					CUR_LEVEL=1;
				}
				String newWorld="level"+CUR_LEVEL+".png";
				World.restartGame(newWorld);
			}
		}else if(gameState == "Game_Over") {
				this.FramesGameOver++;
				if(this.FramesGameOver == 20) {
					this.FramesGameOver =0;
					if(this.showMessageGameOver) 
						this.showMessageGameOver=false;
					else
						this.showMessageGameOver=true;
				}
				if(restartGame) {
					this.restartGame=false;
					this.gameState="NORMAL";
					CUR_LEVEL=1;
					String newWorld="level"+CUR_LEVEL+".png";
					World.restartGame(newWorld);
				}
		}else if(gameState =="menu") {
			//toda a lógica do menu sera feito aqui.
			menu.tick();
		}
		
		
	}
	
	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
        Graphics g = bs.getDrawGraphics();
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH*SCALE, HEIGHT*SCALE);
		
		g = image.getGraphics();
		world.render(g);
		
		
		
		for(int i=0;i < entities.size();i++) {
			Entity e = entities.get(i);
			e.render(g);
		}
		
		for(int i=0 ;i<bullets.size();i++) {
			bullets.get(i).render(g);
		}
		
		ui.render(g);
		
		g.dispose();
		g = bs.getDrawGraphics();
		g.drawImage(image,0,0,WIDTH*SCALE,HEIGHT*SCALE,null);
		g.setColor(Color.WHITE);
		g.setFont(new Font("arial",Font.BOLD,20));
		g.drawString("Municao: "+player.anmo, 520, 20);
		
		if(gameState=="Game_Over") {
			Graphics2D g2=(Graphics2D) g;
			
			g2.setColor(new Color(0,0,0,100));
			g2.fillRect(0, 0, WIDTH*SCALE, HEIGHT*SCALE);
			
			g.setColor(Color.WHITE);
			g.setFont(new Font("arial",Font.BOLD,36));
			g.drawString("Game Over", 230, 240);
			g.setColor(Color.WHITE);
			g.setFont(new Font("arial",Font.BOLD,32));
			if(showMessageGameOver)
				g.drawString(">Pressione Enter para Reiniciar<", 100, 290);
		}
		if(gameState=="menu") {
			menu.render(g);
		}
		
		bs.show();
	}
	
	@Override
	public void run() {

		long lastTime = System.nanoTime();
		final double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		requestFocus();
		
		while(isRunning) {
			long now = System.nanoTime();
			delta+= (now - lastTime) / ns;
			lastTime = now;
			if(delta >= 1) {
				tick();
				render();
				frames++;
				delta--;
			}
			if(System.currentTimeMillis() - timer >= 1000) {
				System.out.println("FPS: "+frames);
				frames = 0;
				timer+=1000;
			}
		}
		stop();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT||
				e.getKeyCode()==KeyEvent.VK_D) {
			player.right=true;
		}else if(e.getKeyCode()==KeyEvent.VK_LEFT||
				e.getKeyCode()==KeyEvent.VK_A) {
			player.left=true;
		}
		if(e.getKeyCode()==KeyEvent.VK_UP||
				e.getKeyCode()==KeyEvent.VK_W) {
			player.up=true;
			if(gameState=="menu") {
				menu.up=true;
			}
		}else if(e.getKeyCode()==KeyEvent.VK_DOWN||
				e.getKeyCode()==KeyEvent.VK_S) {
			player.down=true;
			if(gameState=="menu") {
				menu.down=true;
			}
		}
		
		if(e.getKeyCode()==e.VK_K) {
			player.shooted=true;
		}
		
		if(e.getKeyCode()== KeyEvent.VK_ENTER) {
			this.restartGame=true;
			if(gameState=="menu") {
				Menu.enter=true;
			}
		}
		
		if(e.getKeyCode()==e.VK_ESCAPE) {
			gameState="menu";
			menu.pause=true;
		}
		
		if(e.getKeyCode()==e.VK_SPACE) {
			if(Game.gameState=="NORMAL")
				saveGame=true;
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT||
				e.getKeyCode()==KeyEvent.VK_D) {
			player.right=false;
		}else if(e.getKeyCode()==KeyEvent.VK_LEFT||
				e.getKeyCode()==KeyEvent.VK_A) {
			player.left=false;
		}
		if(e.getKeyCode()==KeyEvent.VK_UP||
				e.getKeyCode()==KeyEvent.VK_W) {
			player.up=false;
		}else if(e.getKeyCode()==KeyEvent.VK_DOWN||
				e.getKeyCode()==KeyEvent.VK_S) {
			player.down=false;
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {

		player.mouseShooted=true;
		player.mx=(e.getX()/SCALE);
		player.my=(e.getY()/SCALE);
		
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}