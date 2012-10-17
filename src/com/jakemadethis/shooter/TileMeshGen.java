package com.jakemadethis.shooter;

import com.badlogic.gdx.math.Vector3;

public class TileMeshGen extends MeshGen {

	private static final Vector3 UP = new Vector3(0, 0, 1);
	private static final Vector3 NORTH = new Vector3(0, 1, 0);
	private static final Vector3 EAST = new Vector3(1, 0, 0);
	private static final Vector3 DOWN = new Vector3(0, 0, -1);
	private static final Vector3 SOUTH = new Vector3(0, -1, 0);
	private static final Vector3 WEST = new Vector3(-1, 0, 0);
	
	private final float tileSize;
	private final float height;
	private float halfTile;
	private float half_height;
	private final TextureTileMap tile_map;
	
	public TileMeshGen(float tileSize, float height, TextureTileMap tile_map) {
		this.tileSize = tileSize;
		this.tile_map = tile_map;
		this.halfTile = tileSize/2;
		this.height = height;
		this.half_height = height/2;
	}
	public void northWall(int x, int y, int tile) {
		rectTile(new Vector3(x * tileSize + halfTile, -y * tileSize, half_height), UP, EAST, tileSize, height, tile);
	}
	public void southWall(int x, int y, int tile) {
		rectTile(new Vector3(x * tileSize + halfTile, -(y+1) * tileSize, half_height), UP, WEST, tileSize, height, tile);
	}
	public void westWall(int x, int y, int tile) {
		rectTile(new Vector3(x * tileSize, -y * tileSize - halfTile, half_height), UP, NORTH, tileSize, height, tile);
	}
	public void eastWall(int x, int y, int tile) {
		rectTile(new Vector3((x+1) * tileSize, -y * tileSize - halfTile, half_height), UP, SOUTH, tileSize, height, tile);
	}
	public void ceiling(int x, int y, int tile) {
		rectTile(new Vector3(x * tileSize + halfTile, -y * tileSize - halfTile, height), SOUTH, EAST, tileSize, tileSize, tile);
	}
	public void floor(int x, int y, int tile) {
		rectTile(new Vector3(x * tileSize + halfTile, -y * tileSize - halfTile, 0), NORTH, EAST, tileSize, tileSize, tile);
	}
	
	public void rectTile(Vector3 pos, Vector3 up, Vector3 right, float width, float height, int tile) {
		float[] uvs = tile_map.getUV(tile);
		uvs[3] *=  (height/width);
		rect(pos, up, right, width, height, uvs);
	}
}
