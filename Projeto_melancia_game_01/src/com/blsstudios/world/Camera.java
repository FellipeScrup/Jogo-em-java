package com.blsstudios.world;

public class Camera {
	/*A camera precisa ter o seus proprios metodos de x e y*/
	public static int x;
	public static int y;
	/*aqui estou falando os tamanhos da minha camera no meu player*/
	public static int clamp(int xAtual, int xMin, int xMax) {
		if(xAtual < xMin) {
			xAtual = xMin;
		}
		
		if(xAtual > xMax){
			xAtual = xMax;
		}
		return xAtual;
	}
	
}
