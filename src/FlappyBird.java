/*
	==> Recriando o Flappy Bird // Recreating Flappy Bird <== 
	
	This project is not for profit, as much as degradation of the original project, 
	it served as a practice for improving knowledge and in this way improved.
	
	Based in project of teacher Fabio Mascarenhas. 
	--> All rights reserved.
	Official video of the project: https://www.youtube.com/watch?v=VerqbgKmkM0&ab_channel=SweetDreams 
	--> All rights reserved.
	 
	Author: Thiago Hercules de Aguiar Silva. // Dev. Fullstack junior.
	
	==> Functions in the application: 
		--> Game over screen 
		--> HitBox 
		--> Flappy configuration
		--> Configuration of 2D graphic motor 
			
CONTACTS AND INFOS:
|--> e-mail: thhercules2012@gmail.com
|--> LinkedIn: https://www.linkedin.com/in/thiago-hercules-2669901ba/
|--> GitHub: https://github.com/Thercules
|--> Personal Instagram: https://www.instagram.com/t.hercules02/
|--> Professional Instagram: https://www.instagram.com/tdesigner._/
|--> Behance Portfolio: https://www.behance.net/thercules/appreciated

=====================			=====================			=====================
Copy is not permited.			Copy is not permited.			Copy is not permited.
=====================			=====================			=====================
*/

import java.util.Set;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBird implements Jogo{
	
	public Passaro bird;
	public Random gerador = new Random();
	public int record = 0;
	public ScoreNumber scorenumber;
	
	public int game_state = 0; 
	
	//Numerações: 
	//0 --> Tela de inicio
	//1 --> Get Ready 
	//2 --> Jogo
	//3 --> Tela de game over
	
	public double scenario_offset = 0;
	public double ground_offset = 0;
	public ArrayList<Cano> canos = new ArrayList<Cano>();
	public Timer pipetimer;
	public Hitbox groundbox;
	
	public Timer auxtimer;
	
	
	private Acao addCano(){
		return new Acao(){
			public void executa(){
				canos.add(new Cano(getLargura(),gerador.nextInt(getAltura()-112-Cano.HOLESIZE)));
			}
		};
	}
	
	private Acao proxCena(){
		return new Acao(){
			public void executa(){
				game_state += 1;
				game_state = game_state%4;
			}
		};
	}
	
	
	
	public FlappyBird(){
		bird = new Passaro(50,getAltura()/4);
		pipetimer = new Timer(2,true,addCano());
		scorenumber = new ScoreNumber(0);
		groundbox = new Hitbox(0, getAltura()-112, getLargura(), getAltura());
		
		
	}

	public String getTitulo(){
		return "Flappy Bird";
	}
	public String getAuthor(){
		return "Érick Ávila da Silva | DRE:113140496";
	}
	public int getLargura(){
		return 384;
	}
	public int getAltura(){
		return 512;
	}
	
	public void gameOver(){
		canos = new ArrayList<Cano>();
		bird = new Passaro(50,getAltura()/4);
		proxCena().executa();
	}
	
	public void tique(Set<String> keys, double dt){
		scenario_offset += dt*25;
		scenario_offset = scenario_offset % 288;
		ground_offset += dt*100;
		ground_offset = ground_offset%308;
		
		switch(game_state){
		case 0: //Tela de inicio
			break;
		case 1: //Get Ready
			auxtimer.tique(dt);
			bird.updateSprite(dt);
			break;
		case 2: //Jogo
			pipetimer.tique(dt);
			bird.update(dt);
			bird.updateSprite(dt);
			if(groundbox.intersecao(bird.box)!=0){
				gameOver();
				return;
			}
			if(bird.y<-5){
				gameOver();
				return;
			}
			for(Cano cano: canos){
				cano.tique(dt);
				if(cano.boxcima.intersecao(bird.box)!=0 || cano.boxbaixo.intersecao(bird.box)!=0){
					if(scorenumber.getScore()>ScoreNumber.record){
						ScoreNumber.record = scorenumber.getScore();
					}
					gameOver();
					return;
				}
				if(!cano.counted && cano.x < bird.x){
					cano.counted = true;
					scorenumber.modifyScore(1);;
				}
			}
			if(canos.size() > 0 && canos.get(0).x < -70){
				canos.remove(0);
			}
			
			break;
		case 3: //Tela de game over
			break;
		}
	}
	public void tecla(String c){
		switch(game_state){
		case 0:
			if(c.equals(" ")){
				auxtimer = new Timer(1.6, false, proxCena());
				proxCena().executa();
			}
			break;
		case 1:
			break;
		case 2:
			if(c.equals(" ")){
				bird.flap();
			}
			break;
		case 3:
			if(c.equals(" ")){
				scorenumber.setScore(0);
				proxCena().executa();
			}
			break;
		}
	}

	public void desenhar(Tela t){
		//background
		t.imagem("flappy.png", 0, 0, 288, 512, 0,(int) -scenario_offset, 0);
		t.imagem("flappy.png", 0, 0, 288, 512, 0, (int) (288 - scenario_offset), 0);
		t.imagem("flappy.png", 0, 0, 288, 512, 0, (int) ((288*2) - scenario_offset), 0);
		
		for(Cano cano: canos){
			cano.drawItself(t);
		}
		
		//desenhando o background
		t.imagem("flappy.png", 292, 0, 308, 112, 0, -ground_offset, getAltura()-112);
		t.imagem("flappy.png", 292, 0, 308, 112, 0, 308 -ground_offset, getAltura()-112);
		t.imagem("flappy.png", 292, 0, 308, 112, 0, (308*2) - ground_offset, getAltura()-112);
		
		switch(game_state){
		case 0:
			t.imagem("flappy.png", 292, 346, 192, 44, 0, getLargura()/2 - 192/2, 100);
			t.imagem("flappy.png", 352, 306, 70, 36, 0, getLargura()/2 - 70/2, 175);
			t.texto("Pressione espaço", 60, getAltura()/2 - 16, 32, Cor.BRANCO);
			break;
		case 1:
			bird.drawItself(t);
			t.imagem("flappy.png",292,442,174,44, 0, getLargura()/2 - 174/2, getAltura()/3);
			scorenumber.drawScore(t, 5, 5);
			break;
		case 2:
			scorenumber.drawScore(t, 5, 5);
			bird.drawItself(t);
			break;
		case 3:
			t.imagem("flappy.png", 292, 398, 188, 38, 0, getLargura()/2 - 188/2, 100);
			t.imagem("flappy.png", 292, 116, 226, 116, 0, getLargura()/2 - 226/2, getAltura()/2 - 116/2);
			scorenumber.drawScore(t, getLargura()/2 + 50, getAltura()/2-25);
			scorenumber.drawRecord(t, getLargura()/2 + 55, getAltura()/2 + 16);
			break;
		}		
	}
	public static void main(String[] args) {
        roda();
    }
    
    private static void roda() {
    	new Motor(new FlappyBird());
    }
	
}
