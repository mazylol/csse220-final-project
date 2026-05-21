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
 * Represents the player avatar and its state in the game world.
 *
 * Fields: x, y, width, height, startX, startY, sprite, gameWidth, gameHeight,
 * facingRight, health, damageTime.
 * Methods: Player(...), loadSprite(), getX(), getY(), drawOn(...), moveBy(...),
 * reset(), getHealth(), handleZombieCollision(), handleDamage().
 */
public class Player {
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
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}

	public boolean isFacingRight() {
		return facingRight;
	}
	
	public FacingDirection getFacingDirection() {
		return facingDirection;
	}
	
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
	
	public void reset() {
		this.x = startX;
		this.y = startY;
	}
	
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
	
	// this basically boosts the red channel of the player an dims the others
	private BufferedImage createDamageTint(BufferedImage source) {
		float[] scales = {1.4f, 0.7f, 0.7f, 1f};
		float[] offsets = {0f, 0f, 0f, 0f};
		RescaleOp op = new RescaleOp(scales, offsets, null);
		return op.filter(source, null);
	}
}
