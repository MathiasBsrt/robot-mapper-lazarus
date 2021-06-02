package model.settings;

/**
 * An event listener interface used to listen
 * when a setting has been changed by something.
 * @author TRAFNY Theo - UPSSITECH
 *
 */
public interface SettingChangeListener {

	/**
	 * Called when the setting has been modified.
	 * @param setting The setting updated
	 */
	public void onSettingChange(Setting setting);

}
