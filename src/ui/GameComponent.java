package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

import model.GameModel;
import model.Zombie;

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
	private BufferedImage floorSprite;
	private BufferedImage wallSprite;
	private BufferedImage gemSprite;
	private BufferedImage doorSprite;
	private Timer timer;

	public GameComponent(GameModel model) {
		this.model = model;
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.setBackground(BG);
		this.setOpaque(true);
		
		background = loadBackground();
		floorSprite = loadSprite("/sprites/floor.png", "src/sprites/floor.png");
		wallSprite = loadSprite("/sprites/wall.png", "src/sprites/wall.png");
		gemSprite = loadSprite("/sprites/gem.png", "src/sprites/gem.png");
		doorSprite = loadSprite("/sprites/door.png", "src/sprites/door.png");
		
		timer = new Timer(30, e -> {
			model.update();
			model.checkZombieCollision();
			repaint();
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

	private BufferedImage loadSprite(String resourcePath, String filePath) {
		try {
			var resource = GameComponent.class.getResource(resourcePath);
			if (resource != null) {
				return ImageIO.read(resource);
			}
			return ImageIO.read(new File(filePath));
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

		drawLevel(g2);
		
		model.getPlayer().drawOn(g2);
		for (Zombie zombie : model.getZombies()) {
			zombie.drawOn(g2);
		}
		drawHud(g2);
	}

	private void drawLevel(Graphics2D g2) {
		ArrayList<ArrayList<GameModel.Item>> level = model.getLevel();
		for (int row = 0; row < level.size(); row++) {
			for (int col = 0; col < level.get(row).size(); col++) {
				int x = col * GameModel.TILE_SIZE;
				int y = row * GameModel.TILE_SIZE;
				drawFloorTile(g2, x, y);

				GameModel.Item item = level.get(row).get(col);
				switch (item) {
					case Wall -> drawWithFallback(g2, wallSprite, x, y, Color.DARK_GRAY);
					case Gem -> drawWithFallback(g2, gemSprite, x, y, Color.CYAN);
					case Key -> {
						g2.setColor(Color.YELLOW);
						g2.fillOval(x + 10, y + 10, GameModel.TILE_SIZE - 20, GameModel.TILE_SIZE - 20);
					}
					case Exit -> drawWithFallback(g2, doorSprite, x, y, model.hasKey() ? new Color(70, 180, 70) : new Color(170, 90, 50));
					default -> {
					}
				}
			}
		}
	}

	private void drawFloorTile(Graphics2D g2, int x, int y) {
		if (floorSprite != null) {
			g2.drawImage(floorSprite, x, y, GameModel.TILE_SIZE, GameModel.TILE_SIZE, null);
		} else {
			g2.setColor(new Color(245, 245, 245));
			g2.fillRect(x, y, GameModel.TILE_SIZE, GameModel.TILE_SIZE);
		}
	}

	// draw function with a fallback to drawing a simple color, just in case the sprite did not load
	private void drawWithFallback(Graphics2D g2, BufferedImage sprite, int x, int y, Color fallbackColor) {
		if (sprite != null) {
			g2.drawImage(sprite, x, y, GameModel.TILE_SIZE, GameModel.TILE_SIZE, null);
		} else {
			g2.setColor(fallbackColor);
			g2.fillRect(x, y, GameModel.TILE_SIZE, GameModel.TILE_SIZE);
		}
	}

	private void drawHud(Graphics2D g2) {
		g2.setColor(new Color(0, 0, 0, 140));
		g2.fillRoundRect(8, 8, 220, 56, 8, 8);
		g2.setColor(Color.WHITE);
		g2.setFont(new Font("SansSerif", Font.BOLD, 13));
		g2.drawString("Gems left: " + model.getGemsRemaining(), 16, 30);
		g2.drawString("Key: " + (model.hasKey() ? "Yes" : "No"), 16, 48);
		if (model.hasWon()) {
			g2.setColor(new Color(20, 150, 20));
			g2.setFont(new Font("SansSerif", Font.BOLD, 28));
			g2.drawString("LEVEL COMPLETE", 180, 320);
		}
	}
}
