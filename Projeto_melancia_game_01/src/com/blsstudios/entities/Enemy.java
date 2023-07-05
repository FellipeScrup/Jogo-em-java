package com.blsstudios.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.blsstudios.main.Game;
import com.blsstudios.main.Sound;
import com.blsstudios.world.Camera;
import com.blsstudios.world.World;

public class Enemy extends Entity{
	
	private double speed = 0.5;
	private int right_dir = 0, left_dir = 1;
	public int dir=  right_dir;
	
	private int frames = 0, maxFrames = 5, index = 0, maxIndex = 3;
	private boolean moved = false;
	private BufferedImage[] rightEnemy;
	private BufferedImage[] leftEnemy;
	
	private int maskx = 8 , masky = 8, maskw = 10, maskh = 10;
	
	private double life = 10;
	
	private boolean isDamaged = false;
	private int damageFrames = 05, damageCurrent = 0;
	
	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, null);
		rightEnemy = new BufferedImage[4];
		leftEnemy = new BufferedImage[4];
		for(int i = 0; i < 4; i++) {
			rightEnemy[i] = Game.spritesheet.getSprite(32 + (i*16), 32, 16, 16);
			}
			for(int i = 0; i < 4; i++) {
				leftEnemy[i] = Game.spritesheet.getSprite(32 + (i*16), 48, 16, 16);
				}
	}
	
	public void tick() {
		/*esse metodo aqui eu estou carregando a posi��o do meu player e do meu enemy, para que eu fa�a o meu enemy me segui dependendo da dist�ncia que eu estiver dele.*/
		if(this.calculateDistance(this.getX(), this.getY(),	Game.player.getX(), Game.player.getY()) < 95) {
		/*aqui eu estou dizendo que eu s� mouvo meu enemy se ele n�o estiver colidindo com o meu player,
		 basicamente eu estou dizendo, se n�o colidiu, segue, se colidiu pare.*/
		if(this.isColiddingWithPlayer() == false) {
		moved = false;
		//if(Game.rand.nextInt(100) < 30) {
		/*esse aqui � todo o sistema de inteligencia artifial e de sistema de colis�o dos inimigos com
		 * o mundo */
		if((int)x < Game.player.getX() && World.isFree((int)(x+speed), this.getY())
				/*alem de colis�o com o mundo eu vou verifica a colis�o entre os
				 inimigos.*/
				&& !isColidding((int)(x+speed), this.getY())){
			moved = true;
			
			x+=speed;
		}
		else if((int)x > Game.player.getX() && World.isFree((int)(x-speed), this.getY())
				&& !isColidding((int)(x-speed), this.getY())) {
			moved = true;
			
			x-=speed;
		}
		
		if((int)y < Game.player.getY() && World.isFree(this.getX(), (int)(y+speed)) &&
				!isColidding(this.getX(), (int)(y+speed))){
			moved = true;
			y+=speed;
		}
		else if((int)y > Game.player.getY() && World.isFree(this.getX(), (int)(y-speed)) &&
				!isColidding(this.getX(), (int)(y-speed))) {
			moved = true;
			y-=speed;
		}
		}else {
		//Estamos colidindo com o player
		/*esse metodo randomico abaixo serve para fazer uma probabilidade de nem sempre
		 nosso jogador perde vida para os inimigos, caso a probabilidade
		 bata, eu digo ao java fazer meu player perder vida*/
		if(Game.rand.nextInt(100) < 10) {
			//Sound.playerhurt.play();
			Player.life -= Game.rand.nextInt(3);
			Game.player.isDamaged = true;
			//System.out.println(Player.life);
		}
		
		
	}
		}else {
			
			
		}
		if(moved) {
			frames++;
			if(frames == maxFrames) {
				frames = 0;
				index++;
				if(index > maxIndex)
					index = 0;
			}
		}
		collidingBullet();
		
		if(life <= 0) {
			destroySelf();
		return;
		}
		
		if(isDamaged) {
			this.damageCurrent++;
			if(this.damageCurrent == this.damageFrames) {
				this.damageCurrent = 0;
				this.isDamaged = false;
			}
		}
		
	//}
}
	/*esse sistema faz com que o meu enemy se remova quando as sua vida for 0*/
	public void destroySelf() {
		Game.enemies.remove(this);
		Game.entities.remove(this);
	}
	
	public void collidingBullet (){
		/*esse metodo eu estou detectando coliz�o da bala no meu enemy*/
		for(int i = 0;i < Game.bullets.size(); i++ ) {
			
			Entity e = Game.bullets.get(i);
			/*aqui eu to instanciando a classe bulletshoot, apenas nesse metodo*/
			if(e instanceof BulletShoot) {
				
				/*aqui eu estou dizendo que, se a bala colidiu com o meu enemy, sua vida tem que cai 0,5 dela. E isso que eu to fazendo � uma conferencia.*/
				if(Entity.isColidding(this, e)) {
					isDamaged = true;
					life-= 0.5;
					//Sound.hurt.play();
					Game.bullets.remove(i);
					
					return;
				}
				
				
				
			}
		}
		
	}
	
	public boolean isColiddingWithPlayer() {
		/*esse metodo cria um retangulo ficticio em nossos enemies e em nosso player
		 fazendo com que o enemy se colida sem entra dentro do nosso player*/
		Rectangle enemyCurrent = new Rectangle(this.getX() + maskx,this.getY()+masky, maskw, maskh );
		/*aqui estou fazendo um novo rectangle pegando a posi��o do nosso player para 
		 cria um retangulo de colis�o nele*/
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(),16,16);
		
		return enemyCurrent.intersects(player);
	}
	
	
	public boolean isColidding (int xnext, int ynext) {
		/*o que � a classe Rectangle? e o que ela vai fazer aqui? � uma classe que me permite criar retangulos ficticios que eu n�o
		 vou chegar a renderizar na tela mas que a� eu posso testa essa classe para testa colis�es que tem um metodo proprio para
		 testa para ver se um ta colidindo com o outro.*/				
		Rectangle enemyCurrent = new Rectangle(xnext + maskx,ynext+masky, maskw, maskh );
		/*aqui um loop que j� estou bem familiarizado*/ 
		for(int i = 0; i < Game.enemies.size(); i++) {
			/*uma hora que eu estiver percorrendo essa lista eu irei chegar nessa propria classe, porque essa classe do tick
			 faz parte da lista.*/
			
			Enemy e = Game.enemies.get(i);
			/*aqui eu estou abrindo uma conferencia, aqui eu to dizendo que se E for igual a THIS se der falso, eu n�o quero que
			 fa�a nada por isso o CONTINUE (para continua o loop) */
			if(e == this)
				continue;
			/*esse aqui � o trageto que eu quero testa para ver se meu enemy est� colidindo.*/
			Rectangle targetEnemy = new Rectangle(e.getX() + maskx,e.getY()+ masky, maskw, maskh );
			/*aqui eu estou dizendo que os meu enemies est�o colidindo um com os outros*/
			if(enemyCurrent.intersects(targetEnemy)) {
				return true;

			}
		}
		
		
		/*eu irei retorna falso se todo metodo ali encima n�o der certo*/
		return false;
	}
	
	public void render (Graphics g) {
		if(! isDamaged) {
			
			
		
		if(dir == right_dir) {
			g.drawImage(rightEnemy[index], this.getX() - Camera.x, this.getY()- Camera.y, null);
			}else if (dir == left_dir) {
				g.drawImage(leftEnemy[index], this.getX() - Camera.x, this.getY()- Camera.y, null);
			}
		//g.setColor(Color.BLUE);
		//g.fillRect(this.getX() + maskx - Camera.x, this.getY() + masky - Camera.y,maskw, maskh);
	
		
		}else {
			g.drawImage(Entity.ENEMY_FEEDBACK, this.getX() - Camera.x, this.getY() - Camera.y, null);
		}
	}

}
	


