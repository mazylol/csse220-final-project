package game;

import javax.swing.JFrame;

public class GameApp {
	private JFrame frame;
	private GamePanel panel;
	
	public GameApp() {
		this.frame = new JFrame("Game");
		this.panel = new GamePanel();
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setContentPane(this.panel);
        this.frame.pack();
        this.frame.setLocationRelativeTo(null);
        this.frame.setResizable(false);
	}
	
	public void show() {
		this.frame.setVisible(true);

	}

}