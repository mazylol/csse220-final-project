package ui;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import model.GameModel;

public class GameComponent extends JPanel {
	public static final int WIDTH = 600;
	public static final int HEIGHT = 600;
	public static final Color BG = Color.WHITE;
	public static final Color FG = Color.BLACK;

	private final GameModel model;

	public GameComponent(GameModel model) {
		this.model = model;
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.setBackground(BG);
		this.setOpaque(true);
	}


	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		g2.setColor(FG);
		g2.drawString("Final Project Starter: UI is running", 20, 30);
		model.getPlayer().drawOn(g2);
	}
}
