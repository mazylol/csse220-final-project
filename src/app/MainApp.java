package app;

import javax.swing.SwingUtilities;

import ui.GameWindow;
import model.GameModel;

/**
 * Application entry point that boots the game UI.
 *
 * Fields: none.
 * Methods: main(...), run().
 */

public class MainApp {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new MainApp().run();
		});
	}
	
	public void run() {
		GameModel model = new GameModel();
		GameWindow window = new GameWindow(model);
		window.show();
	}
}
