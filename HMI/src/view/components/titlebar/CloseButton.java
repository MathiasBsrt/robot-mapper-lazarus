package view.components.titlebar;

import java.awt.Graphics;
import java.awt.Rectangle;

import model.theme.ThemeManager;
import model.theme.ThemeValue;

/**
 * An implementation of a {@link TitleBarButton}.
 * Represents the close button.
 * 
 * @author TRAFNY Theo - UPSSITECH
 *
 */
class CloseButton extends TitleBarButton {

	public CloseButton() {
		// Set the button on the right of the title bar
		// with a size of 20.
		super(new Rectangle(-25, 5, 20, 20), 5, ThemeManager.get(ThemeValue.BACKGROUND_BUTTONS));
	}

	@Override
	public void draw(Graphics g, int width, int height) {
		g.setColor(color);
		g.drawLine(width + bounds.x + padding, bounds.y + padding,
				width + bounds.x + bounds.width - padding,
				bounds.y + bounds.height - padding);
		g.drawLine(width + bounds.x + bounds.width - padding,
				bounds.y + padding, width + bounds.x + padding,
				bounds.y + bounds.height - padding);
	}

}
