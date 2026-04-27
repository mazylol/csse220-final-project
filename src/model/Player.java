package model;

import java.awt.Graphics2D;

public class Player {
	private static final int TILE_SIZE = 40;
	private int row;
	private int col;
	
	public Player(int startRow, int startCol) {
		this.row = startRow;
		this.col = startCol;
	}
	
	int getRow() {
		return this.row;
	}
	
	int getCol() {
		return this.col;
	}
	
	public void drawOn(Graphics2D g2) {
		int x = this.col * TILE_SIZE;
		int y = this.row * TILE_SIZE;
		
		g2.fillRect(x, y, TILE_SIZE, TILE_SIZE);
	}
	
	public void moveBy(int dRow, int dCol) {
		this.row += dRow;
		this.col += dCol;
	}
}