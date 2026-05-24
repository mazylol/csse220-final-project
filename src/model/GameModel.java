package model;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Stores and updates all game state, including level data, actors, and rules.
 */
public class GameModel {
	private static final class AttackInfo {
		private final int x;
		private final int y;
		private final int width;
		private final int height;
		private final int pushX;
		private final int pushY;

		private AttackInfo(int x, int y, int width, int height, int pushX, int pushY) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.pushX = pushX;
			this.pushY = pushY;
		}
	}

	private Player player;
	private final ArrayList<Zombie> zombies;
	private ArrayList<ArrayList<Item>> level;
	public static final int GAME_WIDTH = 600;
	public static final int GAME_HEIGHT = 600;
	public static final int PLAYER_SIZE = 40;
	public static final int ZOMBIE_SIZE = 40;
	public static final int TILE_SIZE = 40;
	private static final int ATTACK_RANGE = 20;
	private static final int ATTACK_PUSH_DISTANCE = 80;
	private static final int ATTACK_PUSH_TICKS = 10;
	private static final int ATTACK_TOLERANCE = 10;
	private static final String DEFAULT_LEVEL = "/level1.csv";
	public static final String NEXT_LEVEL = "/level2.csv";
	private int gemsRemaining;
	private boolean hasKey;
	private boolean won;
	private static boolean lost;
	public static int currentLevel;
	
	/**
	 * Creates a new game model and loads the default level.
	 */
	public GameModel() {
		this.zombies = new ArrayList<>();
		loadLevel(DEFAULT_LEVEL);
		currentLevel = 1;
	}
	
	/**
	 * @return the player instance for the current level
	 */
	public Player getPlayer() {
		return this.player;
	}
	
	/**
	 * @return an unmodifiable list of zombies in the current level
	 */
	public List<Zombie> getZombies() {
		return Collections.unmodifiableList(this.zombies);
	}

	/**
	 * @return the 2D grid representing level tiles
	 */
	public ArrayList<ArrayList<Item>> getLevel() {
		return this.level;
	}

	/**
	 * @return the number of gems remaining to collect
	 */
	public int getGemsRemaining() {
		return this.gemsRemaining;
	}

	/**
	 * @return true if the player has collected the key
	 */
	public boolean hasKey() {
		return this.hasKey;
	}

	/**
	 * @return true if the player has met win conditions for the current level
	 */
	public boolean hasWon() {
		return this.won;
	}
	
	/**
	 * @return true if the player has lost the game
	 */
	public boolean hasLost() {
		return this.lost;
	}
	
	/**
	 * Updates zombie movement and resolves wall collisions.
	 */
	public void update() {
		for (Zombie zombie : zombies) {
			if (zombie.hasKnockback()) {
				int oldX = zombie.getX();
				int oldY = zombie.getY();
				if (zombie.applyKnockbackStep() && checkZombieWallCollision(zombie)) {
					zombie.moveBy(oldX - zombie.getX(), oldY - zombie.getY());
					zombie.clearKnockback();
				}
				continue;
			}
			if(checkZombieWallCollision(zombie)) {
				zombie.backTrack();
				zombie.updateDirection();
			}
			zombie.update();
		}
	}
	
	/**
	 * Moves the player up by the given number of pixels.
	 *
	 * @param n pixels to move
	 */
	public void movePlayerUp(int n) {
		movePlayerBy(0, -n);
	}
	
	/**
	 * Moves the player down by the given number of pixels.
	 *
	 * @param n pixels to move
	 */
	public void movePlayerDown(int n) {
		movePlayerBy(0, n);
	}
	
	/**
	 * Moves the player left by the given number of pixels.
	 *
	 * @param n pixels to move
	 */
	public void movePlayerLeft(int n) {
		movePlayerBy(-n, 0);
	}
	
	/**
	 * Moves the player right by the given number of pixels.
	 *
	 * @param n pixels to move
	 */
	public void movePlayerRight(int n) {
		movePlayerBy(n, 0);
	}

	/**
	 * Attempts to attack the nearest zombie within the attack arc.
	 */
	public void attemptAttack() {
		if (won || lost) {
			return;
		}

		int playerX = player.getX();
		int playerY = player.getY();
		AttackInfo attackInfo = getAttackInfo(playerX, playerY);
		if (attackInfo == null) {
			return;
		}

		for (Zombie zombie : zombies) {
			if (!rectanglesIntersect(attackInfo.x, attackInfo.y, attackInfo.width, attackInfo.height,
					zombie.getX(), zombie.getY(), ZOMBIE_SIZE, ZOMBIE_SIZE)) {
				continue;
			}
			if (isTouchingPlayer(playerX, playerY, zombie)) {
				continue;
			}

			zombie.startKnockback(attackInfo.pushX, attackInfo.pushY, ATTACK_PUSH_TICKS);
			return;
		}
	}

	/**
	 * Checks if a zombie is currently within the player's attack range.
	 *
	 * @param zombie zombie to evaluate
	 * @return true if the zombie is in range and not overlapping the player
	 */
	public boolean isZombieInAttackRange(Zombie zombie) {
		if (won || lost) {
			return false;
		}
		int playerX = player.getX();
		int playerY = player.getY();
		AttackInfo attackInfo = getAttackInfo(playerX, playerY);
		if (attackInfo == null) {
			return false;
		}
		if (!rectanglesIntersect(attackInfo.x, attackInfo.y, attackInfo.width, attackInfo.height,
				zombie.getX(), zombie.getY(), ZOMBIE_SIZE, ZOMBIE_SIZE)) {
			return false;
		}
		return !isTouchingPlayer(playerX, playerY, zombie);
	}
	
	/**
	 * Applies contact damage if a zombie overlaps the player.
	 */
	public void checkZombieCollision(){
		for (Zombie zombie : zombies) {
			boolean compareX = (player.getX() + PLAYER_SIZE >= zombie.getX()) && (player.getX() <= zombie.getX() + ZOMBIE_SIZE);
			boolean compareY = (player.getY() + PLAYER_SIZE >= zombie.getY()) && (player.getY() <= zombie.getY() + ZOMBIE_SIZE);
			if(compareX&&compareY) {
				player.handleZombieCollision();
				//player.reset();
				return;
			}
		}
	}
	
	/** Tile types used in parsed level data. */
	public enum Item {
		Wall,
		Player,
		Zombie,
		Gem,
		Key,
		Exit,
		None
	}
	
	private void loadLevel(String filename) {
		this.level = new ArrayList<>();
		this.zombies.clear();
		this.gemsRemaining = 0;
		this.hasKey = false;
		this.won = false;
		lost = false;
		int playerX = 50;
		int playerY = 50;
		
		InputStream stream = GameModel.class.getResourceAsStream(filename);
		
		if (stream == null) {
			String normalized = filename.startsWith("/") ? filename.substring(1) : filename;
			try {
				stream = new FileInputStream("src/" + normalized);
			} catch (Exception e) {
				throw new IllegalStateException("Level file not found: " + filename + " (also tried src/" + normalized + ")");
			}
		}
		try (Scanner scanner = new Scanner(stream)) {
			int row = 0;
			while (scanner.hasNextLine()) {
				String[] splitLine = scanner.nextLine().split(",", -1);
				ArrayList<Item> levelRow = new ArrayList<>();
				for (int col = 0; col < splitLine.length; col++) {
					String cell = splitLine[col].trim();
					Item item = switch (cell) {
						case "#" -> Item.Wall;
						case "p" -> Item.Player;
						case "z" -> Item.Zombie;
						case "g" -> Item.Gem;
						case "k" -> Item.Key;
						case "e" -> Item.Exit;
						default -> Item.None;
					};

					switch (item) {
						case Player -> {
							playerX = col * TILE_SIZE;
							playerY = row * TILE_SIZE;
							levelRow.add(Item.None);
						}
						case Zombie -> {
							zombies.add(new Zombie(col * TILE_SIZE, row * TILE_SIZE, ZOMBIE_SIZE, ZOMBIE_SIZE, GAME_WIDTH, GAME_HEIGHT));
							levelRow.add(Item.None);
						}
						case Gem -> {
							gemsRemaining += 1;
							levelRow.add(item);
						}
						default -> levelRow.add(item);
					}
				}
				level.add(levelRow);
				row += 1;
			}
		}

		this.player = new Player(playerX, playerY, PLAYER_SIZE, PLAYER_SIZE, GAME_WIDTH, GAME_HEIGHT);
	}

	/**
	 * Moves the player, resolves wall collisions, and applies tile effects.
	 */
	private void movePlayerBy(int dx, int dy) {
		if (won || lost) {
			return;
		}

		int oldX = player.getX();
		int oldY = player.getY();
		player.moveBy(dx, dy);
		if (isWallCollision(player.getX(), player.getY(), PLAYER_SIZE, PLAYER_SIZE)) {
			player.moveBy(oldX - player.getX(), oldY - player.getY());
			return;
		}

		applyTileEffects();
	}

	/**
	 * Checks whether a zombie intersects any wall tiles.
	 *
	 * @param zombie zombie to test
	 * @return true if the zombie collides with a wall tile
	 */
	public boolean checkZombieWallCollision(Zombie zombie) {
		int leftCol = zombie.getX() / TILE_SIZE;
		int rightCol = (zombie.getX() + zombie.getWidth() - 1) / TILE_SIZE;
		int topRow = zombie.getY() / TILE_SIZE;
		int bottomRow = (zombie.getY() + zombie.getHeight() - 1) / TILE_SIZE;
		for (int row = topRow; row <= bottomRow; row++) {
			for (int col = leftCol; col <= rightCol; col++) {
				if (!isInBounds(row, col)) {
					continue;
				}
				if (level.get(row).get(col) == Item.Wall) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isWallCollision(int x, int y, int width, int height) {
		int leftCol = x / TILE_SIZE;
		int rightCol = (x + width - 1) / TILE_SIZE;
		int topRow = y / TILE_SIZE;
		int bottomRow = (y + height - 1) / TILE_SIZE;
		for (int row = topRow; row <= bottomRow; row++) {
			for (int col = leftCol; col <= rightCol; col++) {
				if (!isInBounds(row, col)) {
					continue;
				}
				if (level.get(row).get(col) == Item.Wall) {
					return true;
				}
			}
		}
		return false;
	}

	private void applyTileEffects() {
		int leftCol = player.getX() / TILE_SIZE;
		int rightCol = (player.getX() + PLAYER_SIZE - 1) / TILE_SIZE;
		int topRow = player.getY() / TILE_SIZE;
		int bottomRow = (player.getY() + PLAYER_SIZE - 1) / TILE_SIZE;
		for (int row = topRow; row <= bottomRow; row++) {
			for (int col = leftCol; col <= rightCol; col++) {
				if (!isInBounds(row, col)) {
					continue;
				}
				Item item = level.get(row).get(col);
				if (item == Item.Gem) {
					level.get(row).set(col, Item.None);
					gemsRemaining -= 1;
				} else if (item == Item.Key) {
					level.get(row).set(col, Item.None);
					hasKey = true;
				} else if (item == Item.Exit && hasKey && gemsRemaining == 0) {
					won = true;
				}
			}
		}
	}

	private AttackInfo getAttackInfo(int playerX, int playerY) {
		Player.FacingDirection facing = player.getFacingDirection();
		int attackX;
		int attackY;
		int attackWidth;
		int attackHeight;
		int pushX = 0;
		int pushY = 0;
		switch (facing) {
			case RIGHT -> {
				attackX = playerX + PLAYER_SIZE;
				attackY = playerY - ATTACK_TOLERANCE;
				attackWidth = ATTACK_RANGE;
				attackHeight = PLAYER_SIZE + 2 * ATTACK_TOLERANCE;
				pushX = ATTACK_PUSH_DISTANCE;
			}
			case LEFT -> {
				attackX = playerX - ATTACK_RANGE;
				attackY = playerY - ATTACK_TOLERANCE;
				attackWidth = ATTACK_RANGE;
				attackHeight = PLAYER_SIZE + 2 * ATTACK_TOLERANCE;
				pushX = -ATTACK_PUSH_DISTANCE;
			}
			case UP -> {
				attackX = playerX - ATTACK_TOLERANCE;
				attackY = playerY - ATTACK_RANGE;
				attackWidth = PLAYER_SIZE + 2 * ATTACK_TOLERANCE;
				attackHeight = ATTACK_RANGE;
				pushY = -ATTACK_PUSH_DISTANCE;
			}
			case DOWN -> {
				attackX = playerX - ATTACK_TOLERANCE;
				attackY = playerY + PLAYER_SIZE;
				attackWidth = PLAYER_SIZE + 2 * ATTACK_TOLERANCE;
				attackHeight = ATTACK_RANGE;
				pushY = ATTACK_PUSH_DISTANCE;
			}
			default -> {
				return null;
			}
		}
		return new AttackInfo(attackX, attackY, attackWidth, attackHeight, pushX, pushY);
	}

	private boolean isTouchingPlayer(int playerX, int playerY, Zombie zombie) {
		return playerX + PLAYER_SIZE >= zombie.getX()
				&& playerX <= zombie.getX() + ZOMBIE_SIZE
				&& playerY + PLAYER_SIZE >= zombie.getY()
				&& playerY <= zombie.getY() + ZOMBIE_SIZE;
	}

	private boolean rectanglesIntersect(int ax, int ay, int aw, int ah, int bx, int by, int bw, int bh) {
		return ax < bx + bw && ax + aw > bx && ay < by + bh && ay + ah > by;
	}

	private boolean isInBounds(int row, int col) {
		return row >= 0 && row < level.size() && col >= 0 && col < level.get(row).size();
	}

	/**
	 * @return the player's current health
	 */
	public int getHealth() {
		return player.getHealth();
	}
	
	/**
	 * Marks the game as lost.
	 */
	public static void GameOver() {
		lost = true;
	}
	
	/**
	 * Resets the game to the first level.
	 */
	public void restartGame() {
		currentLevel = 1;
		loadLevel(DEFAULT_LEVEL);
	}
	
	/**
	 * @return true if the current level is completed
	 */
	public boolean getWon() {
		return won;
	}
	
	/**
	 * Loads the next level if the first level has been completed.
	 */
	public void proceed() {
		if(won&&currentLevel==1) {
			loadLevel(NEXT_LEVEL);
			currentLevel = 2;
		}
	}
}
