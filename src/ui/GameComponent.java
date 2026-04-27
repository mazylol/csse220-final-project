package ui;


import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;

import model.GameModel;

public class GameComponent extends JComponent {
	private GameModel model;


	public GameComponent(GameModel model) {
		this.model = new GameModel();
		
		super.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
		            System.out.println("Spacebar pressed!");
		        }	
				
				switch (e.getKeyCode()) {
				case KeyEvent.VK_W:
					model.movePlayerUp(5);
				case KeyEvent.VK_S:
					model.movePlayerDown(5);
				case KeyEvent.VK_A:
					model.movePlayerLeft(5);
				case KeyEvent.VK_D:
					model.movePlayerRight(5);

				}
			}
		});
	}


	@Override
	protected void paintComponent(Graphics g) {
	super.paintComponent(g);
	Graphics2D g2 = (Graphics2D) g;

	// Minimal placeholder to test  it’s running
	g2.drawString("Final Project Starter: UI is running ✅", 20, 30);
	model.getPlayer().drawOn(g2);

	// TODO: draw based on model state	
	}
}
