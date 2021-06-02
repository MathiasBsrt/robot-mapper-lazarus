package model.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.studiohartman.jamepad.ControllerButton;

/**
 * We have implemented a way to add settings to the program.
 * This class handle all the values registered as a setting.
 * @author TRAFNY Theo - UPSSITECH
 *
 */
public class ProgramSettings {

	/**
	 * Handle all settings of integer values.
	 */
	private static HashMap<Setting, Integer> settingsInteger = new HashMap<>();
	/**
	 * Handle all settings of double values.
	 */
	private static HashMap<Setting, Double> settingsDouble = new HashMap<>();
	/**
	 * Handle all settings of string values.
	 */
	private static HashMap<Setting, String> settingsString = new HashMap<>();
	/**
	 * Handle all settings of Enum values.
	 */
	private static HashMap<Setting, Enum<?>> settingsEnums = new HashMap<>();

	/**
	 * Handle all setting change listeners related to a specific setting.
	 */
	private static HashMap<Setting, List<SettingChangeListener>> settingsListeners = new HashMap<>();

	/**
	 * Register an integer value to the specified setting.
	 * @param setting The setting to set the value to.
	 * @param value The value
	 */
	public static void register(Setting setting, int value) {
		if (settingsInteger.containsKey(setting)) {
			settingsInteger.put(setting, value);
			List<SettingChangeListener> listeners = settingsListeners.get(setting);
			for (SettingChangeListener listener : listeners)
				listener.onSettingChange(setting);
		} else {
			settingsInteger.put(setting, value);
			settingsListeners.put(setting, new ArrayList<SettingChangeListener>());
		}
	}
	
	/**
	 * Register a double value to the specified setting.
	 * @param setting The setting to set the value to.
	 * @param value The value
	 */
	public static void register(Setting setting, double value) {
		if (settingsDouble.containsKey(setting)) {
			settingsDouble.put(setting, value);
			List<SettingChangeListener> listeners = settingsListeners.get(setting);
			for (SettingChangeListener listener : listeners)
				listener.onSettingChange(setting);
		} else {
			settingsDouble.put(setting, value);
			settingsListeners.put(setting, new ArrayList<SettingChangeListener>());
		}
	}

	/**
	 * Register a string value to the specified setting.
	 * @param setting The setting to set the value to.
	 * @param value The value
	 */
	public static void register(Setting setting, String value) {
		if (settingsString.containsKey(setting)) {
			settingsString.put(setting, value);
			List<SettingChangeListener> listeners = settingsListeners.get(setting);
			for (SettingChangeListener listener : listeners)
				listener.onSettingChange(setting);
		} else {
			settingsString.put(setting, value);
			settingsListeners.put(setting, new ArrayList<SettingChangeListener>());
		}
	}

	/**
	 * Register an Enum value to the specified setting.
	 * @param setting The setting to set the value to.
	 * @param value The value
	 */
	public static void register(Setting setting, Enum<?> value) {
		if (settingsEnums.containsKey(setting)) {
			settingsEnums.put(setting, value);
			List<SettingChangeListener> listeners = settingsListeners.get(setting);
			for (SettingChangeListener listener : listeners)
				listener.onSettingChange(setting);
		} else {
			settingsEnums.put(setting, value);
			settingsListeners.put(setting, new ArrayList<SettingChangeListener>());
		}
	}

	/**
	 * Get the value of a setting.
	 * This method can be used has a "universal" one and for debugging.
	 * @see ProgramSettings#getInt(Setting)
	 * @see ProgramSettings#getDouble(Setting)
	 * @see ProgramSettings#getString(Setting)
	 * @see ProgramSettings#getEnum(Setting)
	 * 
	 * @param setting The setting that hold the value
	 * @return An object.
	 */
	public static Object get(Setting setting) {
		if (settingsInteger.containsKey(setting))
			return settingsInteger.get(setting);
		if (settingsDouble.containsKey(setting))
			return settingsDouble.get(setting);
		if (settingsEnums.containsKey(setting))
			return settingsEnums.get(setting);
		if (settingsString.containsKey(setting))
			return settingsString.get(setting);
		throw new SettingNotRegisteredException(setting);
	}

	/**
	 * Get a int value of a setting.
	 * @see ProgramSettings#get(Setting)
	 * @see ProgramSettings#getDouble(Setting)
	 * @see ProgramSettings#getString(Setting)
	 * @see ProgramSettings#getEnum(Setting)
	 * 
	 * 
	 * @param setting The setting that hold the value
	 * @return An object.
	 */
	public static int getInt(Setting setting) {
		if (settingsInteger.containsKey(setting))
			return settingsInteger.get(setting);
		throw new SettingNotRegisteredException(setting);
	}

	/**
	 * Get a double value of a setting.
	 * @see ProgramSettings#get(Setting)
	 * @see ProgramSettings#getInt(Setting)
	 * @see ProgramSettings#getString(Setting)
	 * @see ProgramSettings#getEnum(Setting)
	 * 
	 * 
	 * @param setting The setting that hold the value
	 * @return An object.
	 */
	public static double getDouble(Setting setting) {
		if (settingsDouble.containsKey(setting))
			return settingsDouble.get(setting);
		throw new SettingNotRegisteredException(setting);
	}

	/**
	 * Get an Enum value of a setting.
	 * @see ProgramSettings#get(Setting)
	 * @see ProgramSettings#getDouble(Setting)
	 * @see ProgramSettings#getString(Setting)
	 * @see ProgramSettings#getInt(Setting)
	 * 
	 * @param setting The setting that hold the value
	 * @return An object.
	 */
	public static Enum<?> getEnum(Setting setting) {
		if (settingsEnums.containsKey(setting))
			return settingsEnums.get(setting);
		throw new SettingNotRegisteredException(setting);
	}

	/**
	 * Get a String value of a setting.
	 * @see ProgramSettings#get(Setting)
	 * @see ProgramSettings#getDouble(Setting)
	 * @see ProgramSettings#getInt(Setting)
	 * @see ProgramSettings#getEnum(Setting)
	 * 
	 * 
	 * @param setting The setting that hold the value
	 * @return An object.
	 */
	public static String getString(Setting setting) {
		if (settingsString.containsKey(setting))
			return settingsString.get(setting);
		throw new SettingNotRegisteredException(setting);
	}

	/**
	 * Register a setting change listener for the specified settings.
	 * @param listener The registered listener.
	 * @param settings Registered settings.
	 */
	public static void listen(SettingChangeListener listener, Setting... settings) {
		for (Setting setting : settings) {
			if (settingsListeners.containsKey(setting)) {
				settingsListeners.get(setting).add(listener);
			}
		}
	}

	/**
	 * Check if there is already a value bind to a setting.
	 * @param value The value to search.
	 * @return boolean
	 */
	public static boolean alreadyExists(Object value) {
		return settingsInteger.containsValue(value) || settingsDouble.containsValue(value)
				|| settingsEnums.containsValue(value) || settingsString.containsValue(value);
	}

	/**
	 * Get the setting bind to the given value.
	 * @param value The value bind to the setting searched.
	 * @return The setting bind to the given value.
	 */
	public static Setting getSettingFromValue(Object value) {
		if (settingsInteger.containsValue(value)) {
			for (Entry<Setting, Integer> entry : settingsInteger.entrySet()) {
				if (entry.getValue().equals(value))
					return entry.getKey();
			}
		} else if (settingsDouble.containsValue(value)) {
			for (Entry<Setting, Double> entry : settingsDouble.entrySet()) {
				if (entry.getValue().equals(value))
					return entry.getKey();
			}
		} else if(settingsEnums.containsValue(value)) {
			for(Entry<Setting, Enum<?>> entry : settingsEnums.entrySet()) {
				if(entry.getValue().equals(value)) return entry.getKey();
			}
		} else if(settingsString.containsValue(value)) {
			for(Entry<Setting, String> entry : settingsString.entrySet()) {
				if(entry.getValue().equals(value)) return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * Initialize default values for each settings.
	 */
	public static void initSettings() {
		register(Setting.CONTROLLER_JOYSTICK_DEADZONE, 0.3);
		register(Setting.CONTROLLER_TRIGGER_DEADZONE, 0.3);
		register(Setting.CONTROLLER_PERFORMANCE_MODE, ControllerButton.Y);
		register(Setting.CONTROLLER_AUTOMATIC_SCALE_MODE, ControllerButton.B);
		register(Setting.CONTROLLER_AUTONOMOUS_CARTOGRAPHY, ControllerButton.X);
		register(Setting.CONTROLLER_SETTINGS, ControllerButton.START);
		register(Setting.CONTROLLER_THEME, ControllerButton.DPAD_LEFT);
		
	}

}
