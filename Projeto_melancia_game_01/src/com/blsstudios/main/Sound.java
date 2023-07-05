package com.blsstudios.main;

import java.io.*;
import javax.sound.sampled.*;

public class Sound {
	
	public static class Clips {
		 
		public Clip[] clips;
		private int p;
		/*esse aqui é um contador*/
		private int count;
		
		/*construtor, agente tem um byte, que bai ter um buffer que vamos percorrer
		 para joga o som*/
		public Clips (byte[] buffer, int count) throws LineUnavailableException , IOException, UnsupportedAudioFileException {
			/*caso buffer for nada, retorne*/
			if(buffer == null)
				return;
			/*o clips vai ser igual a New clip, o tamanho do clip é com base no count*/
			clips = new Clip[count];
			/*aqui eu to dizendo que a minha vareavel dessa classe vai ser igual a do construtor*/
			this.count = count;
			/*aqui eu to opegando o CLIP atual e o abrindo meu audio*/
			for(int i = 0; i < count; i++) {
				clips[i] = AudioSystem.getClip();
				clips[i].open(AudioSystem.getAudioInputStream(new ByteArrayInputStream(buffer)));
			}
		}
		
		/*essa classe é colocada quando eu quero toca apenas uma vez o audio*/
		public void play() {
			if(clips == null) return;
			clips[p].stop();
			clips[p].setFramePosition(0);
			clips[p].start();
			p++;
		/*se o p for maior que o meu count, o p tem que voltar a 0*/
			if(p>= count) p = 0;
		}
		/*essa classe é colocada quando eu quero tocar o som e loop*/
		public void loop () {
			if(clips == null) return;
			clips[p].loop(300);
		}
	}
	
	public static Clips music = load("/musiquinha.wav",1);
	public static Clips hurt = load("/off.wav",1);
	public static Clips playerhurt = load("/playerDano.wav",1);
	
	/*essa aqui é a classe responsavel por carregar o meu som*/
	private static Clips load(String name, int count) {
		try {
			/*aqui eu to fazendo o ByteArrayOutputStream, virar a palavra baos*/
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			/*aqui eu to fazendo o DataInputStream, virar a palavra dis*/ /*nessa parametro a baixo eu vou passar o nome do arquivo*/
			DataInputStream dis = new DataInputStream(Sound.class.getResourceAsStream(name));
			
			/*esse novo Array de byte serve para armazenar todos os valores que a gente ta carregando
			 do som*/
			byte[] buffer = new byte [1024];
			/*nesse int e nesse while eu estou rodando um laço de repetição, eu estou fazendo um loop de repetição*/
			int read = 0;
			while ((read = dis.read(buffer)) >= 0) {
				/*aqui eu coloco os valores que eu preciso referente ao meu arquivo*/
				baos.write(buffer, 0 , read);
			}
			/*aqui eu estou fechando o arquivo*/
			dis.close();
			/*se o valor do data for igual o do baos ele tem que pega e er igual um ByteArray*/
			byte[] data = baos.toByteArray();
			return new Clips(data,count);
		}catch(Exception e) {
			try {
				return new Clips(null, 0);
			}catch(Exception ee) {
				return null;
			}
		}
			
		}
	}
	

