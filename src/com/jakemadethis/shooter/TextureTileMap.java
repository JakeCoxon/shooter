package com.jakemadethis.shooter;

import com.badlogic.gdx.graphics.Texture;

public class TextureTileMap {
	private final Texture texture;
	private final int tileSize;
	private int cols;
	private int rows;

	public TextureTileMap(Texture texture, int tileSize) {
		this.texture = texture;
		this.tileSize = tileSize;
		this.cols = texture.getWidth() / tileSize;
		this.rows = texture.getHeight() / tileSize;
	}
	
	public Texture getTexture() {
		return texture;
	}
	public int getTileSize() {
		return tileSize;
	}
	
	public float[] getUV(int tileNum) {
		int col = tileNum % cols;
		int row = tileNum / cols;
		return new float[] { (float)col/cols, (float)(col+1)/cols, 
				(float)row/rows, (float)(row+1)/rows };
	}
	
	public void dispose() {
		texture.dispose();
	}
}
