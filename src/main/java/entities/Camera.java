package entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;


public class Camera {
	
	private float distanceFromObject = 50;
	private float angleAroundPlayer = 0;
	private Vector3f position = new Vector3f(100,15,50);
	private float pitch = 10;
	private float yaw =0;
	private float roll;
	private DynamicObject dynamicObject;
	
	
	public Camera(DynamicObject dynamicObject){
		this.dynamicObject = dynamicObject;
	}
	
	public Camera () {
		
	}
	public void move(){
		calculateZoom();
		calculatePitch();
		calculateAngleAround();
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		calculateCameraPosition(horizontalDistance, verticalDistance);
		this.yaw = 180 - (dynamicObject.getRotY() + angleAroundPlayer);
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
	
	
	private void calculateZoom() {
		float zoomLevel = Mouse.getDWheel() * 0.1f;
		distanceFromObject -= zoomLevel;
	}
	
	private void calculatePitch() {
		if(Mouse.isButtonDown(1)) {
			float pitchChange = Mouse.getDY() * 0.1f;
			pitch -= pitchChange;
		}
	}
	
	private void calculateAngleAround() {
		if(Mouse.isButtonDown(0)) {
			float angleChange = Mouse.getDX() * 0.1f;
			angleAroundPlayer -= angleChange;
		}
	}
	
	private float calculateHorizontalDistance() {
		return (float) (distanceFromObject * Math.cos(Math.toRadians(pitch)));
	}
	
	private float calculateVerticalDistance() {
		return (float) (distanceFromObject * Math.sin(Math.toRadians(pitch)));
	}
	
	private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
		float angle = dynamicObject.getRotY() + angleAroundPlayer;
		float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(angle)));
		float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(angle)));
		position.x = dynamicObject.getPosition().x - offsetX;
		position.z = dynamicObject.getPosition().z - offsetZ;
		position.y = dynamicObject.getPosition().y + verticalDistance;
	}
}
