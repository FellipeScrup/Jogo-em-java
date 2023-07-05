package com.blsstudios.graficos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.blsstudios.entities.Player;
import com.blsstudios.main.Game;

public class UI {

	
	public void render(Graphics g) {
		g.setColor(Color.RED);
		g.fillRect(03, 4,50,10);
		g.setColor(Color.GREEN);
		/*aqui estou fazendo um metodo para mostra a minha vida na tela
		 com uma regra de 3 basica*/
		g.fillRect(03, 4,(int)((Game.player.life/Game.player.maxLife)*50),10);
		g.setColor(Color.white);
		/*aqui estou setando a font dos numeros e o tamanho da font*/
		g.setFont(new Font("arial", Font.BOLD, 10));
		/*e aqui estou fazlando para renderizar os numeros da vida na minha
		 tela*/
		g.drawString((int)Game.player.life+"/" +(int)Game.player.maxLife,04,13);
		
	}
	
}
