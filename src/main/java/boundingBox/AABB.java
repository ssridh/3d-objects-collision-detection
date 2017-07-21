package boundingBox;

import org.lwjgl.util.vector.Vector3f;

public class AABB {
	private int vaoID;
	private Vector3f max;
	private Vector3f min;

	public AABB() {
		//do nothing		
	}

	public AABB(int vaoID, Vector3f min, Vector3f max) {
		this.vaoID = vaoID;		
		this.max = max;
		this.min = min;
	}

	public AABB(Vector3f min, Vector3f max) {
		this.max = max;
		this.min = min;
	}

	public int getVaoID() {
		return vaoID;
	}

	public void setVaoID(int vaoID) {
		this.vaoID = vaoID;
	}

	public Vector3f getMax() {
		return max;
	}

	public void setMax(Vector3f max) {
		this.max = max;
	}

	public Vector3f getMin() {
		return min;
	}

	public void setMin(Vector3f min) {
		this.min = min;
	}
}
