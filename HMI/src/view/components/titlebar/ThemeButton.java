package view.components.titlebar;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

import model.theme.ThemeManager;
import model.theme.ThemeValue;
import model.theme.Themes;

/**
 * An implementation of a {@link TitleBarButton}.
 * Represents the theme button.
 * 
 * @author TRAFNY Theo - UPSSITECH
 *
 */
class ThemeButton extends TitleBarButton {
	
	/**
	 * The sun icon is used when the dark theme is used.
	 */
	private Image sunImage = new ImageIcon("res/sun.png").getImage();
	/**
	 * The moon icon is used when the light theme is used.
	 */
	private Image moonImage = new ImageIcon("res/moon.png").getImage();

	public ThemeButton() {
		// Set the button on the right of the title bar
		// at the left of the minimize button
		// with a size of 20.
		super(new Rectangle(-115, 5, 20, 20), 5, ThemeManager.get(ThemeValue.BACKGROUND_BUTTONS));
	}

	@Override
	public void draw(Graphics g, int width, int height) {
		g.setColor(color);
		Image image = sunImage;
		if(ThemeManager.getCurrentThemeType().equals(Themes.LIGHT_THEME)) {
			image = moonImage;
		}
		g.drawImage(image, width + bounds.x + padding,
				bounds.y - bounds.height / 4 + padding,
				bounds.width, bounds.height,
				null);
	}

}
