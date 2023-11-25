package MainGame;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.GraphicAttribute;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import Objects.SuperObject;
import entity.Clone;
import entity.Entity;
import entity.Player;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable{
	//SCREEN SETTING
	private final int originalTileSize = 16; //16x16 tile
	private final int scale = 3; //tăng kích cỡ 3 lần
	
	public final int tileSize = originalTileSize * scale;
	public int maxScreenCol = 25 + 1; //có 25 cột (không phải 26 đâu)
	public int maxScreenRow = 15; //có 15 hàng
	private final int screenWidth = tileSize * (maxScreenCol - 1); 
	private final int screenHeight = tileSize * maxScreenRow;

	public int stt;
	public boolean[] moveobj = new boolean[20];
	public boolean[] upobj = new boolean[20];
	public int sttobj;
	public int quantityClone = 0;
	public int level = 1;
	
	//FPS
	int FPS = 60;
	
	public TileManager tileM = new TileManager(this);
	public KeyHandler keyH = new KeyHandler();
	public Thread gameThread;
	public CollisionChecker cChecker = new CollisionChecker(this);
	public AssetSetter aSetter = new AssetSetter(this);
	private CheckDead checkDead = new CheckDead(this);
	public Player player = new Player(this, keyH);
	public Clone[] clone = new Clone[5];
	public SuperObject obj[] = new SuperObject[20];
	public String[][] movXClone = new String[5][6000000];
	public String[][] movYClone = new String[5][6000000];
	
	
	public BufferedImage img;
	public BufferedImage[] raineffect;
	public int effectSpeed = 5, effectTick, effectIndex;

	
	public GamePanel() {
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setBackground(Color.black);
		this.raineffect = new BufferedImage[8];
		addEffect();
		try {
			img = ImageIO.read(getClass().getResourceAsStream("/ui/background.png"));

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.setFocusable(true);
	}
	
	private void addEffect() {
		try {
			raineffect[0] = ImageIO.read(getClass().getResourceAsStream("/ui/rain1.png"));

		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			raineffect[1] = ImageIO.read(getClass().getResourceAsStream("/ui/rain2.png"));

		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			raineffect[2] = ImageIO.read(getClass().getResourceAsStream("/ui/rain3.png"));
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			raineffect[3] = ImageIO.read(getClass().getResourceAsStream("/ui/rain4.png"));
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			raineffect[4] = ImageIO.read(getClass().getResourceAsStream("/ui/rain5.png"));
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			raineffect[5] = ImageIO.read(getClass().getResourceAsStream("/ui/rain6.png"));
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			raineffect[6] = ImageIO.read(getClass().getResourceAsStream("/ui/rain7.png"));
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			raineffect[7] = ImageIO.read(getClass().getResourceAsStream("/ui/rain8.png"));
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setupGame() {
		aSetter.set();
	}
	
	public void startGameThread() {
		gameThread = new Thread(this);
		gameThread.start();
	}

	@Override
	public void run() {
		double drawInterval = 1000000000/FPS;
		double nextDrawTime = System.nanoTime() + drawInterval;
		
		while(gameThread != null) {
			// 1 UPDATE: update in4 such as character positions
			update();
			// 2 UPDATE: draw screen with the updated in4
			repaint();
				
			try {
				double remainingTime = nextDrawTime - System.nanoTime();
				remainingTime /= 1000000;
				
				if(remainingTime < 0) {
					remainingTime = 0;
				}
				
				Thread.sleep((long) remainingTime);
				
				nextDrawTime += drawInterval;
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void setSolid(Entity entity, SuperObject obj) {
		entity.solidArea.x += entity.worldX;
		entity.solidArea.y += entity.worldY;
		obj.solidArea.x = obj.worldX + obj.solidArea.x;
		obj.solidArea.y = obj.worldY + obj.solidArea.y;
	}
	public void setDefaultSolid(Entity entity, SuperObject obj) {
		 entity.solidArea.x = entity.solidAreaDefaultX;
		 entity.solidArea.y = entity.solidAreaDefaultY;
		 obj.solidArea.x = obj.solidAreaDefaultX;
		 obj.solidArea.y = obj.solidAreaDefaultY; 
	}
	
	public void update() {
		for(int i=0; i<5; i++) upobj[i] = false;
//		player.dead = checkDead.checkDeadPlayer(player);
		if(player != null) {
			if(player.dead == false) {
				player.dead = checkDead.checkDeadPlayer(player);
				if(player.dead == true) player.aniIndex = 0;
			}
			player.update();
//			System.out.println(player.speed);
		}

		for(int i=0; i<5; i++) {
//			clone[i].dead = checkDead.checkDeadPlayer(clone[i]);
			if(clone[i] != null) {
				if(clone[i].dead == false) {
					clone[i].dead = checkDead.checkDeadPlayer(clone[i]);
					if(clone[i].dead == true) clone[i].aniIndex = 0;
				}
				stt = i;
				clone[i].update();
			}
		}

		for(int i=0; i<20; i++) {
			if(obj[i] != null && upobj[i] == false) {
				sttobj = i;
				obj[i].update();
			}
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(img, 0, 0, screenWidth, screenHeight, null);
		
		
		effectTick ++;
		if(effectTick >= effectSpeed) {
			effectTick = 0;
			effectIndex ++;
		}
		if(effectIndex >= 8) {
			effectIndex = 0;
		}	
		g2.drawImage(raineffect[effectIndex], 0, 0, screenWidth, screenHeight, null);
		tileM.draw(g2);
		for(int i=0; i<obj.length; i++) {
			if(obj[i] != null) {
				obj[i].draw(g2, this);
			}
		}
		
		for(int i=0; i<5; i++) {
			if(clone[i] != null) {
				clone[i].draw(g2);
			}
		}
		if(player != null) player.draw(g2);
		
		g2.dispose();
	}
	

}
