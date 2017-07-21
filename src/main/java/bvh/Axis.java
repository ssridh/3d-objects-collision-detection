package bvh;

public enum Axis {
	X_AXIS(0), 
	Y_AXIS(1), 
	Z_AXIS(2), 
	NO_AXIS(-1);
	
	//0 (x axis), 1 (y axis), 2 (z axis)
    public final int longestAxis;

    Axis(int longestAxis) {
        this.longestAxis = longestAxis;
    }

}
