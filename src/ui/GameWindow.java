package ui;

import java.awt.CardLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import model.GameModel;

/**
 * Top-level Swing window that manages screens, input, and movement loop timing.
 *
 * Fields: MOVE_STEP, FRAME_DELAY_MS, frame, model, gameComponent, gameLoop,
 * movingUp, movingDown, movingLeft, movingRight.
 * Methods: GameWindow(...), show().
 */
public class GameWindow extends JPanel {
	private static final int MOVE_STEP = 5;
	private static final int FRAME_DELAY_MS = 30;

	private final JFrame frame;
	private final GameModel model;
	private final GameComponent gameComponent;
	private final Timer gameLoop;
	private boolean movingUp;
	private boolean movingDown;
	private boolean movingLeft;
	private boolean movingRight;
	
	public GameWindow(GameModel model) {
		this.model = model;
		this.frame = new JFrame("CSSE220 Final Project");
		this.gameComponent = new GameComponent(this.model);

		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel cards = new JPanel(new CardLayout());
		StartPanel startPanel = new StartPanel();
		cards.add(startPanel, "START");
		cards.add(this.gameComponent, "GAME");
		
		this.frame.setContentPane(cards);
		
		CardLayout cl = (CardLayout) cards.getLayout();
		cl.show(cards, "START");
		
		this.frame.pack();
		this.frame.setLocationRelativeTo(null);
		this.frame.setResizable(false);
		this.gameComponent.setFocusable(true);
		
		this.gameComponent.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_W, KeyEvent.VK_UP -> movingUp = true;
					case KeyEvent.VK_S, KeyEvent.VK_DOWN -> movingDown = true;
					case KeyEvent.VK_A, KeyEvent.VK_LEFT -> movingLeft = true;
					case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> movingRight = true;
					case KeyEvent.VK_R -> {
						if (model.hasWon() || model.hasLost()) {
							restartGame();
						}
					}
					case KeyEvent.VK_N -> {
						if(model.getWon()) {
							model.proceed();
						}
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_W, KeyEvent.VK_UP -> movingUp = false;
					case KeyEvent.VK_S, KeyEvent.VK_DOWN -> movingDown = false;
					case KeyEvent.VK_A, KeyEvent.VK_LEFT -> movingLeft = false;
					case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> movingRight = false;
				}
			}
		});
		
		startPanel.button.addActionListener(e -> {
		    this.gameComponent.startTimer();
		    cl.show(cards, "GAME");
		    SwingUtilities.invokeLater(() -> this.gameComponent.requestFocusInWindow()); // without this input does not work, future self: might need this again
		});

		this.gameLoop = new Timer(FRAME_DELAY_MS, e -> {
			boolean moved = false;
			if (movingUp) {
				model.movePlayerUp(MOVE_STEP);
				moved = true;
			}
			if (movingDown) {
				model.movePlayerDown(MOVE_STEP);
				moved = true;
			}
			if (movingLeft) {
				model.movePlayerLeft(MOVE_STEP);
				moved = true;
			}
			if (movingRight) {
				model.movePlayerRight(MOVE_STEP);
				moved = true;
			}
			if (moved) {
				gameComponent.repaint();
			}
		});
	}
	
	private void restartGame() {
		movingUp = false;
		movingDown = false;
		movingLeft = false;
		movingRight = false;
		this.gameComponent.restartGame();
	}

	public void show() {
		this.frame.setVisible(true);
		this.gameComponent.requestFocusInWindow();
		this.gameLoop.start();
	}
}
