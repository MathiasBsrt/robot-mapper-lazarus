package model.controller;

/**
 * An event listener interface used to listen
 * new states of the controller.
 * @author TRAFNY Theo - UPSSITECH
 *
 */
public interface ControllerStateListener {

	/**
	 * Called when the controller is no longer being used.
	 */
	public void onControllerStopBeingUsed();
	
}
