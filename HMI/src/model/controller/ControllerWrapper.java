package model.controller;

import java.util.LinkedList;
import java.util.List;

import com.studiohartman.jamepad.ControllerButton;
import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

import model.settings.ProgramSettings;
import model.settings.Setting;
import model.settings.SettingChangeListener;

/**
 * This class is used to wrap and handle more easily interactions between
 * the game controller and the program.
 * In this way, we case use static calls to register listeners
 * to listen game controller events.
 * We have also created a "wrapper" over the current used API "Jamepad" because
 * the current implementation of this API is not really easy to use,
 * and we have seen some errors where the controller state return by the API
 * did not receive game controller events.
 * 
 * This class is also a singleton one.
 * 
 * @see ControllerButtonPressedEvent
 * @author TRAFNY Theo - UPSSITECH
 *
 */
public class ControllerWrapper implements SettingChangeListener {
	
	// This class is a Singleton, so we need to store the instance.
	private static class ControllerWrapperHolder {
		public static ControllerWrapper INSTANCE = new ControllerWrapper();
	}
	
	/**
	 *  Controller deadzones registered from the setting manager
	 */
	private static double joystick_deadzone;
	private static double trigger_deadzone;
	
	/**
	 * Hold a reference to the controller manager of the API "Jamepad"
	 */
	private static ControllerManager controllerManager;
	
	/**
	 * Handle multiple states. This can be used if there is more than one controller connected.
	 * Here we only use the first one, but we let this here so the wrapper can be enhanced.
	 */
	private static ControllerState[] lastStates = new ControllerState[5];
	
	/**
	 * Handle all states of each button
	 */
	private static boolean[] buttons = new boolean[ControllerButton.values().length];
	/**
	 * Handle all previous states of each buttons.
	 * With those two states holders, we can know is a button
	 * as been pressed, released or is being pressed.
	 */
	private static boolean[] lastButtons = new boolean[buttons.length];
	
	/**
	 * List of all controller button pressed event listeners.
	 */
	private static final List<ControllerButtonPressedEvent> listenersButtonPressed = new LinkedList<>();
	
	/**
	 * List of all controller state listeners.
	 */
	private static final List<ControllerStateListener> listenersState = new LinkedList<>();
	
	private static boolean isUsing = false;
	private static boolean wasUsing = false;

	public ControllerWrapper() {
		controllerManager = new ControllerManager();
		controllerManager.initSDLGamepad();
		// Register the controller wrapper to the settings so
		// we can configure controller deadzones.
		ProgramSettings.listen(this, Setting.CONTROLLER_JOYSTICK_DEADZONE);
		ProgramSettings.listen(this, Setting.CONTROLLER_TRIGGER_DEADZONE);
		joystick_deadzone = ProgramSettings.getDouble(Setting.CONTROLLER_JOYSTICK_DEADZONE);
		trigger_deadzone = ProgramSettings.getDouble(Setting.CONTROLLER_TRIGGER_DEADZONE);
	}
	
	/**
	 * Update controller's buttons states.
	 * @param controllerId The controller's id (0-indexed).
	 */
	public static void update(int controllerId) {
		// Fetch the current state of the controller with the id "controllerId".
		lastStates[controllerId] = controllerManager.getState(controllerId);

		// Set lastButtons states to the current buttons states.
		for(int i = 0; i < buttons.length; i++) {
			lastButtons[i] = buttons[i];
		}
		
		// Set all states by hand.
		buttons[0] = lastStates[controllerId].a;
		buttons[1] = lastStates[controllerId].b;
		buttons[2] = lastStates[controllerId].x;
		buttons[3] = lastStates[controllerId].y;
		buttons[4] = lastStates[controllerId].back;
		buttons[5] = lastStates[controllerId].guide;
		buttons[6] = lastStates[controllerId].start;
		buttons[7] = lastStates[controllerId].leftStickClick;
		buttons[8] = lastStates[controllerId].rightStickClick;
		buttons[9] = lastStates[controllerId].leftTrigger >= trigger_deadzone;
		buttons[10] = lastStates[controllerId].rightTrigger >= trigger_deadzone;
		buttons[11] = lastStates[controllerId].dpadUp;
		buttons[12] = lastStates[controllerId].dpadDown;
		buttons[13] = lastStates[controllerId].dpadLeft;
		buttons[14] = lastStates[controllerId].dpadRight;
		
		// Check values between "last" and "current" button states
		// to know if a button has been pressed, released or is being pressed.
		ControllerButton[] values = ControllerButton.values();
		for(int i = 0; i < buttons.length; i++) {
			if(buttons[i] && !lastButtons[i]) 
				notifyAllButtonPressedListener(values[i]);
		}
		
		wasUsing = isUsing;
		
		isUsing = isUsing(controllerId);
		
		if(wasUsing && !isUsing) notifyAllStateListener();
	}
	
	/**
	 * Get the current state (from the last update) of the controller with the id "controllerId".
	 * @param controllerId The id of the controller
	 * @return The current state of the controller
	 */
	public static ControllerState getState(int controllerId) {
		return lastStates[controllerId];
	}
	
	/**
	 * Is there a controller available ?
	 * @return boolean
	 */
	public static boolean controllerAvailable() {
		return controllerManager.getNumControllers() > 0;
	}
	
	/**
	 * Is the controller "controllerId" is currently being used.
	 * @param controllerId The id of the controller
	 * @return boolean
	 */
	public static boolean isUsing(int controllerId) {
		ControllerState state = controllerManager.getState(controllerId);
		boolean isUsed = false;
		isUsed |= state.a || state.b || state.y || state.x;
		isUsed |= state.dpadDown || state.dpadLeft || state.dpadRight || state.dpadUp;
		isUsed |= state.lb || state.rb;
		isUsed |= state.rightStickClick || state.leftStickClick;
		isUsed |= state.leftTrigger >= trigger_deadzone || state.rightTrigger >= trigger_deadzone;
		isUsed |= state.back || state.guide;
		isUsed |= state.leftStickMagnitude >= joystick_deadzone || state.rightStickMagnitude >= joystick_deadzone;
		return isUsed;
	}
	
	public static boolean stopUsing() {
		return wasUsing && !isUsing;
	}
	
	/**
	 * A static way to know if a button is currently being pressed.
	 * @param button The game controller button
	 * @return
	 */
	public static boolean isButton(ControllerButton button) {
		return buttons[button.ordinal()];
	}
	
	/**
	 * A static way to know if a button has been pressed.
	 * @param button The game controller button
	 * @return
	 */
	public static boolean isButtonPressed(ControllerButton button) {
		int index = button.ordinal();
		return buttons[index] && !lastButtons[index];
	}
	
	/**
	 * A static way to know if a button has been released.
	 * @param button The game controller button
	 * @return
	 */
	public static boolean isButtonReleased(ControllerButton button) {
		int index = button.ordinal();
		return !buttons[index] && lastButtons[index];
	}
	
	/**
	 * Register a controller button pressed event listener.
	 * @param listener The listener to register
	 * 
	 * @see ControllerButtonPressedEvent
	 */
	public static void addButtonPressedListener(ControllerButtonPressedEvent listener) {
		listenersButtonPressed.add(listener);
	}
	
	/**
	 * Unregister a controller button pressed event listener.
	 * @param listener The listener to unregister
	 * 
	 * @see ControllerButtonPressedEvent
	 */
	public static void removeButtonPressedListener(ControllerButtonPressedEvent listener) {
		listenersButtonPressed.remove(listener);
	}
	
	/**
	 * Notify all registered listener that a button has been pressed.
	 * @param button The button that has been pressed.
	 */
	private static void notifyAllButtonPressedListener(ControllerButton button) {
		int size = listenersButtonPressed.size();
		for(int i = 0; i < size; i++) {
			listenersButtonPressed.get(i).onControllerButtonPressed(button);
		}
	}
	
	/**
	 * Notify all registered listener that a button has been pressed.
	 * @param button The button that has been pressed.
	 */
	private static void notifyAllStateListener() {
		int size = listenersState.size();
		for(int i = 0; i < size; i++) {
			listenersState.get(i).onControllerStopBeingUsed();
		}
	}
	
	/**
	 * Get the current handled controller manager from the external API "Jamepad".
	 * @return controllerManager
	 */
	public static ControllerManager getManager() {
		return controllerManager;
	}
	
	/**
	 * Init before anything the controller wrapper instance.
	 */
	public static void init() {
		if(ControllerWrapperHolder.INSTANCE == null)
			ControllerWrapperHolder.INSTANCE = new ControllerWrapper();
	}

	@Override
	public void onSettingChange(Setting setting) {
		switch(setting) {
		case CONTROLLER_JOYSTICK_DEADZONE: {
			joystick_deadzone = ProgramSettings.getDouble(Setting.CONTROLLER_JOYSTICK_DEADZONE);
			break;
		}
		case CONTROLLER_TRIGGER_DEADZONE: {
			trigger_deadzone = ProgramSettings.getDouble(Setting.CONTROLLER_TRIGGER_DEADZONE);
			break;
		}
		}
	}

}
