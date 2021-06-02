package model.settings;

import com.studiohartman.jamepad.ControllerButton;

/**
 * Describes all available settings of the application
 * @author TRAFNY Theo - UPSSITECH
 *
 */
public enum Setting {

	// INPUT SETTINGS
	// CONTROLLER
	CONTROLLER_JOYSTICK_DEADZONE("Joystick Deadzone", double.class),
	CONTROLLER_TRIGGER_DEADZONE("Trigger Deadzone", double.class),
	
	CONTROLLER_PERFORMANCE_MODE("Performance Mode", ControllerButton.class),
	CONTROLLER_AUTOMATIC_SCALE_MODE("Automatic Scale Mode", ControllerButton.class),
	CONTROLLER_AUTONOMOUS_CARTOGRAPHY("Autonomous Cartography", ControllerButton.class),
	CONTROLLER_SETTINGS("Open / Close Settings", ControllerButton.class),
	CONTROLLER_THEME("Switch Theme", ControllerButton.class);

	/**
	 * A little description of the setting
	 */
	private String label;
	
	/**
	 * The type of the value held by the setting
	 */
	private Class<?> classValue;

	/**
	 * Describes a setting
	 * @param label A little description of the setting
	 * @param classValue The type of the value held by the setting
	 */
	Setting(String label, Class<?> classValue) {
		this.label = label;
		this.classValue = classValue;
	}
	
	/**
	 * Returns the type of the value held by the setting
	 * @return A class type
	 */
	public Class<?> getAttributedClass() {
		return classValue;
	}
	
	/**
	 * Returns a little description of the setting
	 * @return The description
	 */
	public String getLabel() {
		return label;
	}

}
