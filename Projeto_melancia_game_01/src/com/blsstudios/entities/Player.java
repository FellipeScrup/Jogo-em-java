package com.blsstudios.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.blsstudios.graficos.Spritesheet;
import com.blsstudios.main.Game;
import com.blsstudios.world.Camera;
import com.blsstudios.world.World;
import com.blsstudios.entities.BulletShoot;
/*O player ou personagem principal do jogo é uma entity por isso damos um extends da Entity*/
public class Player extends Entity{

	public boolean right,up,left,down; 
	/*quando eu coloco right_dir, o player sempre irá inicializar para o lado direito,
e se eu setar o left_dir, ele sempre irá inicializar pelo lado esquerdo. E com o DIR eu
seto as direções e as animações de direção, como o player ir para cima, ou para baixo
e para os lados.*/
	public int right_dir = 0, left_dir = 1;
	/*aqui estou declarando a direção que o meu player deve iniciar*/
	public int dir=  right_dir;
	/*aqui eu falo a velocidade do meu player*/
	public double speed = 1.0;
	
	/*aqui eu digo quantos frames, e quantas animações eu tenho (index)*/
	private int frames = 0, maxFrames = 5, index = 0, maxIndex = 3;
	/*toda vez que comessarmos a ligar a logica do player o moved dele vai ser igual a 
falso, a não ser que precionemos a tecla para tal direção que queromos que o player vá*/
	private boolean moved = false;
	/*dois Arrays de direção do nosso player, se eu quiser posso fazer ele pra cima e de 
costa */
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	
	
	private BufferedImage playerDamage;
	
	private boolean hasGun = false; 
	
	public static int ammo = 0;
	
	
	public boolean isDamaged = false;
	/*eu faço esse metodo para que o nosso player pisque branco quando for atacado*/
	private int damageFrames = 0; 
	
	
	public boolean shoot = false, mouseShoot = false;
	
	public static double life = 100, maxLife=100;
	/*aqui eu estou dizendo a posição atual do meu mouse*/
	public int mx, my;
	
	public boolean jump =  false;
	/*essa Classe é classe que pergunta ao java se estamos pulando*/
	public boolean isJumping = false;
	/*esse eixo z é um eixo ficticio, pois é um jogo 2D, isso serve para dar uma sensção de 2.5D*/
	public int zplayer = 0;
	
	public int jumpFrames = 50, jumpCur = 0;
	
	public boolean jumpUp = false, jumpDown = false; 
		
	
	public double jumpSpd = 1.8;
	
	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		rightPlayer = new BufferedImage[4];
		leftPlayer = new BufferedImage[4];
		/*aqui é aposição do nosso feadback*/
		playerDamage = Game.spritesheet.getSprite(32, 64, 16, 16);
		/*o loop está com valor de 4 pois temos 4 animações e podemos repitir ela 4 vezes*/
		for(int i = 0; i < 4; i++) {
		rightPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 0, 16, 16);
		}
		for(int i = 0; i < 4; i++) {
			leftPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 16, 16, 16);
			}
		
	}
	public void tick(){
		if(jump) {
			/*aqui faz com que eu consiga pular para cima*/
			if(isJumping == false) {
			jump = false;  
			isJumping = true;
			jumpUp = true;
			}
		}
		/*caso o meu isJumping for true faça eu subir na vel do jumpSPD*/
		if(isJumping == true) {
				if(jumpUp) {
					jumpCur+= jumpSpd;
		/*aqui o famoso, caso contrio faça a nossa spd diminuir e nos faça voltar ao chão*/
				}else if(jumpDown) {
					jumpCur -= jumpSpd;
					if(jumpCur <= 0 ) {
						isJumping = false;
						jumpDown = false;
						jumpUp = false;
					}
			 }
				/*aqui é onde eu detecto a minha altura maxima*/
				zplayer = jumpCur;
				if(jumpCur >= jumpFrames) {
					jumpUp = false;
					jumpDown = true;
					// System.out.println("Chegou na altura máxima");
				}
			
		}
		
		
		
		moved = false;
		/*aqui estou dizendo o metodo de quando o player vai para direita e sua velocidade*
		 (&& World.isFree ((int)(x+speed),this.getY())) isso que eu tenho aqui nada mais é do que uma
		 verificação, para preve se a posição que eu vou avança está livre ou não. E também aqui eu estou
		 convertendo um DOUBLE para INT, o que eu eu to fazendo? Estou pegando X em double mais a
		 minha speed somando os dois para depois eles serem convertidos em INT (numeros inteiros)
		 por isso não estou usando o getX pois aqui já vai me retorna inteiro direto, e não é isso que eu quero eu quero a 
		 posição real que vai está, e como não estou me movendo no eixo Y ele não importa pois estou apenas utilizando aqui
		 o eixo X */
		if(right && World.isFree ((int)(x+speed),this.getY())) {
			moved = true;
			dir = right_dir;
			x+=speed;
		}
		//aqui estou dizendo o metodo de quando o player vai para esquerda e sua velocidade
		else if(left&& World.isFree((int)(x-speed),this.getY())) {
			moved = true;
			dir = left_dir;
			x-=speed;
		}
		//aqui estou dizendo o metodo de quando o player vai para cima e sua velocidade. OBS: o up é negativo(-) e o down positivo (+)
		if(up && World.isFree(this.getX(),(int) (y -speed))) {
			moved = true;
			y-=speed;
		}
		//aqui estou dizendo o metodo de quando o player vai para baixo e sua velocidade. OBS: o up é negativo(-) e o down positivo (+)
		else if(down && World.isFree(this.getX(),(int) (y + speed))) {
			moved = true;
			y+=speed;
		}
/*caso o moved for true, eu vou quere que rode as animações do nosso player*/
		if(moved) {
			frames++;
			if(frames == maxFrames) {
				frames = 0;
				index++;
				if(index > maxIndex)
					index = 0;
			}
		}
		checkCollisionLifePack();
		checkCollisionAmmo();
		checkCollisionGun();
		
		/*aqui é aonde eu seto o feadback*/
		if(isDamaged) {
			this.damageFrames++;
			if(this.damageFrames == 4) {
				this.damageFrames = 0;
				isDamaged = false;
			}
		}
		/*essa aqui é a nossa lógica serve para que atire*/	
		if(shoot) {
			shoot = false;
			/*nesse metodo eu falo que só posso atirar
			 se eu tiver a arma e se eu tiver munição*/
			if(hasGun && ammo > 0) {
			ammo--;
			
			int dx = 0;
			int px = 0;
			int py = 6;
			if(dir == right_dir) {
				px = 9;
				
				dx = 1;
			}else {
				px =  3;
				 dx = -1;
			}
			
			BulletShoot bullet = new BulletShoot(getX() + px ,getY() + py , 3, 3, null, dx , 0);
			Game.bullets.add(bullet);
			}
		}
		
		if(mouseShoot) {				
			mouseShoot = false;

			if(hasGun && ammo > 0) {
			ammo--;
			/*aqui eu to setando a posição de onde sai a bala do meu player*/
			int px = 7, py = 6;
			double angle = 0;
			if(dir == right_dir) { 
				px = 9;
			/*aqui eu estou calculando o ângulo com base no meu player. O primeiro metodo que eu preciso passar
			 é o meu trageto, aonde eu quero que comece a calcular que é primeiro o meu my, */
				angle = Math.atan2(my - (this.getY()+py - Camera.y) , mx - (this.getX()+px - Camera.x));
				
			}else {
				px= 0;
				angle = Math.atan2(my - (this.getY()+py - Camera.y) , mx - (this.getX()+px - Camera.x)); 
				
			}
			/*aqui eu estou usando o Seno e o Cosseno para deginir a direção da bullet, e tem que ser double pois
			 irá dar muitos numeros "quebrados"*/
			double dx = Math.cos(angle);
			double dy =Math.sin(angle);

			 
			BulletShoot bullet = new BulletShoot(getX() + px ,getY() + py , 3, 3, null, dx , dy);
			Game.bullets.add(bullet);
			}
		}
		
			
		
		/*esse metodo serve para dar game over*/
		if(life <= 0) {
			life = 0;
			Game.gameState = "GAME_OVER";
		}
		
		updateCamera();
		
		
		
		
	}
	public void updateCamera() {
		/*esse metodo aqui faz a camera seguir o jogador
		esse é um calculo que mostra o quanto falta pro meu player, o quanto falta pro centro da tela, AQUI estou limitando
		até onde a camera pode ir, e o maximo que posso ir com a minha camera*/
		Camera.x = Camera.clamp(this.getX() - (Game.WIDTH/2), 0, World.WIDTH * 16  - Game.WIDTH) ;
		Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT/2),0 , World.HEIGHT * 16 - Game.HEIGHT);
	
	}
	/*esse metodo detecta colizão do nosso player com a arma*/
	public void checkCollisionGun() {
		/*com este FOR eu estarei percorrendo por todas as minhas entities, por isso que se o jovo tiver
		 muito cheio de entities o certo é criar uma lista individual a ele igual os enemies tem */
		for(int i = 0; i < Game.entities.size();i++) {
		/*aqui eu estou verificando a minha entity atual*/	
			Entity atual = Game.entities.get(i);
		/*fazendo isso acima eu tenho que verificar se essa entity é um lifepack, por isso dou um instanceof*/	
			if(atual instanceof Weapon) {
			/*e com esse metodo eu verifico se eu estou colidindo com um lifepack, como primeiro metodo
			 colocamos no e1 um THIS para pegar da classe atual, e no e2 colocamos o ATUAL, para ver se
			 o player está colidindo*/	
				 if(Entity.isColidding(this, atual)) {
					/*se caso colidir ele iré aumenta a nossa arma.*/
					 hasGun = true;
					 
					// System.out.println("pego a arma");
					 /*e esse metodo serve para destruir o as balas caso a gente colida com ela*/
					 Game.entities.remove(atual);
				 }
			}
		}
	}
	
	
	public void checkCollisionAmmo() {
		/*com este FOR eu estarei percorrendo por todas as minhas entities, por isso que se o jovo tiver
		 muito cheio de entities o certo é criar uma lista individual a ele igual os enemies tem */
		for(int i = 0; i < Game.entities.size();i++) {
		/*aqui eu estou verificando a minha entity atual*/	
			Entity atual = Game.entities.get(i);
		/*fazendo isso acima eu tenho que verificar se essa entity é um lifepack, por isso dou um instanceof*/	
			if(atual instanceof Bullet) {
			/*e com esse metodo eu verifico se eu estou colidindo com um lifepack, como primeiro metodo
			 colocamos no e1 um THIS para pegar da classe atual, e no e2 colocamos o ATUAL, para ver se
			 o player está colidindo*/	
				 if(Entity.isColidding(this, atual)) {
					/*se caso colidir ele iré aumenta a nossa munição.*/
					 ammo+=50;
					// System.out.println("munição: " + ammo);
					 /*e esse metodo serve para destruir o as balas caso a gente colida com elas*/
					 Game.entities.remove(atual);
				 }
			}
		}
	}
	
	
	/*esse metodo é aonde ficará toda a logica do nosso lifepack*/
	public void checkCollisionLifePack(){
		/*com este FOR eu estarei percorrendo por todas as minhas entities, por isso que se o jovo tiver
		 muito cheio de entities o certo é criar uma lista individual a ele igual os enemies tem */
		for(int i = 0; i < Game.entities.size();i++) {
		/*aqui eu estou verificando a minha entity atual*/	
			Entity atual = Game.entities.get(i);
		/*fazendo isso acima eu tenho que verificar se essa entity é um lifepack, por isso dou um instanceok*/	
			if(atual instanceof LifePack) {
			/*e com esse metodo eu verifico se eu estou colidindo com um lifepack, como primeiro metodo
			 colocamos no e1 um THIS para pegar da classe atual, e no e2 colocamos o ATUAL, para ver se
			 o player está colidindo*/	
				 if(Entity.isColidding(this, atual)) {
					/*se caso colidir ele iré aumenta a nossa vida.*/
					 life+=10;
					 /*e nesse if eu digo que se minha vida for maior que 100, ela tem que continuar sendo 100*/
					 if(life > 100)
						 life = 100;
					 /*e esse metodo serve para destruir o lifepack caso a gente colida com ele*/
					 Game.entities.remove(atual);
				 }
			}
		}
	}
	/*aqui vamos renderizar o player e suas animções*/
	public void render(Graphics g) {
	if(! isDamaged) {
		if(dir == right_dir) {
		g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY()- Camera.y - zplayer, null);
		if(hasGun) {
			//desenhar arma para direita
			g.drawImage(Entity.GUN_RIGHT, getX()+6 -Camera.x, getY()+2 - Camera.y - zplayer, null);
		}
		}else if (dir == left_dir) {
			g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY()- Camera.y - zplayer, null);
		/*esse metodo renderiza a minha arma*/
			if(hasGun){
			g.drawImage(Entity.GUN_LEFT, getX()+6 -Camera.x, getY()+2 - Camera.y - zplayer, null);
		}
		}
		}else {
			g.drawImage(playerDamage, this.getX() - Camera.x, this.getY() - Camera.y - zplayer, null);
		}
	if(isJumping) {
		g.setColor(Color.BLACK);
		g.fillOval(this.getX() - Camera.x + 3, this.getY() - Camera.y +10,10,10);
	}
	}
}
