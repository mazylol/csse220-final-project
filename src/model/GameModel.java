package model;

/**
 * Stores the current state of the game and controls the main game rules.
 * 
 * This is where the game keeps track of objects such as the player,
 * walls, zombies.
 * 
 * GameModel updates the game state, does not draw anything. (for internal reference)
 * Drawing belongs in GameComponent.
 */
public class GameModel {
	private Player player;
	private Zombie zombie;
	public static final int GAME_WIDTH = 600;
	public static final int GAME_HEIGHT = 600;
	public static final int PLAYER_SIZE = 60;
	public static final int ZOMBIE_SIZE = 60;
	
	public GameModel() {
		this.player = new Player(50, 50, PLAYER_SIZE, PLAYER_SIZE, GAME_WIDTH, GAME_HEIGHT);
		this.zombie = new Zombie(300, 300, ZOMBIE_SIZE, ZOMBIE_SIZE, GAME_WIDTH, GAME_HEIGHT);
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public Zombie getZombie() {
		return this.zombie;
	}
	
	public void update() {
		zombie.update();
	}
	
	public void movePlayerUp(int n) {
		player.moveBy(0, -n);
	};
	
	public void movePlayerDown(int n) {
		player.moveBy(0, n);
	};
	
	public void movePlayerLeft(int n) {
		player.moveBy(-n, 0);
	};
	
	public void movePlayerRight(int n) {
		player.moveBy(n, 0);
	};
}
