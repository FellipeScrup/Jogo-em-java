package com.blsstudios.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.blsstudios.entities.Player;
import com.blsstudios.world.World;

public class Menu {
	
	/*aqui eu estou falando quais são todas as minhas options*/
	public String[] options = {"novo jogo","carregar jogo","sair"};
	
	
	public int currentOption = 0;
	public int maxOption = options.length - 1;
	
	/*aqui são as posições aonde a minha seta vai mecher*/
	public boolean up, down, enter;
	/*o metodo de pausa do esc*/
	public static boolean pause = false;
	
	public static boolean saveExists = false;
	public static boolean saveGame = false; 
	
	public void tick () {
		File file = new File("save.txt");
	/*se o meu save existir ok, caso contrario, o jogo não tem nada no seu save*/
		if(file.exists()) {
			saveExists = true;
		}else {
			 saveExists =  false;
		}
		if(up) {
			up = false;
			/*esse metodo impede que o menu do jogo seja infito para cima*/
			currentOption--;
			if(currentOption < 0)
				currentOption =maxOption;
		}
		if (down) {
			down = false;
			currentOption++;
			if(currentOption > maxOption)
				currentOption = 0;
		}
		/*essa é uma detecção do java pra ver em que função eu apertei enter */
		if(enter) {
			//Sound.music.loop();
			enter = false; 
			/*o novo jogo tem duas funções, uma de inicializar o jogo e o outro de continuar o jogo, mas a continuar só aparece no menu de pausa*/
			if(options[currentOption] == "novo jogo" || options[currentOption] == "continuar") {
				Game.gameState = "NORMAL";
				pause = false;
				file = new File("save.txt");
				file.delete();	
			}else if(options[currentOption] == "carregar jogo") {
			file = new File("save.txt");
			/*aqui esuto dizendo se o file existir ele tem que rodar o meu jogo.*/
			if (file.exists()) {
				String saver = loadGame(10);
				applySave(saver);
			}
				
				
				/*aqui eu to setando o sair para fechar o meu game*/
			}else if(options[currentOption] == "sair") {
				
				System.exit(1);
		}
	}
}
	public static void applySave(String str) {
		/*aqui eu dou um split na barra por conta que eu vou ter outros valores*/
		String[] spl = str.split("/");
		for(int i = 0; i < spl.length; i++) {
		/*aqui eu dou um split nos dois pontos, porque eu quero saber o que foi salvo, e o
		 seu valor*/
			String[] spl2 = spl[i].split(":");
			switch(spl2[0])
			{
			/*a aqui eu estou lendo o meu level*/
			case "level":
				World.restartGame("level" + spl2[1] + ".png");
				Game.gameState = "NORMAL";
				pause = false;
				break;
			/*aqui eu estou lendo a minha vida*/	
			case "vida":
							/*essa função aqui está convertendo a nossa String em numero*/
				Game.player.life =Integer.parseInt(spl2[1]);
				break;
			/*e aqui estou lendo a munição do meu player*/	
			case "munição":
				Player.ammo =Integer.parseInt(spl2[1]);
				break;
			}
		}
	}
	
	public static String loadGame(int encode) {
		/*por padrão teremos um Strting line que começa vazio*/
		String line = "";
		/*Agora vamos fazer um verificação, do meu save*/
		File file = new File("save.txt");
		/*aqui eu to falando se o file existe, ele executa os TRY'S e CATCH's abaixo*/
		if(file.exists()) {
			try {
				/*essa aqui é uma linha individual que eu vou ler vai começa vazia*/
				String singleLine =null;
				/*esse é o meu leitor, ele vai ler o que tem dentro do meu file.*/
				BufferedReader reader = new BufferedReader(new FileReader("save.txt"));
				/*aqui em baixo eu estou de fato lendo o que está no meu reader*/
				try {
				/*aqui eu estou percorrendo um loop, apartir desse momento eu estou colocando
				o meu singleLine  para o meu read e para o meu readline, ou seje
				 eu vou ler linha por linha do meu arquivo de save, só vai continua rodando
				 esse loop caso valor dessa variavel não seja NULL(vazio)*/	
					 while((singleLine = reader.readLine()) != null) {
					/*(trans de transition) pq trans? pq eu to lendo a minha linha, valor
					 ou seja as fazes seram carregadas com base no trans*/	
						 String[] trans = singleLine.split(":");
						 /*aqui eu estou transformando tudo em Array*/
						 char[] val = trans[1].toCharArray();
						/*como já coloquei o valor dele ali encima eu vou dizer que o meu
						 trans é vazio*/
						 trans[1] = "";
						 for(int i = 0; i < val.length; i++) {
							 /*aqui eu estou decodificando o meu arquivo*/
							 val [i] -= encode;
							 trans[1] += val[i];
						 }	 
						 /* aqui eu estou construindo como se eu estivesse salavando.*/
						 line += trans[0];
						 line+=":";
						 line+=trans[1];
						 line+= "/";
					 }
				}catch(IOException e) {}
			}catch(FileNotFoundException e) {}
			
		}
		/*caso file n exista, ele retorna uma line vazia*/
		return line;
	}
	
	/*o val1 é um metodo escrito, vai dizer, o primeiro valor é a vida, aí no mesmo indice vida temos val2 é um metodo de numeros, que mostra
	o valor da vida do meu player, como aqui tem o valor do level também, e o encode é para criptografar o nosso save file, criptografar a o save do jogo*/
	public static void saveGame(String[] val1, int[] val2, int encode) {
		/*esse metodo é o que escreve o nosso save*/
		BufferedWriter write = null;
		 try {
			 /*esse aqui é o nosso metodo de escrita que salva o nosso jogo */
			 write = new BufferedWriter(new FileWriter("save.txt"));
			 
		 }catch(IOException e){
			 e.printStackTrace();
		 }
		 /*eu vou da um loop para fazer o val1 funcionar, o val1.lenghth, é o tamanho da String, eu to percorrendo todas as Strings que
		  eu passei para saber e construir com base nisso o meu arquivo de salvar o jogo*/
		 for(int i = 0; i < val1.length; i++) {
			 /**/
			 String current = val1[i];
			 /**/
			 current+=":";
			 /*aqui é um metodo aonde eu estou convertendo um numero inteiro para uma String, no val2, no mesmo indice do val1, que é um inteiro que eu to convertendo
			  e por fim eu converto tudo em um Array de char*/
			 char[] value = Integer.toString(val2[i]).toCharArray();
			 /*aqui eu to criando o loop do value, eu estou aplicando o Array de CHAR para eu aplicar o sistema de cripitografia do arquivo de save*/
			 for(int n = 0; n < value.length; n++) {
				/*se eu não colocasse essa linha aqui o usuario iria consegui abri o arquivo de save do jogo e o modificar, já desta maneira fica criptografado*/ 
				 value[n] += encode;
				 /*aqui to add a minha string do valor que eu criei no metodo acima */
				 current+=value[n];
			 }
			 try {
				 
				 write.write(current);
				 /**/
				 if( i < val1.length - 1)
					 write.newLine();
			 }catch(IOException e) {
				 
			 }
		 }
		 try {
			 /*esse são sistemas de segurança que depois que eu escrever o aruivo fechar*/
			  write.flush();
			  write.close();
			  
		 }catch(IOException e) {
			 
		 }
	}
	
	public void render(Graphics g) {
		g.setColor(new Color(0,0,0,0/*volta dps para 210*/));
		g.fillRect(0, 0, Game.WIDTH*Game.SCALE, Game.HEIGHT*Game.SCALE);
		g.setColor(Color.WHITE);
		g.setFont(new Font ("arial", Font.BOLD, 36));
		g.drawString("Projeto Melancia. alpha 0.0.4", Game.WIDTH*Game.SCALE / 2 -255, Game.HEIGHT* Game.SCALE / 2 - 180);
		
		//opções do menu
		g.setFont(new Font ("arial", Font.BOLD, 24));
		/*aqui eu to dizendo que seu não fazer a ação do pause
		 irá me aparece apenas o novo jogo*/
		if(pause == false)
		g.drawString("Novo Jogo", (Game.WIDTH*Game.SCALE) / 2 -100, Game.HEIGHT* Game.SCALE / 1 - 130);
		/*aqui é o caso contrario que me a aparece o continuar na tela*/
		else
		g.drawString("Continuar", (Game.WIDTH*Game.SCALE) / 2 -100, Game.HEIGHT* Game.SCALE / 1 - 130);
		g.drawString("Carregar Jogo", (Game.WIDTH*Game.SCALE) / 2 -100, Game.HEIGHT* Game.SCALE / 1 - 90);
		g.drawString("Sair", (Game.WIDTH*Game.SCALE) / 2 - 100, Game.HEIGHT* Game.SCALE / 1 - 50);
		
		
		/*aqui eu estou falando por onde minha seta pode ir, apenas para as funções existentes no jogo*/
		if(options[currentOption] == "novo jogo") {
			g.drawString(">", (Game.WIDTH*Game.SCALE) / 2 - 130, Game.HEIGHT* Game.SCALE / 1 - 130);
		}else if(options[currentOption] == "carregar jogo") {
			g.drawString(">", (Game.WIDTH*Game.SCALE) / 2 - 130, Game.HEIGHT* Game.SCALE / 1 - 90);
		}else if(options[currentOption] == "sair") {
			g.drawString(">", (Game.WIDTH*Game.SCALE) / 2 - 130, Game.HEIGHT* Game.SCALE / 1 - 50);
		}
	}
	
}
