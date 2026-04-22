package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class GameComponent extends JPanel {
	private Ball ball;

	public static final int WIDTH = 400;
	public static final int HEIGHT = 150;
	public static final Color BG = Color.CYAN;
	public static final Color FG = Color.BLACK;
	
	
	public GameComponent() {
		this.setPreferredSize(new Dimension(WIDTH,HEIGHT));
		this.setBackground(BG);
		this.setOpaque(true);
	
		ball = new Ball(WIDTH/2,100,14);

	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(FG);	
		ball.draw(g2);
	}
	
	public void move(int x) {
		ball.shift(x);
		repaint();
	}
	
	public void center() {
		ball.reset();
		repaint();
	}
	
	
}
