package com.jakemadethis.shooter;

import java.util.Random;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class ShooterGame implements ApplicationListener, InputProcessor {
	private static final float TILE_SIZE = 4f;

	public static void main(String[] args) {
		LwjglApplication app = 
			new LwjglApplication(new ShooterGame(), "Shooter Game", 800, 600, true);
	}

	private Mesh mesh;
	private ShaderProgram shader;
	private TextureTileMap tile_map;
	private PerspectiveCamera camera;
	
	private Vector3 light_dir = new Vector3(0.8f, 0.3f, -1f).nor();
	
	private Vector3 player_dir = new Vector3();

	private static final Vector3 UP = new Vector3(0, 0, 1);
	private static final Vector3 NORTH = new Vector3(0, 1, 0);
	private static final Vector3 EAST = new Vector3(1, 0, 0);
	private static final Vector3 DOWN = new Vector3(0, 0, -1);
	private static final Vector3 SOUTH = new Vector3(0, -1, 0);
	private static final Vector3 WEST = new Vector3(-1, 0, 0);
	
	public ShooterGame() {
		
	}

	@Override
	public void create() {
		
		
		
		
		tile_map = genTexture();

		TileMeshGen meshGen = new TileMeshGen(TILE_SIZE, 2f, tile_map);
		
		int[] tiles = new int[] {
				0,0,0,0,0,2,0,2,
				0,0,0,0,0,0,0,2,
				0,0,1,0,0,0,0,2,
				0,0,0,1,1,0,0,2,
				0,0,0,0,0,0,0,2,
				0,0,0,0,0,0,0,2,
				2,2,2,0,0,1,1,1,
				0,0,0,0,0,0,0,0
		};
		
		meshGen.corner(new Vector3(2f, 2f, 0f), UP, Math.PI/2, 0, 0.3f, 2f);
		
		for (int j = 0; j < 8; j++) {
			for(int i = 0; i < 8; i++) {
				int tile = tiles[i + j*8];
				int tileNorth = j == 0 ? 1 : tiles[i + (j-1)*8];
				int tileSouth = j == 7 ? 1 : tiles[i + (j+1)*8];
				int tileWest =  i == 0 ? 1 : tiles[i - 1 + j*8];
				int tileEast =  i == 7 ? 1 : tiles[i + 1 + j*8];

				if (tile == 0) {
					//meshGen.ceiling(i, j);
					meshGen.floor(i, j, 2);
					
					if (tileNorth > 0) meshGen.northWall(i, j, tileNorth-1);
					if (tileSouth > 0) meshGen.southWall(i, j, tileSouth-1);
					if (tileEast  > 0) meshGen.eastWall(i, j,  tileEast-1);
					if (tileWest  > 0) meshGen.westWall(i, j,  tileWest-1);
				}
				
			}
		}
		for (int i = 8; i < 100; i++) {
			meshGen.eastWall(0, i, 0);
			meshGen.westWall(0, i, 0);
			meshGen.ceiling(0, i, 2);
			meshGen.floor(0, i, 0);
		}

		//meshGen.rect(new Vector3(0, 0, -height), new Vector3(0, 1, 0), new Vector3(1, 0, 0), width, height);
		
		mesh = meshGen.generate();

		String vertexShader = Gdx.files.internal("data/vertexShader").readString();
		String fragmentShader = Gdx.files.internal("data/fragmentShader").readString();

		shader = new ShaderProgram(vertexShader, fragmentShader);
		if (shader.isCompiled() == false) {
			Gdx.app.log("Shader", shader.getLog());
			Gdx.app.exit();
		}
		//texture = new Texture(Gdx.files.internal("data/image.png"));
		
		
		
		Gdx.input.setInputProcessor(this);
		Gdx.input.setCursorCatched(true);
			
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	private TextureTileMap genTexture() {
		final int tex_width = 128;
		
		Random r = new Random();
		Pixmap pixmap = new Pixmap(tex_width*4, tex_width, Format.RGB888);
		for (int i = 0; i < tex_width; i++) {
			for (int j = 0; j < tex_width; j++) {
				float red = 0.4f + r.nextFloat() * 0.3f;
				pixmap.setColor(red, 0f, 0f, 1f);
				pixmap.drawPixel(i, j);
			}
		}
		for (int i = tex_width; i < tex_width+tex_width; i++) {
			for (int j = 0; j < tex_width; j++) {
				float green = 0.4f + r.nextFloat() * 0.3f;
				pixmap.setColor(0f, green, 0f, 1f);
				pixmap.drawPixel(i, j);
			}
		}
		

		for (int i = 0; i < tex_width; i++) {
			for (int j = 0; j < tex_width; j++) {
				float g = 0.1f + r.nextFloat() * 0.1f;
				pixmap.setColor(g, g, g, 1f);
				pixmap.drawPixel(tex_width*2+i, j);
			}
		}
		for (int i = 0; i < tex_width; i++) {
			pixmap.setColor(0.1f, 0.1f, 0.1f, 1f);
			pixmap.drawPixel(tex_width*2, i);
			pixmap.drawPixel(tex_width*2+i, 0);
		}
		return new TextureTileMap(new Texture(pixmap), tex_width);
	}

	@Override
	public void dispose() {

		mesh.dispose();
		tile_map.dispose();
		shader.dispose();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	float angle = 0;
	Matrix4 matrix = new Matrix4();
	
	@Override
	public void render() {
		
		int d = (Gdx.input.isKeyPressed(Keys.LEFT) ? 1 : 0) - (Gdx.input.isKeyPressed(Keys.RIGHT) ? 1 : 0);
		//camera.rotate(UP, Gdx.graphics.getDeltaTime() * 80f * d);
		
		camera.rotate(UP, Gdx.graphics.getDeltaTime() * -20f * Gdx.input.getDeltaX());
		Vector3 pitch_axis = UP.cpy().crs(camera.direction).nor();
		camera.rotate(pitch_axis, Gdx.graphics.getDeltaTime() * 20f * Gdx.input.getDeltaY());
		
		player_dir = camera.direction.cpy();
		player_dir.z = 0;
		player_dir.nor();

		int side = (Gdx.input.isKeyPressed(Keys.D) ? 1 : 0) - (Gdx.input.isKeyPressed(Keys.A) ? 1 : 0);
		int front = (Gdx.input.isKeyPressed(Keys.W) ? 1 : 0) - (Gdx.input.isKeyPressed(Keys.S) ? 1 : 0);
		camera.translate(player_dir.cpy().mul(front).mul(7f * Gdx.graphics.getDeltaTime()));
		camera.translate(player_dir.cpy().crs(UP).mul(side).mul(7f * Gdx.graphics.getDeltaTime()));
		camera.update();
		
		
		

		Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl20.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl20.glClear(GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl20.glEnable(GL20.GL_TEXTURE_2D);
		Gdx.gl20.glEnable(GL10.GL_BLEND);
		Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl20.glEnable(GL20.GL_CULL_FACE);
		
		Gdx.gl20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		tile_map.getTexture().bind(0);
		

		angle += Gdx.graphics.getDeltaTime() * 45;
		matrix.setToRotation(UP, angle);
		//.rot(matrix).add(0, 0, -1f);
		
		//matrix.idt();
		
		shader.begin();
		shader.setUniformMatrix("u_model", new Matrix4().idt());
		shader.setUniformMatrix("u_view", camera.combined);
		shader.setUniformf("u_eyePos", camera.position);
		shader.setUniformf("u_lightDir", light_dir);
		shader.setUniformi("u_texture", 0);
		
		mesh.render(shader, GL10.GL_TRIANGLES);
		shader.end();
	
	}

	@Override
	public void resize(int w, int h) {
		Vector3 oldpos = new Vector3(TILE_SIZE/2, -TILE_SIZE/2, 1f);
		if (camera != null) oldpos = camera.position.cpy();
		camera = new PerspectiveCamera(40f, w, h);
		camera.up.set(UP);
		camera.direction.set(SOUTH);
		camera.position.set(oldpos);
		camera.update();
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.ESCAPE) {
			Gdx.app.exit();
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchMoved(int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}

