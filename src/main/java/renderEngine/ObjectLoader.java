package renderEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import boundingBox.AABB;
import bvh.AABBbvhTree;
import bvh.Primitive;
import models.IndexedModel;
import models.RawModel;

public class ObjectLoader {

	private static List<Vector3f> vertices;
	private static List<Integer> indices;
	private static List<Primitive> triangles = new ArrayList<Primitive>();

	public static RawModel loadObjModel(String fileName, Loader loader) {
		FileReader fr = null;
		try {
			String dirPath = System.getProperty("user.dir");
			fr = new FileReader(new File("src/main/resources/"+fileName + ".obj"));
		} catch (FileNotFoundException e) {
			System.out.println("Couldn't load file!");
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(fr);
		String line;

		float[] verticesArray = null;
		float[] normalsArray = null;
		float[] textureArray = null;
		int[] indicesArray = null;
		List<Vector3f> objVertices = new ArrayList<Vector3f>();
		List<Vector2f> objTextures = new ArrayList<Vector2f>();
		List<Vector3f> objNormals = new ArrayList<Vector3f>();
		List<Integer> objIndices = new ArrayList<Integer>();


		try {

			while (true) {
				line = reader.readLine();
				String[] currentLine = line.split(" ");
				if (line.startsWith("v ")) {
					Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
					objVertices.add(vertex);
				} else if (line.startsWith("vt ")) {
					Vector2f texture = new Vector2f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]));
					objTextures.add(texture);
				} else if (line.startsWith("vn ")) {
					Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
					objNormals.add(normal);
				} else if (line.startsWith("f ")) {
					textureArray = new float[objVertices.size() * 2];
					normalsArray = new float[objVertices.size() * 3];
					break;
				}
			}
			while (line != null) {
				if (!line.startsWith("f ")) {
					line = reader.readLine();
					continue;
				}
				String[] currentLine = line.split(" ");
				String[] vertex1 = currentLine[1].split("/");
				String[] vertex2 = currentLine[2].split("/");
				String[] vertex3 = currentLine[3].split("/");

				processVertex(vertex1, objIndices, objTextures, objNormals, textureArray, normalsArray);
				processVertex(vertex2, objIndices, objTextures, objNormals, textureArray, normalsArray);
				processVertex(vertex3, objIndices, objTextures, objNormals, textureArray, normalsArray);
				line = reader.readLine();
			}
			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		verticesArray = new float[objVertices.size() * 3];
		indicesArray = new int[objIndices.size()];

		int vertexPointer = 0;
		for (Vector3f vertex : objVertices) {
			verticesArray[vertexPointer++] = vertex.x;
			verticesArray[vertexPointer++] = vertex.y;
			verticesArray[vertexPointer++] = vertex.z;
		}


		for (int i = 0; i < objIndices.size(); i++) {
			indicesArray[i] = objIndices.get(i);
		}
		
		//add primitives in list
		for (int i = 0; i < objIndices.size(); i++) {
			Primitive triangle = new Primitive();
			triangle.v0 = objVertices.get(objIndices.get(i));
			triangle.v1 = objVertices.get(objIndices.get(i+1));
			triangle.v2 = objVertices.get(objIndices.get(i+2));
			triangles.add(triangle);
			i = i+2;
		}
		
		vertices = objVertices;
		indices = objIndices;
		
		Vector3f max = new Vector3f();
		Vector3f min = new Vector3f();
		//initialize
		min.x = max.x = objVertices.get(0).x;
		min.y = max.y = objVertices.get(0).y;
		min.z = max.z = objVertices.get(0).z;
		for (Vector3f vertex : objVertices) {
			if(vertex.x < min.x) min.x = vertex.x;
			if(vertex.y < min.y) min.y = vertex.y;
			if(vertex.z < min.z) min.z = vertex.z;
			if(vertex.x > max.x) max.x = vertex.x;
			if(vertex.y > max.y) max.y = vertex.y;
			if(vertex.z > max.z) max.z = vertex.z;
		}
		float[] aabbPosition = {
				min.x, min.y, min.z,
				max.x, min.y, min.z,				
				max.x, max.y, min.z,
				min.x, max.y, min.z,
				min.x, min.y, max.z,
				max.x, min.y, max.z,
				max.x, max.y, max.z,
				min.x, max.y, max.z				
		};
		int[] aabbIndices = {
				0, 1, 2, 3, 4, 5,6,7,
				0, 4, 5, 1, 2, 6, 7, 3
		};
		AABBbvhTree aabbTree = new AABBbvhTree();
		AABB aabb = new AABB();
		IndexedModel indexedModel = new IndexedModel(vertices, indices, triangles);
		return loader.loadToVao(verticesArray, textureArray, normalsArray, indicesArray, aabbPosition, aabbIndices, aabb, aabbTree, indexedModel);

	}

	private static void processVertex(String[] vertexData, List<Integer> indices, List<Vector2f> textures, List<Vector3f> normals, float[] textureArray, float[] normalsArray) {
		int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
		indices.add(currentVertexPointer);
		Vector2f currentTex = textures.get(Integer.parseInt(vertexData[1]) - 1);
		textureArray[currentVertexPointer * 2] = currentTex.x;
		textureArray[currentVertexPointer * 2 + 1] = 1 - currentTex.y;
		Vector3f currentNorm = normals.get(Integer.parseInt(vertexData[2]) - 1);
		normalsArray[currentVertexPointer * 3] = currentNorm.x;
		normalsArray[currentVertexPointer * 3 + 1] = currentNorm.y;
		normalsArray[currentVertexPointer * 3 + 2] = currentNorm.z;
	}	
	
}
