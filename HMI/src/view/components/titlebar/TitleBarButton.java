package view.components.titlebar;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

/**
 * Describes a title bar button. An instance of this class
 * can be added to the {@link TitleBar} to be drawn and handled.
 * @author TRAFNY Theo - UPSSITECH
 *
 */
public abstract class TitleBarButton {
	
	/**
	 * The boundaries of the button, including its
	 * location and size.
	 */
	protected Rectangle bounds;
	/**
	 * The inner padding in its boundaries.
	 */
	protected int padding;
	/**
	 * The color of the button when it is not hover.
	 */
	protected Color color;
	
	/**
	 * Registered action listeners called when the button is pressed.
	 */
	private List<ActionListener> listeners = new LinkedList<>();
	
	public TitleBarButton(Rectangle bounds, int padding, Color color) {
		this.bounds = bounds;
		this.padding = padding;
		this.color = color;
	}
	
	/**
	 * Ask the button to be drawn with the given graphic context.
	 * @param g The graphic context.
	 * @param width The width of the title bar.
	 * @param height The height of the title bar.
	 */
	public abstract void draw(Graphics g, int width, int height);
	
	/**
	 * Called when the button has been clicked. It will call
	 * all registered action listeners.
	 */
	public void click() {
		for(ActionListener listener : listeners) {
			listener.actionPerformed(null);
		}
	}
	
	/**
	 * Register an action listener to listen press events.
	 * @param listener
	 */
	public void addActionListener(ActionListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Check if the location describes with x and y is in
	 * the boundaries of the button
	 * @param x x component of the location
	 * @param y y component of the location
	 * @return is the location hover the button.
	 */
	public boolean isHover(int x, int y) {
		return bounds.contains(x, y);
	}
	
}
