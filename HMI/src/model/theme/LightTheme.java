package model.theme;

import static model.theme.ThemeValue.*;

import java.awt.Color;

/**
 * An implementation of a Theme, describing
 * a light theme.
 * @see Theme
 * @see ThemeManager
 * @see Themes
 * @see ThemeValue
 * @see DarkTheme
 * 
 * @author TRAFNY Theo - UPSSITECH
 *
 */
public class LightTheme extends Theme {

	@Override
	protected void initValues() {
		register(BACKGROUND, new Color(252, 248, 236));
		register(BACKGROUND_JOYSTICK, new Color(208, 232, 242));
		register(JOYSTICK, new Color(121, 163, 177));
		register(BACKGROUND_ROBOT_MAP, new Color(252, 248, 236));
		
		register(TOGGLE_BUTTONS_ON, new Color(208, 232, 242));
		register(TOGGLE_BUTTONS_OFF, new Color(69, 98, 104));
		register(TOGGLE_BUTTONS_MASK, new Color(128, 128, 128));
		register(BLUETOOTH_INDICATOR_ON, new Color(10, 61, 145).brighter());
		
		register(AUTONOMOUS_SWITCH, new Color(252, 248, 236));
		register(AUTONOMOUS_SWITCH_ON, Color.GREEN.darker());
		register(AUTONOMOUS_SWITCH_OFF, Color.RED.darker());
		
		register(BACKGROUND_BUTTONS, new Color(69, 98, 104));
		register(TEXT, new Color(69, 98, 104));
		
		register(BACKGROUND_BATTERY, new Color(208, 232, 242));
		register(BATTERY_FULL, Color.GREEN);
		register(BATTERY_LOW, Color.ORANGE);
		register(BATTERY_EMPTY, Color.RED);
		
		register(ROBOT_COLOR, new Color(121, 163, 177));
		
		register(TITLE_BAR, new Color(208, 232, 242));
	}

}
