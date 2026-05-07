package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

import model.GameModel;

/**
 * The main game component. Defined the window width/height as well as the colors. Also starts a timer.
 * 
 * Calls the method for calling the drawing methods for Player and Zombie.
 */
public class GameComponent extends JPanel {
	public static final int WIDTH = 600;
	public static final int HEIGHT = 600;
	public static final Color BG = Color.WHITE;
	public static final Color FG = Color.BLACK;

	private final GameModel model;
	private BufferedImage background;
	private Timer timer;

	public GameComponent(GameModel model) {
		this.model = model;
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.setBackground(BG);
		this.setOpaque(true);
		
		background = loadBackground();
		
		timer = new Timer(30, e -> {
			model.update();
			repaint();
			model.checkZombieCollision();
		});
		timer.start();
	}
	
	private BufferedImage loadBackground() {
		try {
			var resource = GameComponent.class.getResource("/sprites/background.png");
			if (resource != null) {
				return ImageIO.read(resource);
			}
			return ImageIO.read(new File("src/sprites/background.png"));
		} catch (IOException e) {
			return null;
		}
	}


	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		if (background != null) {
			g2.drawImage(background, 0, 0, WIDTH, HEIGHT, null);
		} else {
			g2.setColor(BG);
			g2.fillRect(0, 0, WIDTH, HEIGHT);
		}
		
		model.getPlayer().drawOn(g2);
		model.getZombie().drawOn(g2);
	}
}
