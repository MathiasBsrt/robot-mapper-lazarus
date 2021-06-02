package view.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.Vector;
import model.controller.ControllerWrapper;
import view.components.controls.MouseAndControllerJoystick;
import view.components.controls.MouvementInputs;
import view.components.controls.SpeedSlider;

/**
 * 
 * @author TRAFNY Theo - UPSSITECH
 *
 *         TODO: - Bind controller X button for the autonomous cartography
 *         switch. - Refactor the code.
 */
public class RobotMapSimulation extends RobotMap {

	private static final long serialVersionUID = -396510880625863746L;

	// Values to handle the robot's location, velocity (only for the simulation),
	// direction and rotation.
	// The two last values are the same but keep the program readable.
	private Vector robotVelocity = new Vector(0, 0);
	private Vector lastDirection = new Vector(0, -1);

	private boolean forceVelocity = false;
	

	/**
	 * The maxSpeed represents the speed of the robot at the maximum points of the
	 * joystick.
	 */
	private float maxSpeed = 5;

	private MouvementInputs mouvementInputs;

	public RobotMapSimulation(Joystick joystick, SpeedSlider speedSlider, ToggleButtonImage performanceButton,
			ToggleSwitch autonomousCartographySwitch, ImageIndicator robotIsRunningIndicator,
			ImageIndicator robotStoppedIndicator) {
		super(robotIsRunningIndicator, robotStoppedIndicator);
		this.mouvementInputs = new MouseAndControllerJoystick(joystick, speedSlider, performanceButton, this,
				autonomousCartographySwitch);
	}

	@Override
	public void beforeDrawing() {
		robot.update();
		Vector direction = mouvementInputs.computeDirection();
		float angle = mouvementInputs.computeAngle();
		float magnitude = mouvementInputs.computeMagnitude();
		if (direction.mag() != 0)
			lastDirection = direction;
		if (angle != 0)
			robot.rotation = (float) (-angle + Math.PI / 2);
		if (!forceVelocity) {
			robotVelocity.x = maxSpeed * magnitude * lastDirection.x;
			robotVelocity.y = maxSpeed * magnitude * lastDirection.y;
		}
		mouvementInputs.update();
		
		robot.location.add(robotVelocity);
	}

	/**
	 * Allow the robot to go forward at a certain speed until the method stop() is
	 * called.
	 * 
	 * @param speed
	 */
	public void forward(float speed) {
		robotVelocity.x = speed * lastDirection.x;
		robotVelocity.y = speed * lastDirection.y;
		forceVelocity = true;
	}

	/**
	 * Allow the robot to go backward at a certain speed until the method stop() is
	 * called.
	 * 
	 * @param speed
	 */
	public void backward(float speed) {
		robotVelocity.x -= speed * lastDirection.x;
		robotVelocity.y -= speed * lastDirection.y;
		forceVelocity = true;
	}

	/**
	 * Reset the velocity of the robot
	 */
	public void stop() {
		robotVelocity.x = 0;
		robotVelocity.y = 0;
		forceVelocity = false;
	}

	/**
	 * The maxSpeed represents the speed of the robot at the maximum points of the
	 * joystick.
	 * 
	 * @param maxSpeed
	 */
	public void setMaxSpeed(float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
	
	public void setConfiguration(float x, float y, float angle) {
		angle = (float) ((-angle + Math.PI / 2) % (2 * Math.PI));
		robot.location.x = x;
		robot.location.y = y;
		robot.rotation = angle;
	}

	@Override
	public void beforeDrawing(Graphics g) {
		// Not used
	}

}
