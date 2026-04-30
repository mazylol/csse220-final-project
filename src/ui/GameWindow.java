package ui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import model.GameModel;

public class GameWindow extends JPanel {
	private final JFrame frame;
	private final GameModel model;
	private final GameComponent gameComponent;
	
	public GameWindow(GameModel model) {
		this.model = model;
		this.frame = new JFrame("CSSE220 Final Project");
		this.gameComponent = new GameComponent(this.model);

		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.add(this.gameComponent);
		this.frame.setSize(600, 600);
		this.frame.setLocationRelativeTo(null);
		this.gameComponent.setFocusable(true);
		
		this.gameComponent.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_W -> model.movePlayerUp(1);
					case KeyEvent.VK_S -> model.movePlayerDown(1);
					case KeyEvent.VK_A -> model.movePlayerLeft(1);
					case KeyEvent.VK_D -> model.movePlayerRight(1);
				}
				
				gameComponent.repaint();
			}
		});
	}

	public void show() {
		this.frame.setVisible(true);
		this.gameComponent.requestFocusInWindow();
	}
}
