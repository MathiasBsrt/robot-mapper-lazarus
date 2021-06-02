package model.settings;

/**
 * A Runtime Exception, which is called when
 * something tries to modify / fetch a setting's value,
 * by this one has not been registered in the Program Setting Manager.
 * @see ProgramSettings
 * 
 * @author TRAFNY Theo - UPSSITECH
 *
 */
public class SettingNotRegisteredException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6416195013156939881L;

	/**
	 * The setting that is not registered.
	 */
	private Setting setting;

	public SettingNotRegisteredException(Setting setting) {
		super(String.format("No such setting '%s' has been registered.", setting));
		this.setting = setting;
	}

	/**
	 * The setting that is not registered
	 * @return The setting not registered
	 */
	public Setting getSetting() {
		return setting;
	}

}
