package app;

import javax.swing.SwingUtilities;

import ui.GameWindow;
import model.GameModel;

/**
 * Application entry point that boots the game UI.
 */

public class MainApp {
	/**
	 * Launches the Swing application.
	 *
	 * @param args command-line arguments (unused)
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new MainApp().run();
		});
	}
	
	/**
	 * Builds the model and opens the main window.
	 */
	public void run() {
		GameModel model = new GameModel();
		GameWindow window = new GameWindow(model);
		window.show();
	}
}
