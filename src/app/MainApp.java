package app;

import javax.swing.SwingUtilities;

import ui.GameWindow;
import model.GameModel;

public class MainApp {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
		new MainApp().run();
		});
		}
	
	// This runs only once at startup:
	// MainApp creates and connects objects (no game logic here)
	public void run() {
		GameModel model = new GameModel();
		GameWindow window = new GameWindow(model);
		window.show();
	}
}
