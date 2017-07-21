package bvh;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

public class PrimitiveTest {

	public static boolean primitiveCollision(AABBNode node1, AABBNode node2) {

		List<Primitive> objTri1 = node1.triangles;
		List<Primitive> objTri2 = node2.triangles;
		for(Primitive tri1: objTri1) {
			for(Primitive tri2 : objTri2) {
				if(isIntersecting(tri1, tri2)) {
					return true;
				}
			}
		}

		return false;

	}

	@SuppressWarnings("unused")
	public static boolean isIntersecting(Primitive prim1, Primitive prim2) {
		//compute plane equation for triangle 1
		float du0, du1, du2, dv0,dv1,dv2;
		float du0du1,du0du2,dv0dv1,dv0dv2;
		int index = 999;
		float vp0 = 0,vp1 = 0,vp2 = 0;
		float up0 = 0,up1 = 0,up2 = 0;
		int dist = 0;
		Vector3f di = new Vector3f();
		Vector3f e1 = new Vector3f();
		Vector3f.sub(prim1.v0, prim1.v1, e1);

		Vector3f e2 = new Vector3f();
		Vector3f.sub(prim1.v1, prim1.v0, e2);

		Vector3f n1 = new Vector3f();
		Vector3f.cross(e1, e2, n1);

		float d1 = - Vector3f.dot(n1, prim1.v0);

		du0=Vector3f.dot(n1, prim2.v0) + d1;
		du1=Vector3f.dot(n1, prim2.v1) + d1;
		du2=Vector3f.dot(n1, prim2.v1) + d1;

		du0du1=du0*du1;
		du0du2=du0*du2;

		if(du0du1>0.0f && du0du2>0.0f) 
			return false; 

		
		Vector3f.sub(prim1.v0, prim1.v1, e1);

		Vector3f.sub(prim1.v1, prim1.v0, e2);

		Vector3f n2 = new Vector3f();
		Vector3f.cross(e1, e2, n2);

		float d2 = - Vector3f.dot(n2, prim1.v0);

		dv0=Vector3f.dot(n2,prim1.v0)+d2;
		dv1=Vector3f.dot(n2,prim1.v1)+d2;
		dv2=Vector3f.dot(n2,prim1.v2)+d2;

		dv0dv1=dv0*dv1;
		dv0dv2=dv0*dv2;

		if(dv0dv1>0.0f && dv0dv2>0.0f) 
			return false;    

		
		Vector3f.cross(n1, n2, di);
		float max = di.x;
		float bb = di.y;
		float cc = di.z;

		if(bb > max) {
			max = bb;
			index = 1;
		}
		if(cc > max) {
			max = cc;
			index = 2;			
		}

		
		if(index == 0) {
			vp0=prim1.v0.x;
			vp1=prim1.v1.x;
			vp2=prim1.v2.x;
			up0=prim2.v0.x;
			up1=prim2.v1.x;
			up2=prim2.v2.x;
		} else if(index == 1) {
			vp0=prim1.v0.y;
			vp1=prim1.v1.y;
			vp2=prim1.v2.y;
			up0=prim2.v0.y;
			up1=prim2.v1.y;
			up2=prim2.v2.y;
		} else if(index == 2) {
			vp0=prim1.v0.z;
			vp1=prim1.v1.z;
			vp2=prim1.v2.z;
			up0=prim2.v0.z;
			up1=prim2.v1.z;
			up2=prim2.v2.z;
		}

		/* compute interval for triangle 1 */
		float a,b,c,x0,x1;
		if(dv0dv1>0.0f)
		{
			a=vp2; b=(vp0-vp2)*dv2; c=(vp1-vp2)*dv2; x0=dv2-dv0; x1=dv2-dv1;
		}
		else if(dv0dv2>0.0f)
		{
			a=vp1; b=(vp0-vp1)*dv1; c=(vp2-vp1)*dv1; x0=dv1-dv0; x1=dv1-dv2;
		}
		else if(dv1*dv2>0.0f || dv0!=0.0f)
		{
			a=vp0; b=(vp1-vp0)*dv0; c=(vp2-vp0)*dv0; x0=dv0-dv1; x1=dv0-dv2; 
		}
		else if(dv1!=0.0f)
		{
			a=vp1; b=(vp0-vp1)*dv1; c=(vp2-vp1)*dv1; x0=dv1-dv0; x1=dv1-dv2;
		}
		else if(dv2!=0.0f)
		{
			a=vp2; b=(vp0-vp2)*dv2; c=(vp1-vp2)*dv2; x0=dv2-dv0; x1=dv2-dv1;
		} 
		else 
		{ 
			dist = coplanarTriTri(n1,prim1,prim2); 
		}

		float d=0,e=0,f=0,y0=0,y1=0;
		if(du0du1>0.0f)
		{
			d=up2; e=(up0-up2)*du2; f=(up1-up2)*du2; y0=du2-du0; y1=du2-du1;
		}
		else if(du0du2>0.0f)
		{
			d=up1; e=(up0-up1)*du1; f=(up2-up1)*du1; y0=du1-du0; y1=du1-du2;
		}
		else if(du1*du2>0.0f || du0!=0.0f)
		{
			d=up0; e=(up1-up0)*du0; f=(up2-up0)*du0; y0=du0-du1; y1=du0-du2; 
		}
		else if(du1!=0.0f)
		{
			d=up1; e=(up0-up1)*du1; f=(up2-up1)*du1; y0=du1-du0; y1=du1-du2;
		}
		else if(du2!=0.0f)
		{
			d=up2; e=(up0-up2)*du2; f=(up1-up2)*du2; y0=du2-du0; y1=du2-du1;
		} 
		else 
		{ 
			dist = coplanarTriTri(n1,prim1,prim2); 
		}
		if(dist == 0) {
			return false;
		} else if(dist == 1) {
			
			return true;
		}
		return false;

	}

	private static int coplanarTriTri(Vector3f n, Primitive prim1, Primitive prim2) {
		Vector3f a = new Vector3f();
		int i0,i1;
		int dist;
		a.x = Math.abs(n.x);
		a.y = Math.abs(n.y);
		a.z = Math.abs(n.z);
		if(a.x > a.y) {
			if(a.x > a.z) {
				i0 = 1;
				i1 = 2;
			} else {
				i0=0;      
				i1=1;
			}
		} else {
			if(a.z > a.y) {
				i0=0;      
				i1=1;
			} else {
				i0=0;      
				i1=2;
			}
		}
		dist = edgeTriangleTest(prim1.v0,prim1.v1, prim2, i0, i1);
		if(dist == 1) {			
			return 1;
		}
		dist = edgeTriangleTest(prim1.v1, prim1.v2, prim2, i0, i1);
		if(dist == 1) {			
			return 1;
		}
		dist = edgeTriangleTest(prim1.v2, prim1.v0, prim2, i0, i1);
		if(dist == 1) {			
			return 1;
		}
		dist = pointInTriangleTest(prim1.v0, prim2, i0, i1);
		if(dist == 1) {			
			return 1;
		}
		dist = pointInTriangleTest(prim2.v0, prim1, i0, i1);
		if(dist == 1) {			
			return 1;
		}
		return 0;

	}

	private static int edgeTriangleTest(Vector3f v1, Vector3f v2, Primitive prim, int i0, int i1) {
		float Ax,Ay,Bx,By,Cx,Cy,e,d,f;   
		if(i0 == 0) {
			Ax = v2.x - v1.x;			
		} else if (i0 == 1) {
			Ax = v2.y - v1.y;
		} else {
			Ax = v2.z - v1.z;
		}
		if(i1 == 0) {
			Ay = v2.x - v1.x;			
		} else if (i1 == 1) {
			Ay = v2.y - v1.y;
		} else {
			Ay = v2.z - v1.z;
		}


		if(i0 == 0) {
			Bx = prim.v0.x - prim.v1.x;	
			Cx= v1.x - prim.v0.x; 
		} else if (i0 == 1) {
			Bx = prim.v0.y - prim.v1.y;	
			Cx= v1.y - prim.v0.y; 
		} else {
			Bx = prim.v0.y - prim.v1.y;	
			Cx= v1.y - prim.v0.y; 
		}
		if(i1 == 0) {
			By = prim.v0.x - prim.v1.x;	
			Cy= v1.x - prim.v0.x; 		
		} else if (i1 == 1) {
			By = prim.v0.y - prim.v1.y;	
			Cy= v1.y - prim.v0.y; 		
		} else {
			By = prim.v0.y - prim.v1.y;	
			Cy= v1.y - prim.v0.y; 		
		}

		f=Ay*Bx-Ax*By;                                      
		d=By*Cx-Bx*Cy;                                      
		if((f>0 && d>=0 && d<=f) || (f<0 && d<=0 && d>=f))  
		{                                                   
			e=Ax*Cy-Ay*Cx;                                    
			if(f>0)                                           
			{                                                 
				if(e>=0 && e<=f) return 1;                      
			}                                                 
			else                                              
			{                                                 
				if(e<=0 && e>=f) return 1;                      
			}                                                 
		}

		       
		if(i0 == 0) {
			Bx = prim.v1.x - prim.v2.x;	
			Cx= v1.x - prim.v1.x; 
		} else if (i0 == 1) {
			Bx = prim.v0.y - prim.v2.y;	
			Cx= v1.y - prim.v1.y; 
		} else {
			Bx = prim.v1.y - prim.v2.y;	
			Cx= v1.y - prim.v1.y; 
		}
		if(i1 == 0) {
			By = prim.v1.x - prim.v2.x;	
			Cy= v1.x - prim.v1.x; 		
		} else if (i1 == 1) {
			By = prim.v1.y - prim.v2.y;	
			Cy= v1.y - prim.v1.y; 		
		} else {
			By = prim.v1.y - prim.v2.y;	
			Cy= v1.y - prim.v1.y; 		
		}

		f=Ay*Bx-Ax*By;                                      
		d=By*Cx-Bx*Cy;                                      
		if((f>0 && d>=0 && d<=f) || (f<0 && d<=0 && d>=f))  
		{                                                   
			e=Ax*Cy-Ay*Cx;                                    
			if(f>0)                                           
			{                                                 
				if(e>=0 && e<=f) return 1;                      
			}                                                 
			else                                              
			{                                                 
				if(e<=0 && e>=f) return 1;                      
			}                                                 
		}
		         
		if(i0 == 0) {
			Bx = prim.v2.x - prim.v0.x;	
			Cx= v1.x - prim.v2.x; 
		} else if (i0 == 1) {
			Bx = prim.v2.y - prim.v0.y;	
			Cx= v1.y - prim.v2.y; 
		} else {
			Bx = prim.v2.y - prim.v0.y;	
			Cx= v1.y - prim.v2.y; 
		}
		if(i1 == 0) {
			By = prim.v2.x - prim.v0.x;	
			Cy= v1.x - prim.v2.x; 		
		} else if (i1 == 1) {
			By = prim.v2.y - prim.v0.y;	
			Cy= v1.y - prim.v2.y; 		
		} else {
			By = prim.v2.y - prim.v0.y;	
			Cy= v1.y - prim.v2.y; 		
		}

		f=Ay*Bx-Ax*By;                                      
		d=By*Cx-Bx*Cy;                                      
		if((f>0 && d>=0 && d<=f) || (f<0 && d<=0 && d>=f))  
		{                                                   
			e=Ax*Cy-Ay*Cx;                                    
			if(f>0)                                           
			{                                                 
				if(e>=0 && e<=f) return 1;                      
			}                                                 
			else                                              
			{                                                 
				if(e<=0 && e>=f) return 1;                      
			}                                                 
		}

		return 0;
	}

	private static int pointInTriangleTest(Vector3f v, Primitive prim, int i0, int i1) {
		float a,b,c,d0,d1,d2;                     
		float[] v0 = new float[3];
		v0[0] = v.x;
		v0[1] = v.y;
		v0[2] = v.z;
		float[] u0 = new float[3];
		u0[0] = prim.v0.x;
		u0[1] = prim.v0.y;
		u0[2] = prim.v0.z;
		float[] u1 = new float[3];
		u1[0] = prim.v1.x;
		u1[1] = prim.v1.y;
		u1[2] = prim.v1.z;
		float[] u2 = new float[3];
		u2[0] = prim.v2.x;
		u2[1] = prim.v2.y;
		u2[2] = prim.v2.z;
		a=u1[i1]-u0[i1];                          
		b=-(u1[i0]-u0[i0]);                       
		c=-a*u0[i0]-b*u0[i1];                     
		d0=a*v0[i0]+b*v0[i1]+c;                   

		a=u2[i1]-u1[i1];                          
		b=-(u2[i0]-u1[i0]);                       
		c=-a*u1[i0]-b*u1[i1];                     
		d1=a*v0[i0]+b*v0[i1]+c;                   

		a=u0[i1]-u2[i1];                          
		b=-(u0[i0]-u2[i0]);                       
		c=-a*u2[i0]-b*u2[i1];                     
		d2=a*v0[i0]+b*v0[i1]+c;                   
		if(d0*d1>0.0)                             
		{                                         
			if(d0*d2>0.0) return 1;                 
		}                      




		return 0;
	}



}
