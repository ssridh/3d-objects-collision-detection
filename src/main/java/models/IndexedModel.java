package models;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.util.vector.Vector3f;

import bvh.Primitive;

public class IndexedModel {
	
	private List<Vector3f> vertices = new ArrayList<Vector3f>();
	private List<Integer> indices = new ArrayList<Integer>();
	private List<Primitive> primitives = new ArrayList<Primitive>();
	
	public IndexedModel(List<Vector3f> vertices, List<Integer> indices, List<Primitive> primitives) {
		this.vertices = vertices;
		this.indices = indices;		
		this.primitives = primitives;
	}

	public List<Vector3f> getVertices() {
		return vertices;
	}

	public List<Integer> getIndices() {
		return indices;
	}

	public List<Primitive> getPrimitives() {
		return primitives;
	}

	public void setPrimitives(List<Primitive> primitives) {
		this.primitives = primitives;
	}

	public void setVertices(List<Vector3f> vertices) {
		this.vertices = vertices;
	}

	public void setIndices(List<Integer> indices) {
		this.indices = indices;
	}

	
	
}
