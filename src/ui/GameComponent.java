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
 * Main rendering panel for the game world and HUD.
 */
public class GameComponent extends JPanel {
	public static final int WIDTH = 600;
	public static final int HEIGHT = 600;
	public static final Color BG = Color.WHITE;
	public static final Color FG = Color.BLACK;

	private final GameModel model;
	private BufferedImage floorSprite;
	private BufferedImage wallSprite;
	private BufferedImage gemSprite;
	private BufferedImage doorSprite;
	private BufferedImage keySprite;
	private Timer timer;
	public static int GameTime;
	
	/**
	 * Creates the game component and loads sprites.
	 *
	 * @param model game model driving the render state
	 */
	public GameComponent(GameModel model) {
		this.model = model;
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.setBackground(BG);
		this.setOpaque(true);
		
		floorSprite = loadResource("/sprites/floor.png", "src/sprites/floor.png");
		wallSprite = loadResource("/sprites/wall.png", "src/sprites/wall.png");
		gemSprite = loadResource("/sprites/gem.png", "src/sprites/gem.png");
		doorSprite = loadResource("/sprites/door.png", "src/sprites/door.png");
		keySprite = loadResource("/sprites/key.png", "src/sprites/key.png");
		
		timer = new Timer(30, e -> {
			model.update();
			model.checkZombieCollision();
			repaint();
			GameTime++;
		});
		//timer.start();
	}
	
	/**
	 * Starts the animation timer.
	 */
	public void startTimer() {
	    timer.start();
	}	
	
	/**
	 * Restarts the game state and redraws the screen.
	 */
	public void restartGame() {
		timer.stop();
		GameTime = 0;
		model.restartGame();
		repaint();
		timer.start();
	}
	
	/**
	 * @return the current game time in ticks
	 */
	public static int getTime() {
		return GameTime;
	}
	
	private BufferedImage loadResource(String resourcePath, String filePath) {
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

	/**
	 * Paints the current game frame including level, actors, and HUD.
	 *
	 * @param g graphics context
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		g2.setColor(BG);
		g2.fillRect(0, 0, WIDTH, HEIGHT);

		drawLevel(g2);
		
		model.getPlayer().drawOn(g2);
		for (Zombie zombie : model.getZombies()) {
			zombie.drawOn(g2);
			if (model.isZombieInAttackRange(zombie)) {
				g2.setColor(new Color(255, 220, 0, 180));
				for (int i = 0; i < 3; i++) {
					g2.drawRect(zombie.getX() - i, zombie.getY() - i,
							GameModel.ZOMBIE_SIZE - 1 + 2 * i, GameModel.ZOMBIE_SIZE - 1 + 2 * i);
				}
			}
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
					case Key -> drawWithFallback(g2, keySprite, x, y, Color.YELLOW);
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
		g2.fillRoundRect(8, 8, 220, 70, 8, 8);
		g2.setColor(Color.WHITE);
		g2.setFont(new Font("SansSerif", Font.BOLD, 13));
		g2.drawString("Gems left: " + model.getGemsRemaining(), 16, 30);
		g2.drawString("Key: " + (model.hasKey() ? "Yes" : "No"), 16, 48);
		g2.drawString("Health: " + model.getHealth(), 16, 66);
		if (model.hasWon()) {
			g2.setColor(new Color(20, 150, 20));
			g2.setFont(new Font("SansSerif", Font.BOLD, 28));
			g2.drawString("LEVEL COMPLETE", 180, 320);
			g2.setFont(new Font("SansSerif", Font.BOLD, 16));
			g2.setColor(Color.WHITE);
			g2.drawString("Press R to restart", 220, 350);
			if(model.currentLevel == 1) {g2.drawString("Press N for next Level", 206, 375);}
		}else if(model.hasLost()) {
			g2.setColor(Color.RED);
			g2.setFont(new Font("SansSerif", Font.BOLD, 28));
			g2.drawString("GAME OVER", 200,320);
			g2.setFont(new Font("SansSerif", Font.BOLD, 16));
			g2.setColor(Color.WHITE);
			g2.drawString("Press R to restart", 220, 350);
		}
	}
}
