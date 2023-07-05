package com.blsstudios.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.blsstudios.main.Game;
import com.blsstudios.world.Camera;
import com.blsstudios.world.WallTile;
import com.blsstudios.world.World;

public class BulletShoot extends Entity {
	/*o dx e dy � as variaveis que v�o setar a dire��o que as minhas balas tem que ir*/
	private double dx;
	private double dy;
	/*aqui � a velocidade que nossa bala vai ter*/
	private double spd = 4;
	/*essa variavel serve como uma vida para a bala, fazendo com que ela chegue at� uma distancia do mapa e depois seja apagada	*/
	private int life = 8, curLife=0;
	
	public static boolean CollidingWall = false;
	
	public BulletShoot(int x, int y, int width, int height, BufferedImage sprite, double dx, double dy) {
		super(x, y, width, height, sprite);
		this.dx = dx;
		this.dy = dy;
		
		
	}
	
	public void tick() {
		x+= dx*spd;
		y+= dy*spd;
		/*aqui � o metodo que apaga as balas depois de uma distancia quando ela chega no curLife*/
		curLife++;
		if(curLife == life) {
			Game.bullets.remove(this);
			return;
		}
	}
	
	
	public void render(Graphics g) {
		/*aqui eu estou renderizando um circulo preto na tela que vai ser a nossa bala*/
		g.setColor(Color.BLACK);
		g.fillOval(this.getX() - Camera.x, this.getY() - Camera.y, width, height);
	}
	
}
