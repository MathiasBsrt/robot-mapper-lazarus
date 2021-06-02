package decision;

//placeholder class for the robot
public class Robot {
	//the transform matrices for each of the sensors, [0,0] is the midpoint of the robot, distances in m
	
	protected static final Matrix S1_TRANS = new Matrix(new double[][] {{1, 0, 0.2},
																		{0, 1, 0},
																		{0, 0, 1}});
	protected static final Matrix S2_TRANS = new Matrix(new double[][] {{0, -1, 0.2},
																		{1, 0, 0.2}, //for some reason the y coordinates have to be inverted
																		{0, 0, 1}});
	protected static final Matrix S3_TRANS = new Matrix(new double[][] {{0, -1, -0.2},
																		{1, 0, 0.2},
																		{0, 0, 1}});
}
