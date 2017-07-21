package renderEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import boundingBox.AABB;
import bvh.Primitive;
import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import shaders.EntityShader;
import textures.ModelTexture;
import utils.Maths;

public class EntityRenderer {

	private EntityShader shader;

	public EntityRenderer(EntityShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

	public void render(Map<TexturedModel, List<Entity>> entityList, int renderCounter) {
		for (TexturedModel model : entityList.keySet()) {
			prepareTexturedModel(model);
			List<Entity> entities = entityList.get(model);
			for(Entity entity: entities) {
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);	
				prepareBoundingBox(entity.getModel().getRawModel().getAabb());
				GL11.glDrawElements(GL11.GL_LINE_LOOP, 4,
						GL11.GL_UNSIGNED_INT, 0);
				GL11.glDrawElements(GL11.GL_LINE_LOOP, 4,
						GL11.GL_UNSIGNED_INT, 4*4);
				GL11.glDrawElements(GL11.GL_LINE_LOOP, 8,
						GL11.GL_UNSIGNED_INT, 8*4);	
				applyWorldCoordinates(entity, renderCounter);				
			}
			unbindBoundingBox();
			unbindTexturedModel();	

		}
	}

	private void applyWorldCoordinates(Entity entity, int renderCounter) {		List<Vector3f> vertices = entity.getModel().getRawModel().getIndexedModel().getVertices();
	List<Integer> indices = entity.getModel().getRawModel().getIndexedModel().getIndices();
	List<Primitive> primitives = new ArrayList<Primitive>();
	List<Vector3f> transformedVertices = new ArrayList<Vector3f>(); 
	for(Vector3f vertex : vertices) {
		Vector4f temp = new Vector4f();
		Matrix4f.transform(entity.getModel().getRawModel().getAabbTree().getTransformationMatrix(), new Vector4f(vertex.x, vertex.y, vertex.z, 1.0f), temp);
		transformedVertices.add(new Vector3f(temp.x, temp.y, temp.z));			
	}

	//add primitives in list
	for (int i = 0; i < indices.size(); i++) {
		Primitive triangle = new Primitive();
		triangle.v0 = transformedVertices.get(indices.get(i));
		triangle.v1 = transformedVertices.get(indices.get(i+1));
		triangle.v2 = transformedVertices.get(indices.get(i+2));
		primitives.add(triangle);
		i = i+2;
	}
	
	Vector3f max = new Vector3f(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);
	Vector3f min = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
	
	for(Vector3f vertex: transformedVertices){			
		if(vertex.x < min.x) min.x = vertex.x;
		if(vertex.y < min.y) min.y = vertex.y;
		if(vertex.z < min.z) min.z = vertex.z;
		if(vertex.x > max.x) max.x = vertex.x;
		if(vertex.y > max.y) max.y = vertex.y;
		if(vertex.z > max.z) max.z = vertex.z;
	}
	entity.getModel().getRawModel().getAabb().setMax(max);
	entity.getModel().getRawModel().getAabb().setMin(min);
	entity.getModel().getRawModel().getIndexedModel().setPrimitives(primitives);
	//update AABB Tree with world coords
	/*if(entity.getType() == Entity_Type.DYNAMIC) {
		entity.getModel().getRawModel().getAabbTree().buildTree(primitives, entity.getModel().getRawModel().getAabb());
	} else {
		if(renderCounter == 0) {
			entity.getModel().getRawModel().getAabbTree().buildTree(primitives, entity.getModel().getRawModel().getAabb());				
		}
	}*/
	if(renderCounter == 0) {
		entity.getModel().getRawModel().getAabbTree().buildTree(primitives, entity.getModel().getRawModel().getAabb());				
	}
	
}

	private void prepareTexturedModel (TexturedModel texturedModel) {
		RawModel model = texturedModel.getRawModel();
		GL30.glBindVertexArray(model.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		ModelTexture texture = texturedModel.getTexture();
		shader.loadShine(texture.getShineDamper(), texture.getReflectivity());
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE );
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturedModel.getTexture().getTextureID());		
	}

	private void unbindTexturedModel () {
		/*GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);*/
	}

	private void prepareInstance(Entity entity) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);	
		entity.getModel().getRawModel().getAabbTree().setTransformationMatrix(transformationMatrix);
	}

	private void prepareBoundingBox(AABB aabbModel) {
		GL30.glBindVertexArray(aabbModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);	
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);					
	}

	private void unbindBoundingBox() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

}
