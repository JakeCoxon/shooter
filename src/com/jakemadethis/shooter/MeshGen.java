package com.jakemadethis.shooter;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ShortArray;

public class MeshGen {
	
	private static final int NUM_FLOATS = 8;

	FloatArray vertexData = new FloatArray(4*4*NUM_FLOATS);
	ShortArray indicesData = new ShortArray(6*4);
	
	public Mesh generate() {
		Mesh mesh = new Mesh(true, vertexData.size/NUM_FLOATS, indicesData.size, VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0));
		mesh.setVertices(vertexData.toArray());
		mesh.setIndices(indicesData.toArray());
		return mesh;
	}

	public void rect(Vector3 pos, Vector3 up, Vector3 right, float width, float height) {
		rect(pos, up, right, width, height, 0, 1, 0, 1);
	}
	public void rect(Vector3 pos, Vector3 up, Vector3 right, float width, float height, float[] uvs) {
		rect(pos, up, right, width, height, uvs[0], uvs[1], uvs[2], uvs[3]);
	}
	public void rect(Vector3 pos, Vector3 up, Vector3 right, float width, float height, float u0, float u1, float v0, float v1) {
		width /= 2f;
		height /= 2f;
		int index_pos = vertexData.size/NUM_FLOATS;
		Vector3 normal = up.cpy().crs(right);

		vertexData.add(pos.x + right.x * -width + up.x * -height);
		vertexData.add(pos.y + right.y * -width + up.y * -height);
		vertexData.add(pos.z + right.z * -width + up.z * -height);
		vertexData.add(normal.x); vertexData.add(normal.y); vertexData.add(normal.z);
		vertexData.addAll(new float[]{u0, v1});

		vertexData.add(pos.x + right.x * width + up.x * -height);
		vertexData.add(pos.y + right.y * width + up.y * -height);
		vertexData.add(pos.z + right.z * width + up.z * -height);
		vertexData.add(normal.x); vertexData.add(normal.y); vertexData.add(normal.z);
		vertexData.addAll(new float[]{u1, v1});
		
		vertexData.add(pos.x + right.x * width + up.x * height);
		vertexData.add(pos.y + right.y * width + up.y * height);
		vertexData.add(pos.z + right.z * width + up.z * height);
		vertexData.add(normal.x); vertexData.add(normal.y); vertexData.add(normal.z);
		vertexData.addAll(new float[]{u1, v0});

		vertexData.add(pos.x + right.x * -width + up.x * height);
		vertexData.add(pos.y + right.y * -width + up.y * height);
		vertexData.add(pos.z + right.z * -width + up.z * height);
		vertexData.add(normal.x); vertexData.add(normal.y); vertexData.add(normal.z);
		vertexData.addAll(new float[]{u0, v0});

		indicesData.add((short) (index_pos + 0));
		indicesData.add((short) (index_pos + 1));
		indicesData.add((short) (index_pos + 2));
		indicesData.add((short) (index_pos + 2));
		indicesData.add((short) (index_pos + 3));
		indicesData.add((short) (index_pos + 0));
	}
	
	public void wall(Vector3 start, Vector3 end, Vector3 up, float height, float u0, float u1, float v0, float v1) {
		height /= 2f;
		int index_pos = vertexData.size/NUM_FLOATS;
		Vector3 normal = up.cpy().crs(end.cpy().sub(start)).nor();
		
		vertexData.add(start.x + up.x * -height);
		vertexData.add(start.y + up.y * -height);
		vertexData.add(start.z + up.z * -height);
		vertexData.add(normal.x); vertexData.add(normal.y); vertexData.add(normal.z);
		vertexData.addAll(new float[]{u0, v1});
		
		vertexData.add(end.x + up.x * -height);
		vertexData.add(end.y + up.y * -height);
		vertexData.add(end.z + up.z * -height);
		vertexData.add(normal.x); vertexData.add(normal.y); vertexData.add(normal.z);
		vertexData.addAll(new float[]{u1, v1});
		
		vertexData.add(end.x + up.x * height);
		vertexData.add(end.y + up.y * height);
		vertexData.add(end.z + up.z * height);
		vertexData.add(normal.x); vertexData.add(normal.y); vertexData.add(normal.z);
		vertexData.addAll(new float[]{u1, v0});
		
		vertexData.add(start.x + up.x * height);
		vertexData.add(start.y + up.y * height);
		vertexData.add(start.z + up.z * height);
		vertexData.add(normal.x); vertexData.add(normal.y); vertexData.add(normal.z);
		vertexData.addAll(new float[]{u0, v0});

		indicesData.add((short) (index_pos + 0));
		indicesData.add((short) (index_pos + 1));
		indicesData.add((short) (index_pos + 2));
		indicesData.add((short) (index_pos + 2));
		indicesData.add((short) (index_pos + 3));
		indicesData.add((short) (index_pos + 0));
	}
	
	public void corner(Vector3 pos, Vector3 up, double angle, double angle2, float radius, float height) {
		float next_x = pos.x + (float) (Math.sin(angle)*radius);
		float next_y = pos.y + (float) (Math.cos(angle)*radius);
		for (int i = 1; i < 30; i++) {
			double a = angle + (angle2 - angle)*i/30;
			float x = next_x;
			float y = next_y;
			
			next_x = pos.x + (float)Math.sin(a)*radius;
			next_y = pos.y + (float)Math.cos(a)*radius;
			
			wall(new Vector3(x, y, pos.z), new Vector3(next_x, next_y, pos.z), up, height, 1f*i/30, 1f*(i+1)/30, 0, 1);
		}
	}
}
