package model;

/**
 * Stores the current state of the game and controls the main game rules.
 * 
 * This is where the game keeps track of objects such as the player,
 * walls, gems, zombies, score, lives, and levels.
 * 
 * GameModel should update the game state, but it should not draw anything.
 * Drawing belongs in GameComponent.
 */
public class GameModel {
	private Player player;
	
	public GameModel() {
		this.player = new Player(5, 5);
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public void movePlayerUp(int y) {
		player.moveBy(-y, 0);
	};
	
	public void movePlayerDown(int y) {
		player.moveBy(y, 0);
	};
	
	public void movePlayerLeft(int x) {
		player.moveBy(x, 0);
	};
	
	public void movePlayerRight(int x) {
		player.moveBy(x, 0);
	};
}
