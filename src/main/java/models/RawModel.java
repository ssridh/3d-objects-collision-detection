package models;

import boundingBox.AABB;
import bvh.AABBbvhTree;

public class RawModel {
	private int vaoID;
	private int vertexCount;
	private AABB aabb;
	private AABBbvhTree aabbTree;
	private IndexedModel indexedModel;
	
	
	public RawModel(int vaoID, int vertexCount, AABB aabb, AABBbvhTree aabbTree, IndexedModel indexedModel) {
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;			
		this.aabb = aabb;
		this.aabbTree = aabbTree;
		this.indexedModel = indexedModel;
	}

	public int getVaoID() {
		return vaoID;
	}

	public int getVertexCount() {
		return vertexCount;
	}

	public AABB getAabb() {
		return aabb;
	}

	public AABBbvhTree getAabbTree() {
		return aabbTree;
	}

	public IndexedModel getIndexedModel() {
		return indexedModel;
	}
	
	
	
	

}
