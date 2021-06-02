package view.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import model.ImageUtils;
import model.theme.ThemeManager;
import model.theme.ThemeValue;

/**
 * 
 * @author TRAFNY Theo - UPSSITECH
 *
 */
public class ImageIndicator extends JPanel implements HelpHoverable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4719685831516637144L;
	
	private Image offImage;
	private Image onImage;
	
	private String helpMessage;
	
	private boolean indicatorState = false;
	
	public ImageIndicator(ImageIcon icon) {
        this.setOpaque(false);
        this.offImage = icon.getImage();

        Color offColor = ThemeManager.get(ThemeValue.TOGGLE_BUTTONS_OFF);
        Color maskColor = ThemeManager.get(ThemeValue.TOGGLE_BUTTONS_MASK);
        Color onColor = ThemeManager.get(ThemeValue.TOGGLE_BUTTONS_ON);
        this.offImage = ImageUtils.replaceColor(icon.getImage(), maskColor, offColor);
        this.onImage = ImageUtils.replaceColor(icon.getImage(), maskColor, onColor);
        
        ThemeManager.registerComponent(this, this::setBackground, ThemeValue.AUTONOMOUS_SWITCH_OFF);
        ThemeManager.registerComponent(this, this::setForeground, ThemeValue.TOGGLE_BUTTONS_ON);
	}
	
	public ImageIndicator(ImageIcon icon, Color maskColor, Color offColor, Color onColor) {
        this.setOpaque(false);
        this.offImage = ImageUtils.replaceColor(icon.getImage(), maskColor, offColor);
        this.onImage = ImageUtils.replaceColor(icon.getImage(), maskColor, onColor);
	}
	
	@Override
	public void paint(Graphics g) {
//	    g.clearRect(0, 0, getWidth(), getHeight());
		if(indicatorState)
			g.drawImage(onImage, 0, 0, this);
		else g.drawImage(offImage, 0, 0, this);
	}
	
	@Override
	public void setBackground(Color color) {
		if(onImage != null) {
	        Color maskColor = ThemeManager.get(ThemeValue.TOGGLE_BUTTONS_MASK);
	        onImage = ImageUtils.replaceColor(onImage, maskColor, color);
	        repaint();
		}
	}
	
	@Override
	public void setForeground(Color color) {
		if(offImage != null) {
	        Color maskColor = ThemeManager.get(ThemeValue.TOGGLE_BUTTONS_MASK);
	        offImage = ImageUtils.replaceColor(offImage, maskColor, color);
	        repaint();
		}
	}
	
	public boolean isActivated() {
		return indicatorState;
	}
	
	public void setActivated(boolean activated) {
		indicatorState = activated;
		repaint();
	}
	
	private void resizeImages(Dimension dimension) {
		onImage = onImage.getScaledInstance((int) dimension.getWidth(), (int) dimension.getHeight(), Image.SCALE_SMOOTH);
		offImage = offImage.getScaledInstance((int) dimension.getWidth(), (int) dimension.getHeight(), Image.SCALE_SMOOTH); 
		repaint();
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
	public String getHelpMessage() {
		return helpMessage + "\nState: " + (indicatorState ? "ON" : "OFF");
	}

	@Override
	public void setHelpMessage(String message) {
		helpMessage = message;
	}

}
