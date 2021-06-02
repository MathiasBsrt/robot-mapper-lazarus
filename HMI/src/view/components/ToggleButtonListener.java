package view.components;

public interface ToggleButtonListener {

	/**
	 * Return false if you want to prevent the action event.
	 * @param activated
	 * @return
	 */
	public boolean onStateChange(boolean activated);
	
}
