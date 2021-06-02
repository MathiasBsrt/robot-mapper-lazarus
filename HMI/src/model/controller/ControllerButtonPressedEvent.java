package model.controller;

import com.studiohartman.jamepad.ControllerButton;

/**
 * An event listener interface used to listen when a button
 * has been pressed on the controller.
 * You can see {@link ControllerWrapper} to learn more on
 * why we have re-created a new Controller Wrapper to handle 
 * controller events.
 * @author TRAFNY Theo - UPSSITECH
 *
 */
public interface ControllerButtonPressedEvent {
	
	/**
	 * Called when a button has been pressed on the controller.
	 * @param button The pressed button.
	 */
	public void onControllerButtonPressed(ControllerButton button);

}
