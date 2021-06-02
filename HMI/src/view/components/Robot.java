package view.components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;

import model.Vector;

public class Robot {

	public static final int FRONT_SENSOR = 2;
	public static final int RIGHT_FRONT_SENSOR = 1;
	public static final int RIGHT_BACK_SENSOR = 0;
	
	private RobotMap robotMap;
	
	private float robotShapeScale = 0.5f;
	private float captorBeamSize = robotShapeScale;
	
	private Polygon robotShape = new Polygon(new int[] { -15, -15, 15, 15 }, new int[] { 17, -17, -17, 17 }, 4);
	
	private Sensor[] sensors = new Sensor[] {
			new Sensor(15-2.5, -10.0, new Vector(1f, 0f), robotShapeScale, 400),
			new Sensor(15-2.5, 5.0, new Vector(1f, 0f), robotShapeScale, 400),
			new Sensor(-2, 17-2.5, new Vector(0f, 1f), robotShapeScale, 250)
	};
	
	protected Vector location;
	protected Vector lastLocation;
	protected Vector lastDirection = new Vector(0, -1);
	protected double rotation;
	
	public Robot(RobotMap robotMap, Vector startingLocation) {
		this.robotMap = robotMap;
		location = startingLocation;
		lastLocation = startingLocation.copy();
		rotation = directionToAngle(lastDirection);
		
		for(int i = 0; i < robotShape.npoints; i++) {
			robotShape.xpoints[i] *= robotShapeScale;
			robotShape.ypoints[i] *= robotShapeScale;
		}
	}
	
	public void update() {
		lastLocation = location.copy();
	}
	
	public void draw(Graphics2D g2d) {
		g2d.translate(location.x, location.y);
		g2d.rotate(rotation);
		g2d.scale(1, -1); // Flip the robot up-side down
		g2d.fill(robotShape);
		
		g2d.setColor(Color.BLUE);
		g2d.fillOval(-5, -5, 10, 10);
		
		for(int i = 0; i < sensors.length; i++) {
			sensors[i].draw(g2d);
			sensors[i].color = Color.RED;
//			g2d.setColor(Color.BLACK);
//			g2d.fillOval((int) point.x - 5, (int) point.y - 5, 10, 10);
		}
		
	}
	
	public void activateSensor(int sensorId) {
		if(sensorId >= 0 && sensorId < sensors.length) {
			Sensor sensor = sensors[sensorId];
			sensor.color = Color.GREEN;
			Vector point = sensor.computePointAtEnd(rotation);
			robotMap.addPoint(point._add(location));
		}
	}
	
	public void desactivateSensor(int sensorId) {
		if(sensorId >= 0 && sensorId < sensors.length) {
			Sensor sensor = sensors[sensorId];
			sensor.color = Color.RED;
		}
	}
	
	public void setSensorLength(int sensorId, float length) {
		if(sensorId >= 0 && sensorId < sensors.length) {
			Sensor sensor = sensors[sensorId];
			sensor.setLength(length);
		}
	}
	
	public boolean isMoving() {
		return location.dist(lastLocation) != 0;
	}
	
	private double directionToAngle(Vector direction) {
		return direction.angle(new Vector(1, 0)) + (Math.PI / 2);
	}

}
