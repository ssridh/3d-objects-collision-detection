package bvh;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import boundingBox.AABB;


public class AABBNode {
   
    public Axis splitAxis;
    public float splitPlane;
    public AABB aabb;
    public AABBNode leftChild;
    public AABBNode rightChild;
    public List<Primitive> triangles;
    public AABBNode[] childNodesList;
    
    public AABBNode() {
    	//Initialize
    	splitAxis = Axis.NO_AXIS;
    	splitPlane = 0.0f;
    	leftChild = null;
    	rightChild = null;
    	triangles = new ArrayList<Primitive>();
	}

    public final boolean isLeafNode() {
        return splitAxis.equals(Axis.NO_AXIS);
    }
    
 	public static float getLongestAxisValue(Vector3f vector, int longestAxis) {
 		switch (longestAxis) {
        case 0:
            return vector.x;
        case 1:
            return vector.y;
        case 2:
            return vector.z;
        default:
            throw new IllegalArgumentException();
        }
    }
    
}
