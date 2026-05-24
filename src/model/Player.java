package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ui.GameComponent;

/**
 * Represents the player sprite, including movement, rendering, and health state.
 */
public class Player {
	/** Cardinal directions used for facing and attack orientation. */
	public enum FacingDirection {
		UP,
		DOWN,
		LEFT,
		RIGHT
	}

	private static final int DAMAGE_COOLDOWN_TICKS = 30;

	private int x, y;
	private int width, height;
	private int startX, startY;
	private BufferedImage sprite;
	private BufferedImage damagedSprite;
	private int gameWidth, gameHeight;
	private boolean facingRight;
	private FacingDirection facingDirection;
	private int health;
	private int damageTime;
	private boolean recentlyDamaged;
	
	/**
	 * Creates a player with a starting position and game bounds.
	 *
	 * @param startX starting x coordinate in pixels
	 * @param startY starting y coordinate in pixels
	 * @param width width of the player sprite in pixels
	 * @param height height of the player sprite in pixels
	 * @param gameWidth width of the playable area in pixels
	 * @param gameHeight height of the playable area in pixels
	 */
	public Player(int startX, int startY, int width, int height, int gameWidth, int gameHeight) {
		this.x = startX;
		this.y = startY;
		this.width = width;
		this.height = height;
		this.startX = startX;
		this.startY = startY;
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
		this.facingRight = true;
		this.facingDirection = FacingDirection.RIGHT;
		this.health = 3;
		sprite = loadSprite();
		damagedSprite = sprite == null ? null : createDamageTint(sprite);
	}
	
	private BufferedImage loadSprite() {
		try {
			var resource = Player.class.getResource("/sprites/player.png");
			if (resource != null) {
				return ImageIO.read(resource);
			}
			return ImageIO.read(new File("src/sprites/player.png"));
		} catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * @return current x coordinate in pixels
	 */
	public int getX() {
		return this.x;
	}
	
	/**
	 * @return current y coordinate in pixels
	 */
	public int getY() {
		return this.y;
	}

	/**
	 * @return true if the sprite is facing right
	 */
	public boolean isFacingRight() {
		return facingRight;
	}
	
	/**
	 * @return current facing direction for attacks and movement
	 */
	public FacingDirection getFacingDirection() {
		return facingDirection;
	}
	
	/**
	 * Draws the player sprite or fallback shape at the current position.
	 *
	 * @param g2 graphics context to draw onto
	 */
	public void drawOn(Graphics2D g2) {
		if (sprite != null) {
			BufferedImage spriteToDraw = isDamageCooldownActive() && damagedSprite != null ? damagedSprite : sprite;
			if (facingRight) {
				g2.drawImage(spriteToDraw, x, y, width, height, null);
			} else {
				g2.drawImage(spriteToDraw, x + width, y, -width, height, null);
			}
		} else {
			g2.setColor(isDamageCooldownActive() ? new Color(220, 40, 40) : Color.BLUE);
			g2.fillRect(x, y, width, height);
		}
	}
	
	/**
	 * Moves the player and clamps position to the game bounds.
	 *
	 * @param dx change in x position in pixels
	 * @param dy change in y position in pixels
	 */
	public void moveBy(int dx, int dy) {
		if (dx > 0) {
			facingRight = true;
			facingDirection = FacingDirection.RIGHT;
		} else if (dx < 0) {
			facingRight = false;
			facingDirection = FacingDirection.LEFT;
		} else if (dy > 0) {
			facingDirection = FacingDirection.DOWN;
		} else if (dy < 0) {
			facingDirection = FacingDirection.UP;
		}

		x += dx;
		y += dy;
		
		// Wall collision detection
		if (x < 0) x = 0;
		if (x + width > gameWidth) x = gameWidth - width;
		if (y < 0) y = 0;
		if (y + height > gameHeight) y = gameHeight - height;
	}
	
	/**
	 * Resets the player to the starting position.
	 */
	public void reset() {
		this.x = startX;
		this.y = startY;
	}
	
	/**
	 * @return current player health
	 */
	public int getHealth() {
		return health;
	}
	
	/**
	 * Applies contact damage with a short invulnerability cooldown.
	 */
	public void handleZombieCollision() {
		if (GameComponent.getTime() - damageTime >= DAMAGE_COOLDOWN_TICKS) {
			this.handleDamage();
			damageTime = GameComponent.getTime();
			recentlyDamaged = true;
		}
	}
	
	/**
	 * Decrements health and triggers game over when health reaches zero.
	 */
	public void handleDamage() {
		if(this.health>1) {
			health--;
		}
		else if(this.health==1) {
			health = 0;
			GameModel.GameOver();
		}
	}
	
	private boolean isDamageCooldownActive() {
		return recentlyDamaged && GameComponent.getTime() - damageTime < DAMAGE_COOLDOWN_TICKS;
	}
	
	// Boosts the red channel of the player and dims the others.
	private BufferedImage createDamageTint(BufferedImage source) {
		float[] scales = {1.4f, 0.7f, 0.7f, 1f};
		float[] offsets = {0f, 0f, 0f, 0f};
		RescaleOp op = new RescaleOp(scales, offsets, null);
		return op.filter(source, null);
	}
}
