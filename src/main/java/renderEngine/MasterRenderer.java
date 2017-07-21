package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import shaders.EntityShader;

public class MasterRenderer {
	

	private static final float FIELD_OF_VIEW = 70;
	private static final float NEAR_PLANE = 0.2f;
	private static final float FAR_PLANE = 1000;
	
	private Matrix4f projectionMatrix;
	private EntityShader entityShader = new EntityShader();
	private EntityRenderer entityRenderer;
		
	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
		
	public MasterRenderer() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);		
		createProjectionMatrix();
		entityRenderer = new EntityRenderer(entityShader, projectionMatrix);		
	}
	
	public void render(Light light, Camera camera, int renderCounter) {
		prepare();
		entityShader.start();
		entityShader.loadLight(light);
		entityShader.loadViewMatrix(camera);
		entityRenderer.render(entities, renderCounter);
		entityShader.stop();
		entities.clear();
	}
	
	public void processEntity(Entity entity) {
		TexturedModel entityModel = entity.getModel();
		List<Entity> entityList = entities.get(entityModel);
		if(entityList != null) {
			entityList.add(entity);
		} else {
			List<Entity> newEntities = new ArrayList<Entity>();
			newEntities.add(entity);
			entities.put(entityModel, newEntities);
		}		
	}
	
	public void cleanUp() {
		entityShader.cleanUp();		
	}
	
	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(1,0,0,1);
	}
	
	private void createProjectionMatrix() {
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) (1f / Math.tan(Math.toRadians(FIELD_OF_VIEW / 2f)) * aspectRatio);
		float x_scale = y_scale/aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;
		
		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = - (( FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = - ((2 * FAR_PLANE * NEAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
		
	}

}
