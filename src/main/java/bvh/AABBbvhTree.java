package bvh;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import boundingBox.AABB;

public class AABBbvhTree {

	public AABBNode rootNode;
	private int minimumPrimitives = 1;
	private int maximumDepth = 15;
	private Matrix4f transformationMatrix;

	public AABBbvhTree() {
		rootNode = null;
		transformationMatrix = null;
	}

	private void statistics(long time) {
		if (rootNode == null) {
			return;
		}
		long lTime = System.currentTimeMillis() - time;

		String statistics = "Bounding Volume Hierarchy Tree build time (ms) :" + lTime;
		statistics += "\nTotal number of primitives in BVH :" + TriangleCount(rootNode);
		statistics += "\nTree depth :" + (getNodeDepth(rootNode) -1);
		System.out.print(statistics);
	}

	public void buildTree(List<Primitive> triangles, AABB bbox) {
		long time = System.currentTimeMillis();
		if (rootNode != null) {
			rootNode = null;
		}
		rootNode = new AABBNode();
		//
		rootNode.triangles = triangles;
		rootNode.aabb = bbox;
		// build child nodes
		buildTree(rootNode, 0);
		statistics(time);
	}

	private void buildTree(AABBNode node, int depth) {
		if (node == null || node.leftChild != null || node.rightChild != null) {
			throw new IllegalStateException("!!! BVH: broken tree");
		}
		Vector3f min = node.aabb.getMin();
		Vector3f max = node.aabb.getMax();
		// setup node's split axis based on the longest axis
		if (node.triangles.size() > minimumPrimitives) {
			if(((max.x - min.x) >= (max.y - min.y)) && ((max.x - min.x) >= (max.z - min.z))) {
				node.splitAxis = Axis.X_AXIS;	        		
			} else if (((max.y - min.y) >= (max.x - min.x)) && ((max.y-min.y) >= (max.z-min.z))) {
				node.splitAxis = Axis.Y_AXIS;
			} else {
				node.splitAxis = Axis.Z_AXIS;
			}
			node.splitPlane = findSplitPlane(node);
		} else {
			node.splitAxis = Axis.NO_AXIS;
			return;
		}

		if (node.splitAxis.equals(Axis.NO_AXIS)) {
			return;
		}

		if (node.triangles.size() > minimumPrimitives && depth < maximumDepth) {
			node.leftChild = new AABBNode();
			node.rightChild = new AABBNode();

			node.leftChild.aabb = new AABB(min, max);
			if(node.splitAxis == Axis.X_AXIS) {
				node.leftChild.aabb.setMax(new Vector3f(node.splitPlane, max.y, max.z));
			} else if (node.splitAxis == Axis.Y_AXIS) {
				node.leftChild.aabb.setMax(new Vector3f(max.x, node.splitPlane, max.z));
			} else {
				node.leftChild.aabb.setMax(new Vector3f(max.x, max.y, node.splitPlane));
			} 

			node.rightChild.aabb = new AABB(min, max);
			if(node.splitAxis == Axis.X_AXIS) {
				node.rightChild.aabb.setMin(new Vector3f(node.splitPlane, min.y, min.z));
			} else if (node.splitAxis == Axis.Y_AXIS) {
				node.rightChild.aabb.setMin(new Vector3f(min.x, node.splitPlane, min.z));
			} else {
				node.rightChild.aabb.setMin(new Vector3f(min.x, min.y, node.splitPlane));
			} 

			// check based on the split plane where to put the Triangles
			for (int i = 0; i < node.triangles.size(); i++) {
				AABB box = node.triangles.get(i).getAABB();

				// if Triangle lies completly on the right side
				if (AABBNode.getLongestAxisValue(box.getMin(), node.splitAxis.longestAxis) >= node.splitPlane) {
					node.rightChild.triangles.add(node.triangles.get(i));

					// if Triangle lies completly on the left side
				} else {
					if (AABBNode.getLongestAxisValue(box.getMax(), node.splitAxis.longestAxis) <= node.splitPlane) {
						node.leftChild.triangles.add(node.triangles.get(i));

						// for the rest of cases we just put the Triangle in both subtrees
					} else {
						if(node.leftChild.triangles.size() < node.rightChild.triangles.size()) {
							node.leftChild.triangles.add(node.triangles.get(i));
						} else {
							node.rightChild.triangles.add(node.triangles.get(i));
						}

					}
				}
			}
			node.triangles.clear();

			buildTree(node.leftChild, depth + 1);
			buildTree(node.rightChild, depth + 1);
			// do return
			return;
		}
		node.splitAxis = Axis.NO_AXIS;

	}

	private float findSplitPlane(AABBNode node) {
		if (node == null) {
			return Float.POSITIVE_INFINITY;
		}

		float avg = 0.0f;
		for (int i = 0; i < node.triangles.size(); i++) {
			AABB bounds = node.triangles.get(i).getAABB();
			avg += (AABBNode.getLongestAxisValue(bounds.getMin(), node.splitAxis.longestAxis) + AABBNode.getLongestAxisValue(bounds.getMax(), node.splitAxis.longestAxis)) * 0.5f;
		}
		avg /= node.triangles.size();
		return avg;	        
	}

	public Matrix4f getTransformationMatrix() {
		return transformationMatrix;
	}

	public void setTransformationMatrix(Matrix4f transformationMatrix) {
		this.transformationMatrix = transformationMatrix;
	}

	public void update(AABBNode node, List<Primitive> primitives, Vector3f min, Vector3f max) {


	}

	public void reconstructTree(List<Primitive> triangles, AABB bbox) {
		if (rootNode != null) {
			rootNode = null;
		}
		rootNode = new AABBNode();
		//
		rootNode.triangles = triangles;
		rootNode.aabb = bbox;
		reconstructTree(rootNode, 0);

	}

	private void reconstructTree(AABBNode node, int depth) {
		Vector3f min = node.aabb.getMin();
		Vector3f max = node.aabb.getMax();
		// setup node's split axis and plane
		if (node.triangles.size() > minimumPrimitives) {
			if(((max.x - min.x) >= (max.y - min.y)) && ((max.x - min.x) >= (max.z - min.z))) {
				node.splitAxis = Axis.X_AXIS;	        		
			} else if (((max.y - min.y) >= (max.x - min.x)) && ((max.y-min.y) >= (max.z-min.z))) {
				node.splitAxis = Axis.Y_AXIS;
			} else {
				node.splitAxis = Axis.Z_AXIS;
			}
			node.splitPlane = findSplitPlane(node);
		} else {
			node.splitAxis = Axis.NO_AXIS;
			return;
		}

		if (node.splitAxis.equals(Axis.NO_AXIS)) {
			return;
		}
		// System.out.println("Min =" +min +"; Max = " + max);
		if (node.triangles.size() > minimumPrimitives && depth < maximumDepth) {
			node.leftChild = new AABBNode();
			node.rightChild = new AABBNode();

			node.leftChild.aabb = new AABB(min, max);
			if(node.splitAxis == Axis.X_AXIS) {
				node.leftChild.aabb.setMax(new Vector3f(node.splitPlane, max.y, max.z));
			} else if (node.splitAxis == Axis.Y_AXIS) {
				node.leftChild.aabb.setMax(new Vector3f(max.x, node.splitPlane, max.z));
			} else {
				node.leftChild.aabb.setMax(new Vector3f(max.x, max.y, node.splitPlane));
			} 

			node.rightChild.aabb = new AABB(min, max);
			if(node.splitAxis == Axis.X_AXIS) {
				node.rightChild.aabb.setMin(new Vector3f(node.splitPlane, min.y, min.z));
			} else if (node.splitAxis == Axis.Y_AXIS) {
				node.rightChild.aabb.setMin(new Vector3f(min.x, node.splitPlane, min.z));
			} else {
				node.rightChild.aabb.setMin(new Vector3f(min.x, min.y, node.splitPlane));
			} 

			// add triangles to child nodes
			for (int i = 0; i < node.triangles.size(); i++) {
				AABB box = node.triangles.get(i).getAABB();
				//
				if (AABBNode.getLongestAxisValue(box.getMin(), node.splitAxis.longestAxis) >= node.splitPlane) {
					node.rightChild.triangles.add(node.triangles.get(i));
				} else {
					if (AABBNode.getLongestAxisValue(box.getMax(), node.splitAxis.longestAxis) <= node.splitPlane) {
						node.leftChild.triangles.add(node.triangles.get(i));
					} else {
						// divide nodes based on node size						
						if(node.leftChild.triangles.size() < node.rightChild.triangles.size()) {
							node.leftChild.triangles.add(node.triangles.get(i));
						} else {
							node.rightChild.triangles.add(node.triangles.get(i));
						}

					}
				}
			}
			node.triangles.clear();

			buildTree(node.leftChild, depth + 1);
			buildTree(node.rightChild, depth + 1);
			return;
		}
		node.splitAxis = Axis.NO_AXIS;

	}


	public static int TriangleCount(AABBNode node) {
		if (!node.isLeafNode())
			return TriangleCount(node.leftChild) + TriangleCount(node.rightChild);
		return node.triangles.size();
	}
	
	public static int getNodeDepth(AABBNode node)
    {
    	if (node == null)
    		return 0;  
    	int leftChildDepth = getNodeDepth(node.leftChild);
    	int rightChildDepth = getNodeDepth(node.rightChild);
    	if (leftChildDepth > rightChildDepth)
            return (leftChildDepth + 1);
        else
            return (rightChildDepth + 1);
    }
}
