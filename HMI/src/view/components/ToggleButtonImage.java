package view.components;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;
import java.util.List;

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
public class ToggleButtonImage extends JButton implements ActionListener, MouseMotionListener, HelpHoverable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4719685831516637144L;
	
	private ImageIcon offIcon;
	private ImageIcon onIcon;
	
	private boolean buttonState = false;
	
	private List<ToggleButtonListener> listeners = new LinkedList<>();
	
	private String helpMessage;
	
	public ToggleButtonImage(ImageIcon icon) {
		super(icon);
		super.setBackground(new Color(0, 0, 0, 0));
		super.setCursor(new Cursor(Cursor.HAND_CURSOR));
		super.setBorderPainted(false);
		super.setFocusPainted(false);
		super.setContentAreaFilled(false);
        super.setOpaque(false);
        super.addActionListener(this);
        super.addMouseMotionListener(this);

        Color offColor = ThemeManager.get(ThemeValue.TOGGLE_BUTTONS_OFF);
        Color maskColor = ThemeManager.get(ThemeValue.TOGGLE_BUTTONS_MASK);
        Color onColor = ThemeManager.get(ThemeValue.TOGGLE_BUTTONS_ON);

        Image offImage = ImageUtils.replaceColor(icon.getImage(), maskColor, offColor);
        this.offIcon = new ImageIcon(offImage);
        Image onImage = ImageUtils.replaceColor(icon.getImage(), maskColor, onColor);
        this.onIcon = new ImageIcon(onImage);

        ThemeManager.registerComponent(this, this::setBackground, ThemeValue.AUTONOMOUS_SWITCH_OFF);
        ThemeManager.registerComponent(this, this::setForeground, ThemeValue.TOGGLE_BUTTONS_ON);
	}
	
	public void addToggleButtonListener(ToggleButtonListener listener) {
		listeners.add(listener);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		boolean needToBePrevented = false;
		for(ToggleButtonListener listener : listeners)
			needToBePrevented = needToBePrevented || !listener.onStateChange(!buttonState);
		if(needToBePrevented) return;
		buttonState = !buttonState;
		updateIcon();
	}
	
	@Override
	public void setBackground(Color color) {
		if(offIcon != null) {
	        Color maskColor = ThemeManager.get(ThemeValue.TOGGLE_BUTTONS_MASK);
	        Image offImage = ImageUtils.replaceColor(offIcon.getImage(), maskColor, color);
	        offIcon = new ImageIcon(offImage);
	        updateIcon();
		}
	}
	
	@Override
	public void setForeground(Color color) {
		if(onIcon != null) {
	        Color maskColor = ThemeManager.get(ThemeValue.TOGGLE_BUTTONS_MASK);
	        Image onImage = ImageUtils.replaceColor(onIcon.getImage(), maskColor, color);
	        onIcon = new ImageIcon(onImage);
	        updateIcon();
		}
	}
	
	public boolean isActivated() {
		return buttonState;
	}
	
	public void setActivated(boolean activated) {
		buttonState = activated;
		for(ToggleButtonListener listener : listeners)
			listener.onStateChange(buttonState);
		updateIcon();
	}
	
	private void updateIcon() {
		if(buttonState) super.setIcon(onIcon);
		else super.setIcon(offIcon);
	}
	
	private void resizeImages(Dimension dimension) {
		Image onImage = onIcon.getImage();
		onImage = onImage.getScaledInstance((int) dimension.getWidth(), (int) dimension.getHeight(), Image.SCALE_SMOOTH); 
		onIcon = new ImageIcon(onImage);
		
		Image offImage = offIcon.getImage();
		offImage = offImage.getScaledInstance((int) dimension.getWidth(), (int) dimension.getHeight(), Image.SCALE_SMOOTH); 
		offIcon = new ImageIcon(offImage);
		
		updateIcon();
	}
	
	@Override
	public void setPreferredSize(Dimension preferredSize) {
		super.setPreferredSize(preferredSize);
		resizeImages(preferredSize);
	}
	
	@Override
	public void setMinimumSize(Dimension minimumSize) {
		super.setMinimumSize(minimumSize);
		resizeImages(minimumSize);
	}
	
	@Override
	public void setMaximumSize(Dimension maximumSize) {
		super.setMaximumSize(maximumSize);
		resizeImages(maximumSize);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
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

	@Override
	public void setHelpMessage(String message) {
		helpMessage = message;
	}

	@Override
	public String getHelpMessage() {
		return helpMessage;
	}

}
