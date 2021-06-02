package view.components;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import model.ImageUtils;
import model.theme.ThemeManager;
import model.theme.ThemeValue;
import view.MainPanel;

/**
 * 
 * @author TRAFNY Theo - UPSSITECH
 *
 */
public class ImageButton extends JButton implements HelpHoverable, MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4719685831516637144L;
	
	private String hoverMessage;

	public ImageButton(String fileName) {
		super();
		ImageIcon icon = new ImageIcon(fileName);
		Image newImage = ImageUtils.replaceColor(icon.getImage(), Color.BLACK,
				ThemeManager.get(ThemeValue.BACKGROUND_BUTTONS));
		ImageIcon newIcon = new ImageIcon(newImage);
		super.setIcon(newIcon);
		super.setBackground(new Color(0, 0, 0, 0));
		super.setCursor(new Cursor(Cursor.HAND_CURSOR));
		super.setBorderPainted(false);
		super.setFocusPainted(false);
		super.setContentAreaFilled(false);
		super.setOpaque(false);
		super.addMouseMotionListener(this);
	}

	@Override
	public void setHelpMessage(String message) {
		hoverMessage = message;
	}

	@Override
	public String getHelpMessage() {
		return hoverMessage;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// Not used
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// Dispatch event to the main panel for the HelpHoverable method
		Container container = super.getParent();
		while(!(container instanceof MainPanel)) {
			container = container.getParent();
		}
		
		int x = e.getLocationOnScreen().x - container.getLocationOnScreen().x;
		int y = e.getLocationOnScreen().y - container.getLocationOnScreen().y;
		
		MouseEvent me = new MouseEvent(this, // which
			    MouseEvent.MOUSE_MOVED, // what
			    System.currentTimeMillis(), // when
			    0, // no modifiers
			    x, y, // where: at (10, 10}
			    1, // only 1 click 
			    false); // not a popup trigger
		
		container.dispatchEvent(me);
	}

}
