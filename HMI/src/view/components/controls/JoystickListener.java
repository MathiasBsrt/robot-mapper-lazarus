package view.components.controls;

import model.Vector;

/**
 * An event listener interface used to listen
 * joystick value changes.
 * @author TRAFNY Theo - UPSSITECH
 *
 */
public interface JoystickListener {
	
	/**
	 * Called when the joystick's values changed.
	 * @param angle The current angle of the joystick.
	 * @param magnitude The current magnitude of the joystick.
	 * @param direction The current direction of the joystick.
	 */
	public void onJoystickChange(float angle, float magnitude, Vector direction);

}
