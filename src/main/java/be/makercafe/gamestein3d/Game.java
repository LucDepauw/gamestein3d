/*
 	Gamestein3D is a java based 3D raycast game engine
    Copyright (C) 2020  Luc De pauw

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package be.makercafe.gamestein3d;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.studiohartman.jamepad.ControllerManager;

import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;

public class Game extends JFrame implements Runnable {

	private static final long serialVersionUID = 1L;
	private static final int WIDTH = 1280;
	private static final int HEIGHT = 960;
	public int mapWidth = 24;
	public int mapHeight = 24;
	private Thread thread;
	private boolean running;
	private BufferedImage image;
	public int[] pixels;
	public ArrayList<Texture> textures;
	public ArrayList<Sound> sounds;
	public Camera camera;
	public Screen screen;
	public int[][] map = { { 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 4, 4, 6, 4, 4, 6, 4, 6, 4, 4, 4, 6, 4 },
			{ 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4 },
			{ 8, 0, 3, 3, 0, 0, 0, 0, 0, 8, 8, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6 },
			{ 8, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6 },
			{ 8, 0, 3, 3, 0, 0, 0, 0, 0, 8, 8, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4 },
			{ 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 4, 0, 0, 0, 0, 0, 6, 6, 6, 0, 6, 4, 6 },
			{ 8, 8, 8, 8, 0, 8, 8, 8, 8, 8, 8, 4, 4, 4, 4, 4, 4, 6, 0, 0, 0, 0, 0, 6 },
			{ 7, 7, 7, 7, 0, 7, 7, 7, 7, 0, 8, 0, 8, 0, 8, 0, 8, 4, 0, 4, 0, 6, 0, 6 },
			{ 7, 7, 0, 0, 0, 0, 0, 0, 7, 8, 0, 8, 0, 8, 0, 8, 8, 6, 0, 0, 0, 0, 0, 6 },
			{ 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 6, 0, 0, 0, 0, 0, 4 },
			{ 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 6, 0, 6, 0, 6, 0, 6 },
			{ 7, 7, 0, 0, 0, 0, 0, 0, 7, 8, 0, 8, 0, 8, 0, 8, 8, 6, 4, 6, 0, 6, 6, 6 },
			{ 7, 7, 7, 7, 0, 7, 7, 7, 7, 8, 8, 4, 0, 6, 8, 4, 8, 3, 3, 3, 0, 3, 3, 3 },
			{ 2, 2, 2, 2, 0, 2, 2, 2, 2, 4, 6, 4, 0, 0, 6, 0, 6, 3, 0, 0, 0, 0, 0, 3 },
			{ 2, 2, 0, 0, 0, 0, 0, 2, 2, 4, 0, 0, 0, 0, 0, 0, 4, 3, 0, 0, 0, 0, 0, 3 },
			{ 2, 0, 0, 0, 0, 0, 0, 0, 2, 4, 0, 0, 0, 0, 0, 0, 4, 3, 0, 0, 0, 0, 0, 3 },
			{ 1, 0, 0, 0, 0, 0, 0, 0, 1, 4, 4, 4, 4, 4, 6, 0, 6, 3, 3, 0, 0, 0, 3, 3 },
			{ 2, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 1, 2, 2, 2, 6, 6, 0, 0, 5, 0, 5, 0, 5 },
			{ 2, 2, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 2, 2, 0, 5, 0, 5, 0, 0, 0, 5, 5 },
			{ 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 5, 0, 5, 0, 5, 0, 5, 0, 5 },
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5 },
			{ 2, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 5, 0, 5, 0, 5, 0, 5, 0, 5 },
			{ 2, 2, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 2, 2, 0, 5, 0, 5, 0, 0, 0, 5, 5 },
			{ 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 5, 5, 5, 5, 5, 5, 5, 5, 5 } };

	public Sprite[] sprite = { new Sprite(20.5, 11.5, 10), // green light in front of playerstart
			// green lights in every room
			new Sprite(18.5, 4.5, 10), new Sprite(10.0, 4.5, 10), new Sprite(10.0, 12.5, 10), new Sprite(3.5, 6.5, 10),
			new Sprite(3.5, 20.5, 10), new Sprite(3.5, 14.5, 10), new Sprite(14.5, 20.5, 10),

			// row of pillars in front of wall: fisheye test
			new Sprite(18.5, 10.5, 9), new Sprite(18.5, 11.5, 9), new Sprite(18.5, 12.5, 9),

			// some barrels around the map
			new Sprite(21.5, 1.5, 8), new Sprite(15.5, 1.5, 8), new Sprite(16.0, 1.8, 8), new Sprite(16.2, 1.2, 8),
			new Sprite(3.5, 2.5, 8), new Sprite(9.5, 15.5, 8), new Sprite(10.0, 15.1, 8), new Sprite(10.5, 15.8, 8), };
	
	public BufferedImage imageGun = null;
	
	public ControllerManager controllers = null;
	

	public Game() {
		thread = new Thread(this);
		//The java drawing buffer
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		//This is our pixel data
		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		//Initialize textures
		textures = new ArrayList<Texture>();
		textures.add(new Texture("/walld.png", 64));
		textures.add(new Texture("/redbrick.png", 64));
		textures.add(new Texture("/purplestone.png", 64));
		textures.add(new Texture("/greystone.png", 64));
		textures.add(new Texture("/bluestone.png", 64));
		textures.add(new Texture("/mossy.png", 64));
		textures.add(new Texture("/wood.png", 64));
		textures.add(new Texture("/colorstone.png", 64));
		textures.add(new Texture("/barrel.png", 64));
		textures.add(new Texture("/pillar.png", 64));
		textures.add(new Texture("/greenlight.png", 64));

		//Gun image drawn on directly on buffer in render method
		try {
			imageGun = ImageIO.read(getClass().getResourceAsStream("/gun_trans8x.png"));

		} catch (IOException e) {
			System.out.println("Error loading asset: /gun_trans8x.png");
			e.printStackTrace();
		}
		
		//Initialize sound library
		TinySound.init();		
		sounds = new ArrayList<Sound>();
		sounds.add(TinySound.loadSound("/normal_shotgun.wav"));
		
		//Initialize gamepad controller library
		try {
			controllers = new ControllerManager();
			controllers.initSDLGamepad();
		} catch(Exception ex) {
			ex.printStackTrace();
		} 
		camera = new Camera(22, 11.5, 1, 0, 0, -.66, sounds);
		screen = new Screen(map, mapWidth, mapHeight, textures, WIDTH, HEIGHT, sprite);
		addKeyListener(camera);
		setSize(WIDTH, HEIGHT);
		setResizable(false);
		setTitle("Gamestein3D engine");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBackground(Color.black);
		setLocationRelativeTo(null);
		setVisible(true);
		start();
	}

	private synchronized void start() {
		running = true;
		thread.start();
	}

	public synchronized void stop() {
		//Free up resources
		if(controllers != null) {
			controllers.quitSDLGamepad();
		}
		TinySound.shutdown();
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
		if(imageGun != null) {
			g.drawImage(imageGun, (WIDTH /2 ) - (imageGun.getWidth() / 2), HEIGHT - imageGun.getHeight(), imageGun.getWidth(), imageGun.getHeight(), null);
		}
		bs.show();
	}
	
	/**
	 * scale image
	 * 
	 * @param sbi image to scale
	 * @param imageType type of image
	 * @param dWidth width of destination image
	 * @param dHeight height of destination image
	 * @param fWidth x-factor for transformation / scaling
	 * @param fHeight y-factor for transformation / scaling
	 * @return scaled image
	 */
	public static BufferedImage scale(BufferedImage sbi, int dWidth, int dHeight, double fWidth, double fHeight) {
	    BufferedImage dbi = null;
	    if(sbi != null) {
	        dbi = new BufferedImage(dWidth, dHeight, sbi.getType());
	        Graphics2D g = dbi.createGraphics();
	        AffineTransform at = AffineTransform.getScaleInstance(fWidth, fHeight);
	        g.drawRenderedImage(sbi, at);
	    }
	    return dbi;
	}


	public void run() {
		long lastTime = System.nanoTime();
		final double ns = 1000000000.0 / 60.0;// 60 times per second
		double delta = 0;
		requestFocus();
		while (running) {
			long now = System.nanoTime();
			delta = delta + ((now - lastTime) / ns);
			lastTime = now;
			while (delta >= 1)// Make sure update is only happening 60 times a second
			{
				// handles all of the logic restricted time
				screen.update(camera, pixels);
				camera.update(map, controllers);

				delta--;
			}
			render();// displays to the screen unrestricted time
		}
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Game game = new Game();
	}
}