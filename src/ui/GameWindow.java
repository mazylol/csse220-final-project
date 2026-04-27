package ui;

import javax.swing.JFrame;

import model.GameModel;

public class GameWindow {
	private final JFrame frame;
	private final GameModel model;
	
	public GameWindow(GameModel model) {
		this.model = model;
		this.frame = new JFrame("CSSE220 Final Project");

		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.add(new GameComponent(this.model));
		this.frame.setSize(600, 600);
		this.frame.setLocationRelativeTo(null);
	}

	public void show() {
		this.frame.setVisible(true);
	}
}
