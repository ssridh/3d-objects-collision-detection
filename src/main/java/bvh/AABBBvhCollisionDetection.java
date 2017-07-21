package bvh;

import org.lwjgl.util.vector.Vector3f;
import models.RawModel;

public class AABBBvhCollisionDetection {
	
	public static boolean broadPhaseCollisionDetection(RawModel object1, RawModel object2) {
		if(collisionTest(object1.getAabbTree().rootNode, object1.getAabbTree().rootNode)) {
			return false;
		} 
		return true;
	}
	public static boolean collisionAxisAlignedBoundingBox(RawModel object1, RawModel object2) {
		return collision(object1.getAabbTree().rootNode, 
							object2.getAabbTree().rootNode);
		
	}
	
	private static boolean collision(AABBNode node1, AABBNode node2) {
		if(collisionTest(node1, node2)) {
			return false;
		} else {
			if((node1.leftChild == null) && (node1.rightChild == null) && (node2.leftChild == null) && (node2.rightChild == null)) {
				if(PrimitiveTest.primitiveCollision(node1, node2)) {
					return true;
				} else {
					return false;
				}
			} else {
				if((node1.leftChild == null) && (node1.rightChild == null)) {
					return (collision(node1, node2.leftChild)	|| collision(node1, node2.rightChild));
				} 
				if((node2.leftChild == null) && (node2.rightChild == null)) {
					return (collision(node2, node1.leftChild)	|| collision(node2, node1.rightChild));
				} 
				if(volumeBB(node1) > volumeBB(node2)) {
					return (collision(node1, node2.leftChild)	|| collision(node1, node2.rightChild));
				} else {
					return (collision(node2, node1.leftChild)	|| collision(node2, node1.rightChild));					
				}				
			}			
		}
	}
	
	private static boolean collisionTest(AABBNode node1, AABBNode node2) {
		Vector3f min1 = node1.aabb.getMin();
		Vector3f max1 = node1.aabb.getMax();
		Vector3f min2 = node2.aabb.getMin();
		Vector3f max2 = node2.aabb.getMax();
		
		if ((max1.x < min2.x) || (max1.y < min2.y) || (max1.z < min2.z) || (min1.x > max2.x) || (min1.y > max2.y) || (min1.z > max2.z)) {
			return true;
		} else {
			return false;
		}		
	}
	
	private static float volumeBB(AABBNode node) {
		return ((node.aabb.getMax().x - node.aabb.getMin().x) *
				(node.aabb.getMax().y - node.aabb.getMin().y) *
				(node.aabb.getMax().z - node.aabb.getMin().z));
	}
}
