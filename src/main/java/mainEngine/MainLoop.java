package mainEngine;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import bvh.AABBBvhCollisionDetection;
import entities.Camera;
import entities.DynamicObject;
import entities.Entity;
import entities.Light;
import models.RawModel;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.ObjectLoader;
import textures.ModelTexture;

public class MainLoop {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Loader loader = new Loader();
		
		//load object model 1 [dynamic]
		RawModel model1 = ObjectLoader.loadObjModel("bunny", loader);
		TexturedModel texturedModel1 = new TexturedModel(model1,new ModelTexture(loader.loadTexture("cream")));
		ModelTexture texture1 = texturedModel1.getTexture();
		texture1.setShineDamper(10);
		texture1.setReflectivity(1);
		DynamicObject dynamicObject = new DynamicObject(texturedModel1, new Vector3f(130, 0, -20), 0, 180, 0, 1f);
		
		//load object model 2 [static]
		RawModel model2 = ObjectLoader.loadObjModel("bunny", loader);
		TexturedModel texturedModel2 = new TexturedModel(model2,new ModelTexture(loader.loadTexture("green")));		
		ModelTexture texture2 = texturedModel2.getTexture();
		texture2.setShineDamper(10);
		texture2.setReflectivity(1);		
		List<Entity> entitiesList = new ArrayList<Entity>();
		entitiesList.add(new Entity(texturedModel2, new Vector3f(110,0,-20), 0, 180, 0, 1f));
		
		//load other entities
		Camera camera = new Camera(dynamicObject);
		Light light = new Light(new Vector3f(200,400,200), new Vector3f(1,1,1));
		MasterRenderer renderer = new MasterRenderer();
		int renderCounter =0;
		
		//display update
		while(!Display.isCloseRequested()){	
			System.out.println("\n");
			long updateStartTime = System.currentTimeMillis();
			camera.move();
			dynamicObject.move();
			renderer.processEntity(dynamicObject);
			for (Entity entity:entitiesList) {
				renderer.processEntity(entity);				
			}			
			renderer.render(light, camera, renderCounter);	
			long broadPhaseStartTime = System.nanoTime();
			boolean broadPhaseFlag = AABBBvhCollisionDetection.broadPhaseCollisionDetection(model1, model2);
			System.out.println("Broad Phase time (ms):" + ((System.nanoTime() - broadPhaseStartTime) * (1/1000000f)));
			if(broadPhaseFlag) {
				long narrowPhaseStartTime = System.currentTimeMillis();
				model1.getAabbTree().buildTree(model1.getIndexedModel().getPrimitives(), model1.getAabb());
				boolean collisionFlag = AABBBvhCollisionDetection.collisionAxisAlignedBoundingBox(model1, model2);
				System.out.println("\nCollision Detected Status: " + collisionFlag);
				System.out.println("Narrow Phase time (ms):" + (System.currentTimeMillis() - narrowPhaseStartTime));
				
			}
			DisplayManager.updateDisplay();
			renderCounter = 1;
			long updateEndTime = System.currentTimeMillis() - updateStartTime;
			System.out.println("Total update time per frame (ms) :" + updateEndTime);
		}

		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}
