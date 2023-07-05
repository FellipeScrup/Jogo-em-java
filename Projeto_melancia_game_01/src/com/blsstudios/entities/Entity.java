package com.blsstudios.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.blsstudios.main.Game;
import com.blsstudios.world.Camera;
 
/*coisas que possuem vida no jogo nós chamamos de entities*/
public class Entity {
	/*todos esses metodos aqui a baixo servem para renderizarar as entities no mapa*/
	public static BufferedImage LIFEPACK_EN = Game.spritesheet.getSprite(96, 0, 16, 16);
	public static BufferedImage WEAPON_EN = Game.spritesheet.getSprite(112, 0, 16, 16);
	public static BufferedImage BULLET_EN = Game.spritesheet.getSprite(96, 16, 16, 16);
	public static BufferedImage ENEMY_EN = Game.spritesheet.getSprite(32, 32, 16, 16);
	public static BufferedImage ENEMY_FEEDBACK = Game.spritesheet.getSprite(64, 64, 16, 16);
	public static BufferedImage FLOWER_EN = Game.spritesheet.getSprite(128,	32, 16, 16);
	public static BufferedImage GUN_LEFT = Game.spritesheet.getSprite(112, 0, 16, 16);
	public static BufferedImage GUN_RIGHT = Game.spritesheet.getSprite(144, 0, 16, 16);
	/*todos esses metodos servem para setar o tamanho e aonde as entities devem ser criadas*/
	/*os metodos estão como protected por conta que elas precisão ser acessadas pelas classes filhas*/
/*apartir de agora o nosso X e Y serão DOUBLES, ou sejá números com virgula*/
	protected double x;
	protected double y;
	protected int width;
	protected int height;
	
	private BufferedImage sprite;
	
	public int maskx,masky,mwidth,mheight;
	
	public Entity(int x, int y, int width, int height, BufferedImage sprite) {
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
		this.sprite = sprite;
		
		this.maskx = 0;
		this.masky = 0;
		this.mwidth = width;
		this.mheight = height;
	}
	/*esse metodo serve para que eu consiga setar uma mascara em qualquer entidade, aqui eu consigo definir
	 as mask das minhas entities. E com esse metodo será possivel verificar a mascara de colisão de qualquer
	 entities. esse metodo permite eu troca as mask */
	public void setMask(int maskx,int masky,int mwidth,int mheight){
		this.maskx = maskx;
		this.masky = masky;
		this.mwidth = mwidth;
		this.mheight = mheight;
	}
	
	public void setX(int newX) {
		this.x = newX;
	}
	public void setY(int newY) {
		this.y = newY;
	}
	
	public int getX() {
		/*vamos manter o int da getX e getY, só que quendo for retorna serão numeros inteiros também*/
		return (int)this.x;
	}
	public int getY() {
		return (int)this.y;
	}
	public int getWidth() {
		return this.width;
	}
	public int getHeight() {
		return this.height;
	}
	/*aqui será feita a logica da entidade*/
	public void tick () {}
	
	public double calculateDistance(int x1, int y1, int x2, int y2) {
		/*o SQRT retorna a distancia de como seria de quando eu uso angulos para calcular uma certa direção, esse metodo vai me volta com base no angulo do meu
		 player, falando qaunto seria a distância em pixels para eu conseguir chegar nesse lugar, não utilizando o metodo de só subtrai o x e y, mas sim, essa distancia
		 seria se eu utilizasse o seno e o consseno para chegar ali, ou seja esse metodo a baixo vai me retorna a distancia mais curta para que eu consiga chegar no meu
		 destino, quantas vezes que o codigo teria que ser executado para o player chega ao inimigo, é basicamente essa a diferença desse metodo para aquele de subtrair
		 o X e o Y, essa conta a baixo me retorna precisamente, quantas vezes que o meu codigo teria que ser executado para o inimigo ou player chagarem ao seu destino final.*/
		return Math.sqrt((x1 - x2) * (x1 - x2)  + (y1 - y2) * (y1 - y2));
	}
	
	
	/*e quando eu testar o sistema de colisão também estarei testando o sistema do SetMask*/
	public static boolean isColidding(Entity e1,Entity e2){
		Rectangle e1Mask = new Rectangle(e1.getX() + e1.maskx,e1.getY()+e1.masky,e1.mwidth,e1.mheight);
		Rectangle e2Mask = new Rectangle(e2.getX() + e2.maskx,e2.getY()+e2.masky,e2.mwidth,e2.mheight);
		/*eu dou um return para ver se um retangulo ta intercectando um com o outro*/
		return e1Mask.intersects(e2Mask);
	}
	public void render(Graphics g) {
		/*todos sprites terão o - Camera.x e - Camera.y*/
		g.drawImage(sprite, this.getX() - Camera.x, this.getY() - Camera.y, null);
	}
}
