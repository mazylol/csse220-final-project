package game;

import java.awt.Color;
import java.awt.Graphics2D;

public class Ball {
	
	private int x, y, radius;
	private int startX;
	private int startY;
	
	public Ball(int x, int y, int radius) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.startX = x;
		this.startY = y;
	}
	
	public void draw(Graphics2D g2) {
		g2.setColor(Color.RED);
		g2.fillOval(x, y, 2*radius, 2*radius);
	}
	
	public void shift(int dx) {
		  x += dx;
	}
	
	public void reset() {
		this.x = this.startX;
	}

}
