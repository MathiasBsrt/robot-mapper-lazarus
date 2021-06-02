package model.theme;

import static model.theme.ThemeValue.*;

import java.awt.Color;

/**
 * An implementation of a Theme, describing
 * a dark theme.
 * @see Theme
 * @see ThemeManager
 * @see Themes
 * @see ThemeValue
 * @see LightTheme
 * 
 * @author TRAFNY Theo - UPSSITECH
 *
 */
public class DarkTheme extends Theme {

	@Override
	protected void initValues() {
		register(BACKGROUND, new Color(29, 45, 80));
		register(BACKGROUND_JOYSTICK, new Color(19, 59, 92));
		register(JOYSTICK, new Color(252, 218, 183).darker());
		register(BACKGROUND_ROBOT_MAP, new Color(19, 59, 92).brighter());
		
		register(TOGGLE_BUTTONS_ON, new Color(252, 218, 183).darker());
		register(TOGGLE_BUTTONS_OFF, new Color(31, 95, 116));
		register(TOGGLE_BUTTONS_MASK, new Color(128, 128, 128));
		register(BLUETOOTH_INDICATOR_ON, new Color(10, 61, 145).brighter());
		
		register(AUTONOMOUS_SWITCH, new Color(252, 218, 183).darker());
		register(AUTONOMOUS_SWITCH_ON, Color.GREEN.darker());
		register(AUTONOMOUS_SWITCH_OFF, Color.RED.darker());
		
		register(BACKGROUND_BUTTONS, new Color(252, 218, 183).darker());
		register(TEXT, new Color(252, 218, 183));
		
		register(BACKGROUND_BATTERY, new Color(29, 45, 80).brighter());
		register(BATTERY_FULL, Color.GREEN.darker());
		register(BATTERY_LOW, Color.ORANGE.darker());
		register(BATTERY_EMPTY, Color.RED.darker());
		
		register(ROBOT_COLOR, new Color(252, 218, 183));
		
		register(TITLE_BAR, new Color(29, 45, 80).brighter().brighter());
	}

}
