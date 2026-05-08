package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Similar to the player class but defines an update method that handles some simple automatic movement.
 */
public class Zombie {
	private int x, y;
	private int width, height;
	private int startX, startY;
	private BufferedImage sprite;
	private int gameWidth, gameHeight;
	private int dx = -3;
	private int dy = 2;
	private boolean facingRight;
	
	public Zombie(int startX, int startY, int width, int height, int gameWidth, int gameHeight) {
		this.x = startX;
		this.y = startY;
		this.width = width;
		this.height = height;
		this.startX = startX;
		this.startY = startY;
		this.gameWidth = gameWidth;
		this.gameHeight = gameHeight;
		this.facingRight = false;
		
		sprite = loadSprite();
	}

	private BufferedImage loadSprite() {
		try {
			var resource = Zombie.class.getResource("/sprites/zombie.png");
			if (resource != null) {
				return ImageIO.read(resource);
			}
			return ImageIO.read(new File("src/sprites/zombie.png"));
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
				g2.drawImage(sprite, x + width, y, -width, height, null);
			} else {
				g2.drawImage(sprite, x, y, width, height, null);
			}
		} else {
			g2.setColor(Color.GREEN);
			g2.fillRect(x, y, width, height);
		}
	}
	
	public void update() {
		x += dx;
		y += dy;
		
		// LEFT / RIGHT walls
		if (x <= 0) {
			x = 0;
			dx = -dx;
		}
		
		if (x + width >= gameWidth) {
			x = gameWidth - width;
			dx = -dx;
		}
		
		// TOP / BOTTOM walls
		if (y <= 0) {
			y = 0;
			dy = -dy;
		}
		
		if (y + height >= gameHeight) {
			y = gameHeight - height;
			dy = -dy;
		}

		if (dx > 0) {
			facingRight = true;
		} else if (dx < 0) {
			facingRight = false;
		}
	}
	
	public void reset() {
		this.x = startX;
		this.y = startY;
	}
}
