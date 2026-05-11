package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ui.GameComponent;

/**
 * Holds the current position, dimensions, start position, sprite and the game window dimensions (used for collisions).
 * 
 * Has getters for position. Methods for drawing and movement.
 */
public class Player {
	private int x, y;
	private int width, height;
	private int startX, startY;
	private BufferedImage sprite;
	private int gameWidth, gameHeight;
	private boolean facingRight;
	private int health;
	private int damageTime;
	
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
		this.health = 3;
		sprite = loadSprite();
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
	
	public void drawOn(Graphics2D g2) {
		if (sprite != null) {
			if (facingRight) {
				g2.drawImage(sprite, x, y, width, height, null);
			} else {
				g2.drawImage(sprite, x + width, y, -width, height, null);
			}
		} else {
			g2.setColor(Color.BLUE);
			g2.fillRect(x, y, width, height);
		}
	}
	
	public void moveBy(int dx, int dy) {
		if (dx > 0) {
			facingRight = true;
		} else if (dx < 0) {
			facingRight = false;
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
	public void handleZombieCollision() {
		if(GameComponent.getTime()-30>=damageTime) {
			this.handleDamage();
			damageTime=GameComponent.getTime();
		}else {
		}
	}
	public void handleDamage() {
		if(this.health>1) {
			health--;
		}
		else if(this.health==1) {
			health = 0;
			GameModel.GameOver();
		}
	}
}
