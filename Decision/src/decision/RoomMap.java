package decision;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

import commande.CommandeListener;

public class RoomMap implements CommandeListener{
	static final int MAP_DEF_HEIGHT = 200;			//starting height of the map
	static final int MAP_DEF_WIDTH = 200;			//starting width of the map
	static final double NO_OBSTACLE_THRESH = 3.90;	//greater detected distances of sensors are considered to not be obstacles 
	static final int SCALE = 10;					//how many pixels a meter is, used to convert to img coords
	static final private double PIHALF = Math.PI / 2;
	private Graphics2D map;
	private BufferedImage img;
	private StatusMessage lastMsg;
	
	
	public RoomMap() {
		this.img = new BufferedImage(MAP_DEF_WIDTH, MAP_DEF_HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
		this.map = this.img.createGraphics();
		map.setColor(Color.GRAY);
		map.fillRect(0, 0, MAP_DEF_WIDTH, MAP_DEF_HEIGHT);
		map.setTransform(new AffineTransform(1, 0, 0, -1, MAP_DEF_WIDTH / 2, MAP_DEF_HEIGHT / 2)); //make middle the origin
		System.out.println(map.getTransform().toString());
	}
	
	public BufferedImage getImage() {
		return img;
	}

	public StatusMessage getRobPos(){
		return lastMsg;
	}
	
	
	/**
	 * Draws traces on the map for each of the reported distances and extends the map if necessary.
	 * 
	 * @param m the status of the robot, includes position, rotation and sensor distances
	 * */
	public void onReceiveData(StatusMessage m) {
		if(m == null) return;
		this.lastMsg = m;
		
		//due to the nature of img coords, this contains a mirror along the x-axis and a 90� rotation
		Matrix mRobToWorld = new Matrix(new double[][] {{Math.cos(m.getRot() + PIHALF), -Math.sin(m.getRot() + PIHALF), m.getX()},
														{Math.sin(m.getRot() + PIHALF), Math.cos(m.getRot() + PIHALF), m.getY()},
														{0, 0, 1}});
		
		//the origin point of each sensor
		Point2D s1Start = new Point2D.Float();
		Point2D s2Start = new Point2D.Float();
		Point2D s3Start = new Point2D.Float();
		
		//the end point/the point where the signal hit an obstacle
		Point2D s1End = new Point2D.Float();
		Point2D s2End = new Point2D.Float();
		Point2D s3End = new Point2D.Float();
		
		//placing the points at the right coordinates, already converted to img coords
		s1Start.setLocation(computeOrigin(mRobToWorld, Robot.S1_TRANS));
		s2Start.setLocation(computeOrigin(mRobToWorld, Robot.S2_TRANS));
		s3Start.setLocation(computeOrigin(mRobToWorld, Robot.S3_TRANS));
		s1End.setLocation(computeSensorEnd(mRobToWorld, Robot.S1_TRANS, m.getS1Dist()));
		s2End.setLocation(computeSensorEnd(mRobToWorld, Robot.S2_TRANS, m.getS2Dist()));
		s3End.setLocation(computeSensorEnd(mRobToWorld, Robot.S3_TRANS, m.getS3Dist()));
		
		//drawing the traces, update map size if necessary
		drawTrace(s1Start, s1End);
		drawTrace(s2Start, s2End);
		drawTrace(s3Start, s3End);
	}
	
	/**
	 * Computes the origin of the coordinate systems that results from pos * trans * SCALE.
	 * 
	 * @param	pos		usually the transform from world coordinates to robot coordinates
	 * @param	trans	usually the transform from robot coordinates to sensor coordinates
	 * @return origin of resulting coordinate system in img Coords
	 * @see SCALE
	 * */
	private Point2D computeOrigin(Matrix pos, Matrix trans) {
		Matrix mOut = Matrix.multiplyMatrix(pos,  trans);
		Point2D pOut = new Point2D.Float();
		pOut.setLocation(mOut.getElement(0, 2) * SCALE, mOut.getElement(1, 2) * SCALE);
		return pOut;
	}
	
	/**
	 * Computes pos * trans* {distance, 0, 1} * SCALE.
	 * 
	 * @param	pos			usually the transform from world coordinates to robot coordinates
	 * @param	trans		usually the transform from robot coordinates to sensor coordinates
	 * @param	distance	the distance the sensor sensed
	 * @return	the resulting point as a Point2D in img Coords
	 * @see SCALE
	 * 
	 * */
	private Point2D computeSensorEnd(Matrix pos, Matrix trans, float distance) {
		Matrix mOut = Matrix.multiplyMatrix(pos, Matrix.multiplyMatrix(trans, new Matrix(new double[][] {{distance},{0},{1}})));
		Point2D pOut = new Point2D.Float();
		pOut.setLocation(mOut.getElement(0, 0) * SCALE, mOut.getElement(1, 0) * SCALE);
		return pOut;
	}
	
	
	/**
	 * Draws a Sensor Trace from start to end, coloring everything beneath it white(free) and the end point black 
	 * if distance is below NO_OBSTACLE_THRESH.
	 * 
	 * @param	start	the starting point, usually the sensor
	 * @param	end		the end point, an obstacle or the end of the detection range
	 * @see NO_OBSTACLE_TRESH
	 * */
	private void drawTrace(Point2D start, Point2D end) {		
		//size up if either point is outside of image
		Rectangle hitRect = new Rectangle(0, 0, img.getWidth() - 1, img.getHeight() - 1);
		if(!map.hit(hitRect, new Line2D.Float(end, end), true) || !map.hit(hitRect, new Line2D.Float(start, start), true)) {
			sizeUp();
		}
		//draw trace
		//System.out.println("start:" + start.toString() + ", end:" + end.toString());
		map.setColor(Color.WHITE);
		map.draw(new Line2D.Float(start, end));
		if(start.distance(end) / SCALE < NO_OBSTACLE_THRESH){
			map.setColor(Color.BLACK);
			map.draw(new Line2D.Float(end, end));
		}
	}
	
	//double the size of the image and fills the new area with grey (unknown)
	private void sizeUp() {
		BufferedImage img = new BufferedImage(this.img.getWidth() * 2, this.img.getHeight() * 2, BufferedImage.TYPE_BYTE_GRAY);
		this.map = img.createGraphics();
		map.setColor(Color.GRAY);
		map.fillRect(0, 0, img.getWidth(), img.getHeight());
		img.getGraphics().drawImage(this.img, img.getWidth() / 4, img.getHeight() / 4, Color.GRAY, null);
		this.img = img;
		map.setTransform(new AffineTransform(1, 0, 0, -1, img.getWidth() / 2, img.getHeight() / 2));
	}
}
