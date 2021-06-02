package view.components;

import java.awt.Color;
<<<<<<< HEAD
=======
import java.awt.Container;
>>>>>>> HMI
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
<<<<<<< HEAD
import java.awt.image.BufferedImage;
=======
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
>>>>>>> HMI

import javax.swing.JPanel;
import javax.swing.Timer;

<<<<<<< HEAD
=======
import model.theme.ThemeManager;
import model.theme.ThemeValue;
import view.MainPanel;

/**
 * 
 * @author TRAFNY Theo - UPSSITECH 
 * Based on https://stackoverflow.com/a/52421323, with the following adds:
 *              - Texts (with some customizations: Fonts & Colors)
 *              - Animation on click
 *
 */

public class ToggleSwitch extends JPanel implements MouseMotionListener, HelpHoverable {

	// Is the switch activated or not
	private boolean activated = false;
	
	// Background color when the switch is not activated
	private Color switchColor = new Color(200, 200, 200);
	// Color of the moving slider
	private Color buttonColor = new Color(255, 255, 255);
	// Color of the borders
	private Color borderColor = new Color(50, 50, 50);
	// Background color when the switch if activated
	private Color activeSwitch = new Color(0, 125, 255);
	// Border radius
	private int borderRadius = 10;

	// Current 2D graphic context
	private Graphics2D g;
	// Image buffer containing the current looking of the switch
	private BufferedImage puffer;
	
	// Text font
	private Font textFont = new Font("Arial", Font.PLAIN, 30);
	// Text to print when the switch is on
	private String textActivated = "ON";
	// Text to print when the switch is off
	private String textDesactivated = "OFF";
	private String textUnactivated = "OFF";
	
	// Timer used to perform the animation
	private Timer animationTimer;
	// Boolean holder to know if the switch is performing the animation
	private boolean inAnimation = false;
	// Current position of the moving slider
	// (relative position x \in [0, getWidth()], y \in [0, getHeight()])
	private int x, y;

	public ToggleSwitch() {
		super();
	
	private int speed = 1;
	
	private String helpMessage;
	
	private List<ActionListener> listeners = new LinkedList<>();

	public ToggleSwitch() {
		super();
		addMouseMotionListener(this);
		setVisible(true);
		// Register a mouse listener to listen mouse entries
		// In this case a mouse click on its release.
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// Start the animation if it's not currently played
				if(inAnimation) return;
				animationTimer.start();
				
				// Toggle the switch
				activated = !activated;
				
				notifyAllActionListener();
				
				// Ask Swing to repaint the component (look at our implementation of paint(...))
				repaint();
			}
		});
		setCursor(new Cursor(Cursor.HAND_CURSOR));
		setBounds(0, 0, 41, 21);
		
		// Implement the animation of the switch.

		animationTimer = new Timer(1, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				inAnimation = true;
				// Move the slider to the right
				if(activated) {
					x += speed;
					if(x >= getWidth() / 2) {
						x = getWidth() / 2;
						inAnimation = false;
						animationTimer.stop();
					}
				// Move the slider to the left
				} else {

					x -= speed;
					if(x <= 0) {
						x = 0;
						inAnimation = false;
						animationTimer.stop();
					}
				}
				// Ask Swing to repaint the component
				repaint();
			}
		});
		animationTimer.setRepeats(true);
	}

	
	public void addActionListener(ActionListener listener) {
		listeners.add(listener);
	}
	
	private void notifyAllActionListener() {
		for(ActionListener listener : listeners) {
			listener.actionPerformed(null);
		}
	}

	@Override
	public void paint(Graphics gr) {
		if (g == null || puffer.getWidth() != getWidth() || puffer.getHeight() != getHeight()) {
			puffer = (BufferedImage) createImage(getWidth(), getHeight());
			g = (Graphics2D) puffer.getGraphics();
			RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHints(rh);
		}

		

		g.setColor(ThemeManager.get(ThemeValue.BACKGROUND));
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.setColor(activated ? activeSwitch : switchColor);
		g.fillRoundRect(0, 0, this.getWidth() - 1, getHeight() - 1, 5, borderRadius);
		g.setColor(borderColor);
		g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, borderRadius);
		
		g.setColor(buttonColor);
		g.fillRoundRect(x, y, (getWidth() - 1) / 2, (getHeight() - 1), borderRadius, borderRadius);
		g.setColor(borderColor);
		g.drawRoundRect(x, y, (getWidth() - 1) / 2, (getHeight() - 1), borderRadius, borderRadius);
		
		drawText();

		gr.drawImage(puffer, 0, 0, null);
	}

	/**
	 * Draw the texts over the button
	 */
	private void drawText() {
		Font previousFont = g.getFont();
		g.setFont(textFont);
		int textHeight = g.getFontMetrics().getHeight();
		Color currentColor = activated ? activeSwitch : switchColor;
		g.setColor(readableColor(currentColor));
		if (activated && textActivated != null) {
			int textWidth = g.getFontMetrics().stringWidth(textActivated);
			g.drawString(textActivated, getWidth() / 4 - textWidth / 2, getHeight() / 2 + textHeight / 3);

		} else if (!activated && textUnactivated != null) {
			int textWidth = g.getFontMetrics().stringWidth(textUnactivated);
			g.drawString(textUnactivated, (getWidth() / 2 + getWidth() / 4) - textWidth / 2,
			getHeight() / 2 + textHeight / 3);
		}
		g.setFont(previousFont);
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;

		if(activated)
			x = getWidth() - 6;
		else x = 0;
		repaint();
	}
	
	public void toggle() {
		if(inAnimation) return;
		activated = !activated;
		animationTimer.start();
	}

	public Color getSwitchColor() {
		return switchColor;
	}

	/**
	 * Unactivated Background Color of switch
	 */
	public void setSwitchColor(Color switchColor) {
		this.switchColor = switchColor;
	}

	public Color getButtonColor() {
		return buttonColor;
	}

	/**
	 * Switch-Button color
	 */
	public void setButtonColor(Color buttonColor) {
		this.buttonColor = buttonColor;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	/**
	 * Border-color of whole switch and switch-button
	 */
	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	public Color getActiveSwitch() {
		return activeSwitch;
	}

	public void setActiveSwitch(Color activeSwitch) {
		this.activeSwitch = activeSwitch;
	}

	/**
	 * @return the borderRadius
	 */
	public int getBorderRadius() {
		return borderRadius;
	}

	/**
	 * @param borderRadius the borderRadius to set
	 */
	public void setBorderRadius(int borderRadius) {
		this.borderRadius = borderRadius;
	}

	
	public void setActivateText(String textActivated) {
		this.textActivated = textActivated;
	}
	
	public void setUnactivateText(String textUnactivated) {
		this.textUnactivated = textUnactivated;
	}
	
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	public void setFontSize(float fontSize) {
		textFont = textFont.deriveFont(fontSize);
	}


	/**
	 * Computes the best color to use for a text depending of its background color.
	 * 
	 * @param backgroundColor background color of the text.
	 * @return The best color to use for a text depending of its background color. <br />
	 * 
	 *         See also: <br />
	 * 
	 *         {@link https://stackoverflow.com/questions/3116260/given-a-background-color-how-to-
	 *         get-a-foreground-color-that-makes-it-readable-o/3118280}
	 */
	private Color readableColor(Color backgroundColor) {
		// 2.2 represents the gamma value.

		double red = Math.pow((backgroundColor.getRed() / 255.0D), 2.2);
		double green = Math.pow((backgroundColor.getGreen() / 255.0D), 2.2);
		double blue = Math.pow((backgroundColor.getBlue() / 255.0D), 2.2);

		double threshold = 0.2126 * red + 0.7152 * green + 0.722 * blue;

		if (threshold > 0.5)
			return Color.BLACK;
		return Color.WHITE;
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

	@Override
	public void setHelpMessage(String message) {
		helpMessage = message;
	}

	@Override
	public String getHelpMessage() {
		return helpMessage;
	}

}