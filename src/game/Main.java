package game;

import javax.swing.SwingUtilities;

/**
 * Entry point for the game application.
 
 * @author Landon Porter, Pete Harrington
 * */


public class Main {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(()->{
				new GameApp().show();
		});

	}

}