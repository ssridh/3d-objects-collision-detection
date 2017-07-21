package bvh;

import org.lwjgl.util.vector.Vector3f;

import boundingBox.AABB;
import utils.Maths;

public class Primitive {
	 public Vector3f v0;
     public Vector3f v1;
     public Vector3f v2;
     public AABB aabb;

     public AABB getAABB() {
        Vector3f min = new Vector3f();
        Vector3f max = new Vector3f();
    	aabb = new AABB();
        min = Maths.getMinVector(v0, v1);
        min = Maths.getMinVector(min, v2);
        max = Maths.getMaxVector(v0, v1);
        max = Maths.getMaxVector(max, v2);
        
        aabb.setMin(min);
        aabb.setMax(max);     
        return aabb;
     }


}
