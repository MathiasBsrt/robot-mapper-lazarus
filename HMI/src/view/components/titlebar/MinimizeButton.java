package view.components.titlebar;

import java.awt.Graphics;
import java.awt.Rectangle;

import model.theme.ThemeManager;
import model.theme.ThemeValue;

/**
 * An implementation of a {@link TitleBarButton}.
 * Represents the minimize button.
 * 
 * @author TRAFNY Theo - UPSSITECH
 *
 */
class MinimizeButton extends TitleBarButton {

	public MinimizeButton() {
		// Set the button on the right of the title bar
		// at the left of the maximize button
		// with a size of 20.
		super(new Rectangle(-85, 5, 20, 20), 5, ThemeManager.get(ThemeValue.BACKGROUND_BUTTONS));
	}

	@Override
	public void draw(Graphics g, int width, int height) {
		g.setColor(color);
		g.drawLine(width + bounds.x + padding, bounds.y + bounds.height / 2 + padding,
				width + bounds.x + bounds.width - padding,
				bounds.y + bounds.height - padding);
	}

}
