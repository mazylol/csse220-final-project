package app;

import javax.swing.SwingUtilities;

import ui.GameWindow;
import model.GameModel;

/**
 * This is the entry point for our game. Initializes the GameModel, a GameWindow with that model, and then shows it.
 */

// NOTE: player.png and zombie.png were generated with Gemini (they look awful). Will probably make something better in aseprite.

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
