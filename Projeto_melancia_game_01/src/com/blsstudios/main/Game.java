package com.blsstudios.main;



import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
//import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
/*tudo aqui é a estrutura para cria um game*/
import javax.swing.JFrame;

import com.blsstudios.entities.BulletShoot;
import com.blsstudios.entities.Enemy;
import com.blsstudios.entities.Entity;
import com.blsstudios.entities.Player;
import com.blsstudios.graficos.Spritesheet;
import com.blsstudios.graficos.UI;
import com.blsstudios.world.World;
/*O Runnable já meio que cria um game loop*/
public class Game extends Canvas implements Runnable,KeyListener,MouseListener, MouseMotionListener{

	public static JFrame frame;
	/*tamanho da nossa janela são todos esses metodos a baixo*/
	private Thread thread;
	private boolean isRunning = true;
	public static final  int WIDTH = 240;/*240*/
	public static final int HEIGHT = 160;/*160*/
	public static final int SCALE = 3;
	/*aqui vemos se o qual é aonde começa que é no cur_level, e o maximo de level que eu tenho.      */
	private int CUR_LEVEL = 1, MAX_LEVEL = 2;
	/*Tiles sempre são objetos estaticos*/
	private BufferedImage image;
	/*aqui basicamente eu vou ter uma lista do entity*/
	public static List <Entity> entities;
	public static List<Enemy> enemies;
	/*aqui eu vou ter uma lista especifica para guardar as nossas balas*/
	public static List<BulletShoot> bullets;
	public static Spritesheet spritesheet;
	/*Aqui estou instanciando a classe player da entities*/
	public static Player player; 
	/*Aqui estou instanciando a classe world */
	public static World world;
	
	public static Random rand;
	
	
	public UI ui;
	
	public int xx,yy;
	
	/*isso aqui que eu to fazendo criando o Objeto InputStream, eu estou utilizando classes do java, usando objetos auxiliares, para que carregue a minha class, aí
	 eu coloco o meu getResourceAsStream para fala o arquivo que ele tem que ler, a minha fonte melhor dizendo*/
	public InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("pixelartfont.ttf");
	public Font newfont;
	
	/*aqui eu estou começando a mexer com as games states*/
	public static String gameState = "MENU"; 
	/*Esses metodos fazem com que o meu ENTER fique piscando na tela*/
	private boolean showmessageGameOver = true;
	private int framesGameOver = 0;
	private boolean restartGame = false;
	
	private Graphics g;
	
	/*aqui estou instanciando a minha classe menu*/
	public Menu menu;
	
	public boolean saveGame = false;
	
	public int[] pixels;
	public BufferedImage lightmap;
	public int[] lightMapPixels;
	
	/*o mx é para pegar a posição X do meu mouse, o meu my é para a posição Y*/
	public int mx,my;
	
	/*esse aqui é o metodo construtor o metodo "game", e nele vamos colocar alguma variaveis*/
	public Game() {
		rand = new Random()	;
		/*Aqui estou chamando o metodo KeyListener nessa mesma classe*/
		addMouseListener(this);
		addKeyListener(this);
		addMouseMotionListener(this);
		this.setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
	initFrame();
	ui = new UI();
	//Inicializando objetos como as entities e o player e tudo que tem no jogo
	image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
	try {
		/*aqui eu to carregando a imagem*/
		lightmap = ImageIO.read(getClass().getResource("/light_map.png"));
	} catch (IOException e1) {
  		e1.printStackTrace();
	}
	/*aqui eu estou inicializando o meu Array*/
	lightMapPixels = new int[lightmap.getWidth()*lightmap.getHeight()];
	/*aqui eu vou coloca todos os meus pixels e um Array,agora vou colocar os pixels da imagem dentro do meu Array*/
	lightmap.getRGB(0,0,lightmap.getWidth(), lightmap.getHeight(),lightMapPixels,0, lightmap.getWidth());
	pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	/*por que uma ArrayList? e não uma classe individual? por conta de que vamos
ter varias entities no nosso jogo, não importa se é o inimigo ou se é um player,
todos são entidades, o que vai mudar é a ação que cada um deles vai fazer*/
	entities = new ArrayList<Entity>() ;
	enemies = new ArrayList<Enemy>();
	/*eu instanceio essa lista aqui pois podemos ter varias balas */
	bullets = new ArrayList<BulletShoot>();
	/*aqui estamos dizendo ao java aonde está a nossa spritesheet*/
	spritesheet = new Spritesheet("/curso de games.png");
	player = new Player(0,0,16,16,spritesheet.getSprite(32, 0, 16, 16));
	/*esse metodo eu digo ao java o que eu quero desenha na tela, uma imagem minha do spritesheet, seja entities como tile, só que aqui só vai
	as entities por eu chamar a entities.add() */
	entities.add(player);
	world = new World("/level1.png");
	/*aqui eu to inicializando o meu menu no game*/
	menu = new Menu();
	
	/*aqui eu estou colocando a minha Font para inicializar no jogo, aqui eu tenho que coloca o seu formato, o stream
	 da li de cima para carrega a minnha font, e tem que coloca um Try e um catch pois pode falhar,e o derive font é 
	 o tamanho da nossa font*/
	try {
		newfont = Font.createFont(Font.TRUETYPE_FONT, stream) .deriveFont(40f);
	} catch (FontFormatException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
	
	}
	
	public void initFrame() {
		/*com esse metodo abaixo eu estou estendendo o CANVAS. O  canvas não faz a janela mas contem as propriedades para cria a
		minha janela como escolhe o tamanho da janela*/
				frame = new JFrame("projeto melancia");
			/*por que o this? porque é para meu CANVAS, o frame vai conseguir de fato pegar todas as propriedades que temos aqui que é o
		(this.setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));) */
				frame.add(this);
				/*porque é false? porque eu não quero que o meu usuario consiga re-direcionar a janela*/
				frame.setResizable(false);
			/*isso é um metodo do frame para depois que eu add o canvas seta as dimensões e mostrar para mim*/
				frame.pack();
		/*por que o null? nisso eu to dizendo ao JAVA que eu quero que a minha janela fique no centro da tela*/
				frame.setLocationRelativeTo(null);
		/*eu to chamando o metodo do frame dizendo que quando eu clikar para fechar eu quero que de fato o jogo feche, porque muitas vezes
		eu poderia fechar e deixa o jogo rodando, tem como fazer isso que eu vou chamar o atributo do proprio JFrame estatico que tem um valor nume-
		-rico que é o (EXIT_ON_CLOSE), clico e vai fechar o jogo*/
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		/*Pra quando inicializar aqui já ta visivel a janela */
				frame.setVisible(true);
			
	}
	
	public synchronized void start() {
	thread = new Thread(this);
	isRunning = true;
	thread.start();
	}
	
	public synchronized void stop () { 
		/*tudo que ta aqui serve para peralizar as threads e trazer performance ao jogo*/
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main (String args[]) {
	Game game = new Game();
	/*nessa função o meu jogo iniciou de fato*/
	game.start();
	}
	/*aqui  começa toda logica do jogo */
	public void tick () {
		/*aqui eu estou dizendo que, se a vida do player não for menor que 0,
		 o jogo continua com toda a logica*/
			if(gameState == "NORMAL") {
				//xx++;
				/*aqui eu estou dizendo que o meu usuario apertou o save game e o jogo está
				 no estado normal, quer dizer que podemos fazer isso de fato*/
				if(this.saveGame) {
					 /*eu começo dando um false no save game se não iremos cria um monte de
					  arquivo de save game*/
					this.saveGame = false;
					/*aqui eu estou construindo o que vou salvar*/
					String[] opt1 = {"level","vida","munição"};
					/*aqui eu coloco o que deve ser salvo*/
					int[] opt2 = {this.CUR_LEVEL,(int) Player.life, Player.ammo};
					/*aqui é o metodo do meu menu*/
					Menu.saveGame(opt1, opt2, 10);
					/*siste a de debug aqui*/
					System.out.println("Jogo Salvo");
				}
		/*esse metodo abaixo impede que você resete o mundo sem dar o GAME OVER*/
		this.restartGame = false;
				/*isso aqui nada mais é que um loop para rodarmos a logica de nossas
entities*/
		for(int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			
			e.tick();
		}
		/*aqui é um loop que fazemos para que rode a logica das balas sem problemas*/
		for(int i = 0; i < bullets.size(); i++) {
			bullets.get(i).tick();
		}
		/*essa verificação ve se todos meus inimigos foram destruidos*/
		if(enemies.size() == 0) {
			//Avançar para o próximo level!
			CUR_LEVEL++;
			if(CUR_LEVEL > MAX_LEVEL){
				CUR_LEVEL = 1;
			}
			/*esse metodo a baixo fala se o que ele ta carregando é o level 1 ou 2*/
			String newWorld = "level"+CUR_LEVEL+".png";
			//System.out.println(newWorld);
			/*e sesse aqui ele reseta o nosso  mundo a cada vez que o meu player mata todos os inimigos, tudo vai volta a 0*/
			World.restartGame(newWorld);
			}
		/*aqui é quando o meu player morre toda a lógica do jogo para e dá o game state de game over*/
		}else if(gameState == "GAME_OVER") {
			/*aqui eu estou animando a minha mensagem que aparece na tela */
			this.framesGameOver++;
			/*nesses dois metodos é que eu faço fica piscando a mensagem na tela pois eu vou de 0 a 60 frames*/
			if(this.framesGameOver == 60) {
				this.framesGameOver = 0;
				/**/
				/*e nesses dois eu estou falando para eles mostrarem o meu ENTER*/
				if(this.showmessageGameOver)
					this.showmessageGameOver= false;
				else 
					this.showmessageGameOver = true;
			}
			/*aqui é a minha logica do reset no mundo, precionando ENTER, e aqui eu só quero que renincie o jogo só se der game over, porque se o meu player 
			 aperta enter sem quere o jogo vai resetar*/
			if(restartGame) {
				this.restartGame = false;
				Game.gameState = "NORMAL";
				CUR_LEVEL = 1;
				String newWorld = "level"+CUR_LEVEL+".png";
				//System.out.println(newWorld);
				/*e sesse aqui ele reseta o nosso  mundo a cada vez que o meu player mata todos os inimigos, tudo vai volta a 0*/
				World.restartGame(newWorld);
			}
		}else if (gameState == "MENU") {
			menu.tick();
		}
	}
	/*
com esse metodo a baixo me permite fazer Retangulos apenas manipulando
os pixels e os convertendo para virar um retangulo vermelho,nele temos 
que ter tanto a posição de X e de Y.

	public void drawRectangleExample(int xoff, int yoff){
		for(int xx = 0; xx < 32; xx++) {
			for(int yy = 0; yy < 32; yy++) {
				int xOff = xx + xoff;
				int yOff = yy - yoff;
				if(xOff < 0 || yOff < 0 || xOff >= WIDTH || yOff >= HEIGHT)
					 continue;
				pixels[xOff + (yOff*WIDTH)] = 0xFF0000;
			}
		}
	}
	*/
	/*esse sistema aplica a o circulo no meu game*/
	public void applyLight() {
		for(int xx = 0; xx < Game.WIDTH; xx++) {
			for(int  yy = 0; yy < Game.HEIGHT; yy++) {
				/*aqui eu estou falando ao java que a parte que está branca é para ficar
				 preta e para a parte preta fica transparente*/
				if(lightMapPixels [xx + (yy * Game.WIDTH)] == 0xffffffff) {
					/*aqui eu estou setando para que envolta do circulo seja preto */
					pixels[xx + (yy*Game.WIDTH)] = 0;
				}
			}
		}
	}
	/*aqui vai ser cuidada a parte grafica do jogo*/
	public void render() {
	
		BufferStrategy bs = this.getBufferStrategy();
	/*Se o meu BS for igual a null, quer dizer que não existe nem um bufferstrategy*/	
		if(bs == null) {
		/*BuffferStrstegy nada mais é que uma sequencia de buffers que a gente coloca na nossa
tela para otimizar a renderização, é um metodo para mostra como lidar com os graficos de uma maneira
mais proficional e vizanddo performance*/
			this.createBufferStrategy(3);
			return;
		}
	/*com esse metodo já conseguimos renderizar na tela, e agora toda renderização sera feita
aqui*/ /* */
		Graphics g = image.getGraphics(); 
	/*O SETCOLOR basicamente serve para fala a cor que o objeto tem que ser rederizado*/
		
		/*eu sempre preciso usar o g.setColor e o g.fillRect para apagar o que tinha na minha tela antes
para aí eu começa a renderizar de novo, porque se eu não apaga o que ta na minha tela é como se eu renderizasse
na posição X e depois eu fosse renderizar na posição X+10, só o que foi feito na posição X primaria vai continuar
ali, então o que eu faço?? eu limpo toda a hora a tela preenchendo com um retangulo preto ou com qualquer cor*/
		g.setColor(new Color(0,0,0));
	/*A FILLRECT serve para renderizar um retangulo na tela e esse retangulo é setado nas posições X e Y, o X e Y são a posição do
retangulo, e o WIDTH E HEIGHT, nada mais são que as dimenções, o primeiro é a largura e o segundo é a altura*/	
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		
		
		
		
		/*agora eu vou pegar o meu grafico pricipal e vou desenhar a minha imagem, com isso
eu consigo manipula o que eu tenho dentro da minha imagem, e depois eu a desenho na tela com o
metodo abaixo desse*/
	/*O g.setColor e o g.fill são maneiras de renderizar individual mente cada retnagulo ou qualquer figura geométrica, esse codigo pode ser
copiado e colado meio que quantas vezes eu quiser basta apenas mudar a posição da figura geometrica g.filltantofaz(x, y, width, height)*/
		g.setColor(Color.blue);
/*os metodos abaixo g.setFont e tambem os setcolor e o g.drawString, são metodos simples de renderizar uma palavra na tela do
 jogo (g.setFont(new Font("Arial", Font.BOLD,20));) (g.drawString("olar mundor", 10, 20);)   */
		/*
		g.setFont(new Font("Arial", Font.BOLD,20));
		g.setColor(Color.WHITE);
		g.drawString("olar mundor", 10, 20);
		g = bs.getDrawGraphics();
		*/
		/*o g.drawimage serve para renderizar o nosso player o nosso sprite, e eu posso renderizar
		 quantos jogadores eu quizer*/
		
		/*o que o Graphics2D quer dizer? quer dizer que agora eu vou ter um objeto do graficos2d só que ele ta sendo igual a variavel G, ou
seja a gente sabe que é uma variavel do tipo de grafico porem o "()" que eu coloquei se chama cast, cast é: eu estou transformando essa varia-
-vel do tipo graficos no tipo de graficos 2d que agora permite que eu crie animaçõe e efeitos mais avançados usando o objeto 2d, que eu já
tinha o graphics G, só que eu tinha que ativar esse CAST, não to instanciando nem nada apenas usando o cast, para que na minha variavel g2
eu ter os metodos que eu preciso para ter os graficos 2d */
		//Graphics2D g2 = (Graphics2D) g;
		/*o mundo sempre tem que ser renderizado primeiro que as entities*/
		world.render(g);
		/*Agora esse loop serve para renderizar as nosssas entities no jogo*/
		for(int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g);
		}
		for(int i = 0; i < bullets.size(); i++) {
			bullets.get(i).render(g);
		}
		
		//applyLight();
		ui.render(g);
		g.dispose();
		g = bs.getDrawGraphics();
		//esse metodo desenha o retangulo na tela.  drawRectangleExample(xx, yy);
		g.drawImage(image,0,0,WIDTH*SCALE, HEIGHT*SCALE, null);
		g.setFont(new Font("arial", Font.BOLD,25));
		g.setColor(Color.white);
		g.drawString("Munição: " + Player.ammo , 510, 25);
		/*aqui eu estou testando a minha font para ver se ela está funcionando*/
		//g.setFont(newfont);
		//g.drawString("testezin com a fonte", 20, 80);
		if(gameState == "GAME_OVER") {
			/*aqui eu estou chamando o metodo graphics2D, por conta de no final
			 do setColor dele, ele nos permite setar uma opacidade*/
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(0,0,0,101));
			/*aqui estou fazendo um retangulo*/
			g2.fillRect(0, 0, WIDTH*SCALE, HEIGHT*SCALE);
			/*aqui eu to setando o meu game over quando meu player morre*/
			g2.setFont(new Font("arial", Font.BOLD,50));
			g2.setColor(Color.white);
			g2.drawString("GAME OVER" , 210, 240);
			g2.setFont(new Font("arial", Font.BOLD,40));
			if(showmessageGameOver)
			g2.drawString(">Precione ENTER para reininciar<" , 56, 310);
		}else if(gameState == "MENU") {
			menu.render(g);
		}
		/*metodo de rotacionar objetos*/
		//Graphics2D g2 = (Graphics2D) g;
		/*aqui utilizamos o Math.atan2, que já utilizamos para que o nosso jogador atira
		 com base na posição do mouse, e usamos de novo o atan2 para pegar em radianos
		 com base na posição do  mouse e utilizamos o metodo do Graphics2D o rotate
		 e já jagamos direto o angle mouse para ele rodar com a posição do mouse. Aqui
		 é tudo retornado em Radianos, se e u quiser saber o angulo eu uso o 
		 (		double angle = Math.toDegrees(angleMouse);) e dou um (System.out.println(Math.toDegrees(angleMouse);)*/
		//double angleMouse = Math.atan2(200 +25 - my, 200 +25 - mx );
		//g2.rotate(angleMouse, 200 + 25 ,200 + 25);
		//g.setColor(Color.black);
		//g.fillRect(200, 200, 50, 50);
		
		/*o BS.SHOW, serve meio que para mostrar os graficos na tela*/	
		bs.show();
	}
	
	
	public void run() {
		/*a gente pega o ultimo momento o lasttime em nano time por causa de precisão */
		/*o System.nanotime(); pega o tempo atual do meu computador em nanosegundo e isso é feito
para ter precisão*/
		long lastTime =  System.nanoTime();
		/*aqui eu defino algumas constantes apesar de não esta no formato constante, isso aqui a gente nao
altera é 60 ticks por segundo é 60 atualizações em a cada segundo */
		double amountOfTicks = 60.0;
		/*NS é um calculo que eu faço para pegar o momento certo em fazer o update do jogo isso é meio que
uma constante não vamos fica trocando o fps durante o jogo */
		/*Dessa maneira eu estou dividindo um segundo no formato de nano. o meu ns sera o que eu vou 
 utilizar para fazer o calculo do momento certo que o meu jogo tem que fazer o tick dar o update*/
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
/*aqui é feito o metodo pra ver a quantos fps o jogo esta rodando*/
		int frames = 0;
/*isso aqui faz a mesma coisa que o primeiro metodo, mas é um formato menos preciso e mais leve 
 para o computador*/
		double timer = System.currentTimeMillis();
		requestFocus();
	/*o while é o metodo mais facil de se fazer um game loop */
		/*E aqui começa a "magica" porque começa o loop aqui, temos o now, eu diminuo o tempo atual (now)
menos a ultima vez (lasttime), porque de fato ocorre um pequeno intervalo dp primeiro metodo
para cá por isso fazemos o ((now - lastTime) / ns) */
		while(isRunning) {
	/*agora eu vou ter um long que vai ser now */
			long now = System.nanoTime();
	/*agora delta vai ser mais ou igual o meu now menos o lastTime dividido pelo NS, que quer dizer
quando que deve ser o nosso intervalo para executar o tick, em algum momento o delta vai ser 1 é
o momento que eu quero que de o tick para assim fechar a 60 ticks por segundo*/
			delta+= (now - lastTime) / ns;
		/*agora o lasttime = now fazendo o loop infinitas vezes. Assim que funciona o game loop*/
			lastTime = now;
	/*aqui eu to dizendo que quando meu delta for maior ou igual a 1 é que passou um segundo
certo, mas não é sempre que vai dar 1 segundo certinho as vezes o computador pode atrazar por N motivos
mas de qualquer maneira se for maior ou igual a 1 ta na hora de atualizar e renderizar o jogo */
			if(delta >= 1) {
			/*como foi dito no começo "sempre atualize o jogo antes de renderizar"*/
				tick();
			
				render();
			frames ++;
			/*para dizer que eu cheguei no 1 ou maior  ai eu vou decrementa, e voltamos de novo no looping*/
			delta --;
			}
	/*isso é um calculo realizado para mostrar o fps no console*/
			if(System.currentTimeMillis() - timer >=- 1000) {
				System.out.println("FPS: " + frames);
			/*e aqui vai rezetar os frames porque eu ja o mostrei no console*/	
				frames = 0;
				timer += 1000;
			}
		}
		/*essa função serve para paralizar a threads*/
		stop();
		
		
	}

	/*nessa classe nós conseguimos falar as teclas precionadas*/
	public void keyTyped(KeyEvent e) {
		
		
	}
/*aqui nada mais é que a classe que fazemos o java reconhecer a tecla que estamos apertando */
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_Z) {
			player.jump = true;
		}
		/*quando está TRUE, o player vai para o lado que eu aperta a tecla, quando é false quer dizer que eu soltei a
tecla*/
		//aqui estou dizendo para que quando eu aperda a seta direita o meu player tem que ir para direita
		if(e.getKeyCode() == KeyEvent.VK_RIGHT ||
			e.getKeyCode() == KeyEvent.VK_D) {
			player.right = true;
			//aqui estou dizendo para que quando eu aperda a seta esquerda o meu player tem que ir para esquerda
		}else if(e.getKeyCode() == KeyEvent.VK_LEFT ||
				e.getKeyCode() == KeyEvent.VK_A) {
			player.left = true;
		}
		//aqui estou dizendo para que quando eu aperda a seta UP o meu player tem que ir para cima
		if(e.getKeyCode() == KeyEvent.VK_UP ||
				e.getKeyCode() == KeyEvent.VK_W) {
				player.up = true;
				//aqui estou dizendo para que quando eu aperda a seta DOWN o meu player tem que ir para baixo
			}else if(e.getKeyCode() == KeyEvent.VK_DOWN ||
					e.getKeyCode() == KeyEvent.VK_S) {
				player.down = true;
			}
		if(e.getKeyCode() == KeyEvent.VK_X) {
			player.shoot = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			this.restartGame = true;
			if(gameState == "MENU") {
				menu.enter = true;
			}
		}
		
		
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			/*apenas aqui aparece o Continue*/
			gameState = "MENU";
			menu.pause = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			if(Game.gameState == "NORMAL")
			this.saveGame = true;
		}
		
	}
	/*já aqui vamos fala que quando a tecla for solta é para o player parar no lugar*/

	public void keyReleased(KeyEvent e) {
		//aqui estou dizendo que quando eu soltar a seta direita o meu player tem que parar
		if(e.getKeyCode() == KeyEvent.VK_RIGHT ||
				e.getKeyCode() == KeyEvent.VK_D) {
				player.right = false;
				//aqui estou dizendo que quando eu soltar a seta esquerda o meu player tem que parar
			}else if(e.getKeyCode() == KeyEvent.VK_LEFT ||
					e.getKeyCode() == KeyEvent.VK_A) {
				player.left = false;
			}
		//aqui estou dizendo que quando eu soltar a seta UP o meu player tem que parar
			if(e.getKeyCode() == KeyEvent.VK_UP ||
					e.getKeyCode() == KeyEvent.VK_W) {
					player.up = false;
					/*essa aqui é uma mini verificação de movimentação para cima*/
					if(gameState == "MENU") {
						menu.up = true;
					}
					//aqui estou dizendo que quando eu soltar a seta DOWN o meu player tem que parar
				}else if(e.getKeyCode() == KeyEvent.VK_DOWN ||
						e.getKeyCode() == KeyEvent.VK_S) {
					player.down = false;
					/*essa aqui é uma mini verificação de movimentação para baixo*/
					if(gameState == "MENU") {
						menu.down = true;
					}
				}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// vo usa esse evento de click do mouse
		player.mouseShoot = true;
		/*esses dois metodos vão me recuperar a posição
		 do mouse*/
		player.mx = (e.getX() / 3) ;
		player.my = (e.getY() / 3) ;
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	/*esse metodo detecta a movimentação do meu mouse*/
	public void mouseMoved(MouseEvent e) {
		this.mx = e.getX();
		this.my = e.getY();
	}

}
