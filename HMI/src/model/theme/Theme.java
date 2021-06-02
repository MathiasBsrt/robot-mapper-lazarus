package model.theme;

import java.awt.Color;
import java.util.EnumMap;
import java.util.Map;

/**
 * Describes a Theme
 * @see DarkTheme
 * @see LightTheme
 * @see ThemeManager
 * @see Themes
 * @see ThemeValue
 * 
 * @author TRAFNY Theo - UPSSITECH
 *
 */
public abstract class Theme {
	
	/**
	 * The color mask used to change the color of some images and icons.
	 */
	private static final Color COLOR_MASK = new Color(128, 128, 128);
	
	/**
	 * All colors bind to a theme value.
	 * 
	 * @see ThemeValue
	 */
	private Map<ThemeValue, Color> themeValues = new EnumMap<>(ThemeValue.class);
	
	public Theme() {
		initValues();
	}
	
	/**
	 * Bind a value to a theme Value
	 * @see ThemeValue
	 * 
	 * @param value The theme value
	 * @param color The color bind to the theme value
	 */
	protected void register(ThemeValue value, Color color) {
		themeValues.put(value, color);
	}
	
	/**
	 * Bind all needed values to themes values.
	 * It needs to be implemented by all children of this class.
	 */
	protected abstract void initValues();
	
	/**
	 * Fetch the color bind to the theme value passed.
	 * @param value The theme value
	 * @return The color bind to the theme value
	 */
	public Color get(ThemeValue value) {
		return themeValues.getOrDefault(value, Color.WHITE);
	}
	
}
