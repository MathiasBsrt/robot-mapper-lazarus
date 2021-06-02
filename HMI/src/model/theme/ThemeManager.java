package model.theme;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A Manager used to handle all initialized themes.
 * @see DarkTheme
 * @see LightTheme
 * @see Theme
 * @see Themes
 * @see ThemeValue
 * 
 * @author TRAFNY Theo - UPSSITECH
 *
 */
public class ThemeManager {

	/**
	 * All initialized themes
	 */
	private static Map<Themes, Theme> themes = new EnumMap<>(Themes.class);
	
	/**
	 * The current theme being used.
	 */
	private static Themes currentTheme = Themes.LIGHT_THEME;
	
	/**
	 * All components registered to be updated with the binded configuration
	 * @see ComponentConfiguration
	 */
	private static Map<Component, List<ComponentConfiguration>> updatableComponents = new HashMap<>();
	
	/**
	 * Initalized all available themes
	 */
	public static void loadThemes() {
		themes.put(Themes.LIGHT_THEME, new LightTheme());
		themes.put(Themes.DARK_THEME, new DarkTheme());
	}
	
	/**
	 * Set the current theme to the one passed.
	 * @param theme The new theme
	 */
	public static void useTheme(Themes theme) {
		currentTheme = theme;
		updateComponents();
	}
	
	/**
	 * Return the current theme being used.
	 * @return The current theme being used.
	 */
	public static Themes getCurrentThemeType() {
		return currentTheme;
	}
	
	/**
	 * Return the current theme implementation being used.
	 * @return The current theme implementation being used.
	 */
	public static Theme getCurrentTheme() {
		return themes.get(currentTheme);
	}
	
	/**
	 * Fetch the color bind to the theme value needed.
	 * @param value The theme value
	 * @return The color bind to the theme value
	 */
	public static Color get(ThemeValue value) {
		return getCurrentTheme().get(value);
	}
	
	/**
	 * Register a component to be updated when the theme will changed.
	 * @param component The component
	 * @param method The method (with only one parameter: a color) used to update the component
	 * @param valueToFetch The theme value used to update the component.
	 */
	public static void registerComponent(Component component, Consumer<Color> method, ThemeValue valueToFetch) {
		if(updatableComponents.containsKey(component)) {
			List<ComponentConfiguration> configurations = updatableComponents.get(component);
			configurations.add(new ComponentConfiguration(method, valueToFetch));
		} else {
			List<ComponentConfiguration> configurations = new ArrayList<>();
			configurations.add(new ComponentConfiguration(method, valueToFetch));
			updatableComponents.put(component, configurations);
		}
	}
	
	/**
	 * Update every registered component
	 */
	private static void updateComponents() {
		Set<Component> components = updatableComponents.keySet();
		for(Component component : components) {
			List<ComponentConfiguration> configurations = updatableComponents.get(component);
			for(ComponentConfiguration configuration : configurations) {
				updateComponent(component, configuration);
			}
		}
	}
	
	/**
	 * Update the specified component
	 * @param component The component to update
	 * @param configuration The configuration used to update the component
	 */
	private static void updateComponent(Component component, ComponentConfiguration configuration) {
//		// DANGER ZONE HERE - USING REFERENCES TO METHOD /!\
		Consumer<Color> method = configuration.method;
		Color color = get(configuration.value);
		method.accept(color);
	}
	
	/**
	 * Describes a configuration (a pair of a method and theme value)
	 * used to update a component when the user switch theme.
	 * The method needs a be a method with:
	 * 		- A void return type and ;
	 * 		- A color as the only one parameter ;
	 * @author TRAFNY Theo - UPSSITECH
	 *
	 */
	private static class ComponentConfiguration {
		
		/**
		 * The method called when the used change the current theme.
		 * For exemple: {@link Component#setBackground(Color)}
		 */
		private Consumer<Color> method;
		/**
		 * The theme value used to call the method above.
		 */
		private ThemeValue value;
		
		public ComponentConfiguration(Consumer<Color> method, ThemeValue value) {
			this.method = method;
			this.value = value;
		}
		
	}
	
}
