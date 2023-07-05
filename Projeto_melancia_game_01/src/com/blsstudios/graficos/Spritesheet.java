package com.blsstudios.graficos;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Spritesheet {
	
	private BufferedImage spritesheet; 
	/*sempre passamos o parametro path no sprite, que � onde a gente quer que carregue*/
	public Spritesheet(String path) {
		/*O gelClass � um metodo proprio que toda classe tem que quer dizer
		que eu to pegando a classe atual, o getResource vai pega o nome da pasta RES, PATH � o nome
		do arquivo que eu vou passar quando eu instanciar essa classe*/
		try {
			spritesheet = ImageIO.read(getClass().getResource(path));
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public BufferedImage getSprite(int x, int y, int width, int height) {
		/*O que est� retornando aqui � uma bufferedImage, ent�o eu pegarei o meu spritesheet
		que j� � uma bufferedimage, eu irei pegar e colocar o metodo do getSubimage, que me retorna
		uma bufferedimage tamb�m s� que com as cordenadas que eu quero.	 */
		return spritesheet.getSubimage(x, y, width, height);
	}
}
