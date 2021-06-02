package view.components.controls;

import javax.swing.JSlider;

import model.theme.ThemeManager;
import model.theme.ThemeValue;

/**
 * A child of {@link JSlider}, it represents the speed slider
 * next to the joystick, used to set the robot's speed.
 * @author TRAFNY Theo - UPSSITECH
 *
 */
public class SpeedSlider extends JSlider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8899733518597804466L;
	
	private static final int DEFAULT_MEAN_SPEED = 25;
	
	private int meanSpeed = DEFAULT_MEAN_SPEED;
	
	private boolean performanceModeOn = false;
	
	public SpeedSlider() {
		super(JSlider.VERTICAL, 0, 100, DEFAULT_MEAN_SPEED);
		ThemeManager.registerComponent(this, this::setBackground, ThemeValue.BACKGROUND);
		ThemeManager.registerComponent(this, this::setForeground, ThemeValue.TEXT);
		super.setBackground(ThemeManager.get(ThemeValue.BACKGROUND));
		super.setForeground(ThemeManager.get(ThemeValue.TEXT));
	}
	
	public void setMean() {
		setValue(meanSpeed);
	}
	
	public void incSpeed() {
		setValue(getValue() + 1);
		meanSpeed = getValue();
	}
	
	public void decSpeed() {
		setValue(getValue() - 1);
		meanSpeed = getValue();
	}
	
	public void togglePerformanceMode() {
		performanceModeOn = !performanceModeOn;
		if(performanceModeOn) performanceModeOn();
		else performanceModeOff();
	}
	
	public void performanceModeOn() {
		performanceModeOn = true;
		setMaximum(150);
		setValue(getMaximum());
		meanSpeed = getValue();
	}

	public void performanceModeOff() {
		performanceModeOn = false;
		setMaximum(100);
		setValue(50);
		meanSpeed = getValue();
	}
	
	public boolean performanceState() {
		return performanceModeOn;
	}
	
}
