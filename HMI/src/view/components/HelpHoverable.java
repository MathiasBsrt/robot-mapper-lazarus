package view.components;

/**
 * This interface is used to implement the feature "Help Hoverable".
 * This feature let implemented objects to be hovered by the user's cursor,
 * while pressing the ALT key, and display a little description of the object.
 * 
 * For example, the {@link BatteryIcon} implements this interface,
 * so when the user hover, while pressing the ALT key, the battery icon
 * on the interface, a little box will show up a display the description
 * of the battery icon.
 * 
 * In addition, for the class {@link ImageButton}, the {@link #setHelpMessage(String)}
 * has been implemented too, because an {@link ImageButton} can be used as
 * the forward button, the background button or the settings button.
 * So we need a way to set the help message depending on the selected instance.
 * 
 * @author TRAFNY Theo - UPSSITECH
 *
 */
public interface HelpHoverable {
	
	/**
	 * Set the help message to be display.
	 * @param message
	 */
	public void setHelpMessage(String message);
	
	/**
	 * Get the help message to be display.
	 * @return
	 */
	public String getHelpMessage();

}
