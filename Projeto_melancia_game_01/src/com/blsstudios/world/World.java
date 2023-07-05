package com.blsstudios.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.blsstudios.entities.Bullet;
import com.blsstudios.entities.Enemy;
import com.blsstudios.entities.Entity;
import com.blsstudios.entities.Flower;
import com.blsstudios.entities.LifePack;
import com.blsstudios.entities.Player;
import com.blsstudios.entities.Weapon;
import com.blsstudios.graficos.Spritesheet;
import com.blsstudios.main.Game;


public class World {
	/*aqui estou fazendo um Array simples pois é mais leve que o Array multidimencional*/
	public static Tile[] tiles;
	public static int WIDTH,HEIGHT;
	public static final int TILE_SIZE = 16;
	
	public World (String path) {
		try {
			BufferedImage mapa = ImageIO.read(getClass().getResource(path));
		/*nesse int com um Array, aqui vai ser aonde vamos fazer a "contagem de pixels" e aonde
podemos falar quantos pixels o mapa terá para a gente manipular depois*/
			int[] pixels = new int[mapa.getWidth() * mapa.getHeight()];
			WIDTH = mapa.getWidth();
			HEIGHT = mapa.getHeight();
			tiles = new Tile[mapa.getWidth() * mapa.getHeight()];
			/*agora aqui em baixo eu estou pegando os pixels do mapa e jogando no Array aqui de
cima, com um metodo proprio do BufferedImage*/
			mapa.getRGB(0, 0, mapa.getWidth(), mapa.getHeight(),pixels, 0,mapa.getWidth());
			/*Aqui estou fazendo um loop para fazer o metodo pega minha largura e altura do mapa*/
			for(int xx = 0; xx  < mapa.getWidth(); xx++) {
				for(int yy = 0; yy < mapa.getHeight(); yy++) {
			/*esse metodo aqui abaixo serve para que eu pegue a posição do meu pixels com base nas
posições dos dois loops feitos a cima*/
					int pixelAtual = pixels[xx + (yy*mapa.getWidth())];
					tiles[xx + (yy*WIDTH)] = new FloorTile(xx*16,yy*16,Tile.TILE_FLOOR);
					/*Chão/ floor*/
					if(pixelAtual == 0xFF000000) {
						tiles[xx + (yy*WIDTH)] = new FloorTile(xx*16,yy*16,Tile.TILE_FLOOR);
					/*parede*/
					}else if(pixelAtual == 0xFFFFFFFF) {
						tiles[xx + (yy*WIDTH)] = new WallTile(xx*16,yy*16,Tile.TILE_WALL);	
					/*player*/
					}else if (pixelAtual == 0xFF0026FF) {
						Game.player.setX(xx*16);
						Game.player.setY(yy*16);
					/*enemy*/
					}else if(pixelAtual == 0XFFFF0000) {
						/*a grande sacada daqui é que eu não estou instanciando 2 inimigos to criando uma ref de objeto
						 adicionando ela em uma lista e eu a adiciono em outra lista.Por que fiz isso? por que não confiro na
						 propria lista de entity? Eu poderia utilizar a lista de entities para conferir as posições só que dai iria
						 ter mais objetos que eu não ligo se tiver colisão ou não como as balas, e o lifepack, eu só quero conferir
						 as colisões de inimigos, e nessa lista aqui é tudo que eu preciso (Game.enemies.add(en);).*/
						Enemy en = new Enemy(xx*16,yy*16, 16,16,Entity.ENEMY_EN);
						Game.entities.add(en);
						Game.enemies.add(en);
						/*"arma"*/
					}else if (pixelAtual == 0xFFB200FF) {
						Game.entities.add(new Weapon(xx*16, yy*16, 16, 16, Entity.WEAPON_EN));
						/*Life Pack*/
					}else if (pixelAtual == 0xFF00FF21) {
						Game.entities.add(new LifePack(xx*16, yy*16,16,16, Entity.LIFEPACK_EN));
					/*Bullets*/	
					}else if (pixelAtual ==0xFFFFD800 ) {
						Game.entities.add(new Bullet(xx*16,yy*16,16,16, Entity.BULLET_EN));
						/*flor*/
					}else if (pixelAtual == 0xFF00FFFF) {
						Game.entities.add(new Flower(xx*16,yy*16,16,16, Entity.FLOWER_EN));
					}
				
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**/
	public static boolean isFree(int xnext,int ynext) {
		/*o que eu estou fazendo aqui? eu to pegando a proxima posição e eu to convertendo ela no forma-
		 -to de TILE para que conseguimos acessar este ARRAY aqui (Tile tile = tiles[xx + (yy*WIDTH)];)
		 aqui eu estou analisando todas as possibilidades*/
		int x1 = xnext / TILE_SIZE;
		int y1 =ynext / TILE_SIZE;
		
		int x2 = (xnext + TILE_SIZE - 1) / TILE_SIZE;
		int y2 = ynext / TILE_SIZE;
		
		int x3 = xnext / TILE_SIZE;
		int y3 = (ynext + TILE_SIZE - 1) / TILE_SIZE;
		
		int x4 = (xnext + TILE_SIZE - 1) / TILE_SIZE;
		int y4 = (ynext + TILE_SIZE - 1) / TILE_SIZE;
		/*aqui eu irei retornar se essa varificação abaixo é verdadeira ou falsa, esse tile que eu to
		 verificando (tiles[x1 +(y1*World.WIDTH)] instanceof WallTile) é uma instancia de WallTile, caso
		 for verdade ele me retorna TRUE, só que como a minha conferencia, eu não to verificando para ver
		 se é uma parede, eu estou apenas perguntando se é free (se posso seguir por aquele tile) eu tenho
		 que fazer aucontrario se estiver me retornando true, mas como eu coloco o (!) que dizer que é falso
		 mas temos que fazer outras verificações também com todos os pontos (x1,x2,x3,x4. y1,y2,y3,y4) */
		return ! ((tiles[x1 +(y1*World.WIDTH)] instanceof WallTile) ||
				(tiles[x2 +(y2*World.WIDTH)] instanceof WallTile) ||
				(tiles[x3 +(y3*World.WIDTH)] instanceof WallTile) ||
				(tiles[x4 +(y4*World.WIDTH)] instanceof WallTile));
		
	}
	
	public static void restartGame(String level) {
		Game.entities.clear();
		Game.enemies.clear();
		Game.entities = new ArrayList<Entity>();
		Game.enemies = new ArrayList<Enemy>();
		Game.spritesheet = new Spritesheet("/curso de games.png");
		Game.player = new Player(0,0,16,16,Game.spritesheet.getSprite(32, 0,16,16));
		Game.entities.add(Game.player);
		Game.world = new World("/"+level);
		return;
	}
	
	public void render(Graphics g) {
		/*o xstart nada mais é que a posição WIDTH da minha camera, a minha camera.x precisa ser dividida
		 pelo meu tile.*/
		int xstart = Camera.x >> 4;
		/*o ystart nada mais é também que a posição HEIGHT da camera*/
		int ystart = Camera.y >> 4;
		/*esse é o final da posição aonde eu vou dizer que aqui é o final do o que minha camera deve renderizar*/
		int xfinal = xstart + (Game.WIDTH >> 4);
		int yfinal = ystart + (Game.HEIGHT >> 4);
		
		for(int xx = xstart; xx <= xfinal; xx++) {
			for(int yy = ystart; yy <= yfinal; yy++) {
		/*o indice da camera não pode ficar negativo, mas caso ela fique, ela deve não renderizar nada e continuar*/
				if(xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT)
					continue;
				Tile tile = tiles[xx + (yy*WIDTH)];
				tile.render(g);
			}
		}
	}
	
}
