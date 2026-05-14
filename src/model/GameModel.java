package model;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Stores and updates all game state and core rules.
 *
 * Fields: player, zombies, level, GAME_WIDTH, GAME_HEIGHT, PLAYER_SIZE,
 * ZOMBIE_SIZE, TILE_SIZE, DEFAULT_LEVEL, gemsRemaining, hasKey, won, lost.
 * Methods: GameModel(), getPlayer(), getZombies(), getLevel(),
 * getGemsRemaining(), hasKey(), hasWon(), hasLost(), update(),
 * movePlayerUp(...), movePlayerDown(...), movePlayerLeft(...),
 * movePlayerRight(...), checkZombieCollision(), loadLevel(...),
 * movePlayerBy(...), checkZombieWallCollision(...), isWallCollision(...),
 * applyTileEffects(), isInBounds(...), getHealth(), GameOver().
 */
public class GameModel {
	private Player player;
	private final ArrayList<Zombie> zombies;
	private ArrayList<ArrayList<Item>> level;
	public static final int GAME_WIDTH = 600;
	public static final int GAME_HEIGHT = 600;
	public static final int PLAYER_SIZE = 40;
	public static final int ZOMBIE_SIZE = 40;
	public static final int TILE_SIZE = 40;
	private static final String DEFAULT_LEVEL = "/level1.csv";
	private int gemsRemaining;
	private boolean hasKey;
	private boolean won;
	private static boolean lost;
	
	public GameModel() {
		this.zombies = new ArrayList<>();
		loadLevel(DEFAULT_LEVEL);
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public List<Zombie> getZombies() {
		return Collections.unmodifiableList(this.zombies);
	}

	public ArrayList<ArrayList<Item>> getLevel() {
		return this.level;
	}

	public int getGemsRemaining() {
		return this.gemsRemaining;
	}

	public boolean hasKey() {
		return this.hasKey;
	}

	public boolean hasWon() {
		return this.won;
	}
	
	public boolean hasLost() {
		return this.lost;
	}
	
	public void update() {
		for (Zombie zombie : zombies) {
			if(checkZombieWallCollision(zombie)) {
				zombie.backTrack();
				zombie.updateDirection();
			}
			zombie.update();
		}
	}
	
	public void movePlayerUp(int n) {
		movePlayerBy(0, -n);
	}
	
	public void movePlayerDown(int n) {
		movePlayerBy(0, n);
	}
	
	public void movePlayerLeft(int n) {
		movePlayerBy(-n, 0);
	}
	
	public void movePlayerRight(int n) {
		movePlayerBy(n, 0);
	}
	
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
		if (won) {
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

	private boolean isInBounds(int row, int col) {
		return row >= 0 && row < level.size() && col >= 0 && col < level.get(row).size();
	}

	public int getHealth() {
		return player.getHealth();
	}
	
	public static void GameOver() {
		lost = true;
	}
}
