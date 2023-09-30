package com.DragonIgo.Main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.DragonIgo.world.World;

public class Menu {
	
	public String[] options={"novo jogo","Carregar jogo","Sair"};
	
	public int currentOption=0;
	public int maxOption=options.length-1;
	public static boolean down,up,enter,pause=false;
	public boolean saveExists=false;
	public boolean saveGame=false;
	

	public void tick() {
		File file =new File("save.txt");
		if(file.exists()) {
			saveExists=true;
		}else {
			saveExists=false;
		}
		if(up) {
			up=false;
			currentOption--;
			if(currentOption < 0) {
				currentOption=maxOption;
			}
		}
		if(down) {
			down=false;
			currentOption++;
			if(currentOption > maxOption) {
				currentOption=0;
			}
		}
		if(enter) {
			enter=false;
			if(options[currentOption]=="novo jogo"||options[currentOption]=="Continuar") {
				Game.gameState="NORMAL";
				pause=false;
				file=new File("save.txt");
				file.delete();
			}else if(options[currentOption]=="Sair") {
				System.exit(1);
			}else if(options[currentOption]=="Carregar jogo") {
				file =new File("save.txt");
				if(file.exists()) {
					String saver=carregarJogo(10);
					applySave(saver);
					
				}
			}
		}
	}
	
	public static void applySave(String str) {
		String[] spl=str.split("/");
		for(int i=0;i<spl.length;i++) {
			String[] spl2=spl[i].split(":");
			switch(spl2[0]) {
			case "level":
				World.restartGame("level"+spl2[1]+".png");
				Game.gameState="NORMAL";
				pause=false;
				break;
			}
		}
	}
	
	public static String carregarJogo(int encode) {
		String line="";
		File file=new File("save.txt");
		if(file.exists()) {
			try {
				String singleLine=null;
				BufferedReader reader=new BufferedReader(new FileReader("save.txt"));
				try {
					while((singleLine=reader.readLine()) != null) {
						String[] trans=singleLine.split(":");
						char[] val=trans[1].toCharArray();
						trans[1]="";
						for(int i=0;i<val.length;i++) {
							val[i]-=encode;
							trans[1]+=val[i];
						}
						line+=trans[0];
						line+=":";
						line+=trans[1];
						line+="/";
						
					}
				}catch(IOException e) {
					e.printStackTrace();
				}
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			

		}
		
		return line;
	}
	
	public static void saveGame(String[] val1,int[] val2,int encode) {
		BufferedWriter write=null;
		
		try {
			write=new BufferedWriter(new FileWriter("save.txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(int i=0;i<val1.length;i++) {
			String current=val1[i];
			current+=":";
			char[] value=Integer.toString(val2[i]).toCharArray();
			for(int n=0;n<value.length;n++) {
				value[n]+=encode;
				current+=value[n];
			}
			
			try {
				write.write(current);
				if(i<val1.length-1) {
					write.newLine();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		try {
			write.flush();
			write.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void render(Graphics g) {
		
		Graphics2D g2=(Graphics2D) g;
		
		g2.setColor(new Color(0,0,0,100));
		g2.fillRect(0, 0, Game.WIDTH*Game.SCALE, Game.HEIGHT*Game.SCALE);
		g.setColor(Color.WHITE);
		g.setFont(new Font("arial",Font.BOLD,56));
		g.drawString(">game #1<", (Game.WIDTH*Game.SCALE)/2 -130, (Game.WIDTH*Game.SCALE)/2 -250);
		//opções do menu do jogo
		g.setColor(Color.WHITE);
		g.setFont(new Font("arial",Font.BOLD,30));
		if(pause==false)
			g.drawString("novo jogo", (Game.WIDTH*Game.SCALE)/2 -70, (Game.WIDTH*Game.SCALE)/2 -130);
		else
			g.drawString("Continuar", (Game.WIDTH*Game.SCALE)/2 -70, (Game.WIDTH*Game.SCALE)/2 -130);
		g.drawString("Carregar jogo", (Game.WIDTH*Game.SCALE)/2 -110, (Game.WIDTH*Game.SCALE)/2 -90);
		g.drawString("Sair", (Game.WIDTH*Game.SCALE)/2 -50, (Game.WIDTH*Game.SCALE)/2 -50);
		if(options[currentOption]=="novo jogo") {
			g.drawString(">", (Game.WIDTH*Game.SCALE)/2 -100, (Game.WIDTH*Game.SCALE)/2 -130);
		}else if(options[currentOption]=="Carregar jogo") {
			g.drawString(">", (Game.WIDTH*Game.SCALE)/2 -130, (Game.WIDTH*Game.SCALE)/2 -90);
		}else if(options[currentOption]=="Sair") {
			g.drawString(">", (Game.WIDTH*Game.SCALE)/2 -70, (Game.WIDTH*Game.SCALE)/2 -50);
		}
	}
}
