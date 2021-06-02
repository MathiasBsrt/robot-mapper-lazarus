package view.components.titlebar;

import java.awt.Graphics;
import java.awt.Rectangle;

import model.theme.ThemeManager;
import model.theme.ThemeValue;

/**
 * An implementation of a {@link TitleBarButton}.
 * Represents the maximize button.
 * 
 * @author TRAFNY Theo - UPSSITECH
 *
 */
class MaximizeButton extends TitleBarButton {
	
	// This variable means that the Maximize button
	// needs to maximize or to set the defaut size.
	protected boolean state = false;
	
	public MaximizeButton() {
		// Set the button on the right of the title bar
		// at the leftof the close button
		// with a size of 20.
		super(new Rectangle(-55, 5, 20, 20), 5, ThemeManager.get(ThemeValue.BACKGROUND_BUTTONS));
	}
	
	@Override
	public void click() {
		super.click();
		state = !state;
	}

	@Override
	public void draw(Graphics g, int width, int height) {
		g.setColor(color);
		if(state) drawMinimize(g, width, height);
		else drawMaximize(g, width, height);
	}
	
	private void drawMaximize(Graphics g, int width, int height) {
		g.drawRect(width + bounds.x + padding,
				bounds.y + padding,
				bounds.width - 2 * padding,
				bounds.height - 2 * padding);
	}
	
	private void drawMinimize(Graphics g, int width, int height) {
		g.drawRect(width + bounds.x + padding + 2,
				bounds.y + padding,
				bounds.width - 2 * padding - 3,
				bounds.height - 2 * padding - 3);
		g.drawRect(width + bounds.x + padding - 2,
				bounds.y + padding + 4,
				bounds.width - 2 * padding - 3,
				bounds.height - 2 * padding - 3);
	}

}
