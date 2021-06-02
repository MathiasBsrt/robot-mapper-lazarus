package view.components.controls;

import model.Vector;

/**
 * Describes the inputs used to move the robot.
 * @author TRAFNY Theo - UPSSITECH
 *
 */
public interface MouvementInputs {

	/**
	 * Compute the current angle to send to the robot
	 * @return The angle
	 */
	public float computeAngle();
	
	/**
	 * Compute the magnitude (as the speed) of the robot to send.
	 * @return The magnitude
	 */
	public float computeMagnitude();
	
	/**
	 * Compute the direction to send to the robot.
	 * @return The direction
	 */
	public Vector computeDirection();
	
	/**
	 * An update method, can be used by children to
	 * process others inputs if needed.
	 */
	public void update();
	
}
