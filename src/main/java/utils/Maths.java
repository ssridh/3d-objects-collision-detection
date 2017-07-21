package utils;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;

public class Maths {
	
	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1,0,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0,1,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0,0,1), matrix, matrix);
		Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);		
		return matrix;
	}

	public static Matrix4f createViewMatrix(Camera camera) {
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1,0,0), viewMatrix, viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0,1,0), viewMatrix, viewMatrix);
		Vector3f cameraPos = camera.getPosition();
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
		return viewMatrix;		
	}
	
	public static float distance(Vector3f v1, Vector3f v2)
	{
		return (float)Math.abs(Math.sqrt(
				Math.pow((v2.x-v1.x), 2) +
				Math.pow((v2.y-v1.y), 2) +
				Math.pow((v2.z-v1.z), 2)));
	}
	
	public static Vector3f getMinVector(Vector3f v1, Vector3f v2) {
		Vector3f minVector = new Vector3f();
		float minX;
		float minY;
		float minZ;
		
        minX = Math.min(v1.x, v2.x);
        minY = Math.min(v1.y, v2.y);
        minZ = Math.min(v1.z, v2.z);
        minVector = new Vector3f(minX,minY,minZ);
        return minVector;
    }
	
	public static Vector3f getMaxVector(Vector3f v1, Vector3f v2) {
		Vector3f maxVector = new Vector3f();
		float maxX;
		float maxY;
		float maxZ;
		
        maxX = Math.max(v1.x, v2.x);
        maxY = Math.max(v1.y, v2.y);
        maxZ = Math.max(v1.z, v2.z);
        maxVector = new Vector3f(maxX,maxY,maxZ);
        return maxVector;
    }
}
