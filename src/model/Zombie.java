package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

/**
 * Represents an enemy that moves automatically and bounces around the map.
 *
 * Fields: x, y, width, height, startX, startY, sprite, gameWidth, gameHeight,
 * dx, dy, facingRight, direction, random.
 * Methods: Zombie(...), loadSprite(), getX(), getY(), drawOn(...), update(),
 * updateDirection(), reset(), getWidth(), getHeight(), getDX(), getDY(),
 * backTrack().
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
	private int direction; // 8 directions measured 1-8 counterclockwise starting with 1 pointing right
	public static Random random = new Random();
	private int knockbackRemainingX;
	private int knockbackRemainingY;
	private int knockbackStepX;
	private int knockbackStepY;
	
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
		updateDirection();
		
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
	
	/** Advances zombie movement and bounces off world boundaries. */
	public void update() {
		x += 3*dx;
		y += 2*dy;
		
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

	public void moveBy(int moveX, int moveY) {
		if (moveX > 0) {
			facingRight = true;
		} else if (moveX < 0) {
			facingRight = false;
		}

		x += moveX;
		y += moveY;
		if (x < 0) x = 0;
		if (x + width > gameWidth) x = gameWidth - width;
		if (y < 0) y = 0;
		if (y + height > gameHeight) y = gameHeight - height;
	}
	
	public void startKnockback(int totalX, int totalY, int durationTicks) {
		knockbackRemainingX = totalX;
		knockbackRemainingY = totalY;
		if (durationTicks <= 0) {
			knockbackStepX = totalX;
			knockbackStepY = totalY;
			return;
		}
		knockbackStepX = totalX == 0 ? 0 : (int) Math.copySign(Math.max(1, Math.abs(totalX) / durationTicks), totalX);
		knockbackStepY = totalY == 0 ? 0 : (int) Math.copySign(Math.max(1, Math.abs(totalY) / durationTicks), totalY);
	}
	
	public boolean applyKnockbackStep() {
		if (!hasKnockback()) {
			return false;
		}
		int stepX = nextKnockbackStep(knockbackRemainingX, knockbackStepX);
		int stepY = nextKnockbackStep(knockbackRemainingY, knockbackStepY);
		moveBy(stepX, stepY);
		knockbackRemainingX -= stepX;
		knockbackRemainingY -= stepY;
		return true;
	}
	
	public boolean hasKnockback() {
		return knockbackRemainingX != 0 || knockbackRemainingY != 0;
	}
	
	public void clearKnockback() {
		knockbackRemainingX = 0;
		knockbackRemainingY = 0;
		knockbackStepX = 0;
		knockbackStepY = 0;
	}
	
	private int nextKnockbackStep(int remaining, int step) {
		if (remaining == 0) {
			return 0;
		}
		if (Math.abs(remaining) < Math.abs(step)) {
			return remaining;
		}
		return step;
	}
	
	/** Picks one of eight movement directions and updates dx/dy. */
	public void updateDirection() {
		direction = random.nextInt(8) + 1;
		if(direction == 1) {
			dx=1;
			dy=0;
		}else if(direction == 2) {
			dx=1;
			dy=1;
		}else if(direction == 3) {
			dx=0;
			dy=1;
		}else if(direction == 4) {
			dx=-1;
			dy=1;
		}else if(direction == 5) {
			dx=-1;
			dy=0;
		}else if(direction == 6) {
			dx=-1;
			dy=-1;
		}else if(direction == 7) {
			dx=0;
			dy=-1;
		}else if(direction == 8) {
			dx=1;
			dy=-1;
		}
	}
	
	public void reset() {
		this.x = startX;
		this.y = startY;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getDX() {
		return dx;
	}
	
	public int getDY() {
		return dy;
	}
	
	public void backTrack() {
		this.x-=5*dx;
		this.y-=5*dy;
	}
	
}
