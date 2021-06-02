package view.components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import model.Vector;

public class Sensor {
	
	protected Ellipse2D.Double shape;
	protected Vector direction;
	protected Color color = Color.RED;
	
	public Sensor(double unitX, double unitY, Vector direction, float robotSize, float beamSize) {
		shape = new Ellipse2D.Double(unitX, unitY, 5, 5);
		this.direction = direction;
		
		shape.width *= robotSize;
		shape.height *= robotSize;
		shape.x *= robotSize;
		shape.y *= robotSize;

		direction.scale(beamSize * robotSize);
	}
	
	public void setLength(float length) {
		direction = direction.normalize()._scale(length);
	}
	
	public void draw(Graphics2D g2d) {
		g2d.setColor(color);
		g2d.fill(shape);
		g2d.drawLine((int) shape.getCenterX(),
				(int) shape.getCenterY(),
				(int) (shape.getCenterX() + direction.x),
				(int) (shape.getCenterY() + direction.y));
	}
	
	public Vector computePointAtEnd() {
		Vector vector = new Vector((float) (direction.x + shape.getCenterX()),
				(float) (direction.y + shape.getCenterY()));
		return vector;
	}
	
	public Vector computePointAtEnd(double rotationAngle) {
		Vector vector = new Vector(
				(float) (Math.cos(rotationAngle) * (direction.x + shape.getCenterX())) +
				-(float) (Math.sin(rotationAngle) * -(direction.y + shape.getCenterY())),
				(float) (Math.sin(rotationAngle) * (direction.x + shape.getCenterX())) +
				(float) (Math.cos(rotationAngle) * -(direction.y + shape.getCenterY())));
		return vector;
	}

}
