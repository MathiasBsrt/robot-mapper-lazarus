package view.components.titlebar;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import model.BluetoothWrapper;
import model.theme.ThemeManager;
import model.theme.ThemeValue;
import model.theme.Themes;
import view.AppFrame;

/**
 * Represents the title bar.
 * This class handle mouse motions to be able to
 * be dragged and drag the window with it.
 * @author TRAFNY Theo - UPSSITECH
 *
 */
public class TitleBar extends JPanel implements MouseMotionListener, MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6473857282151769739L;

	/**
	 * Cursor used when the user hover a title bar button.
	 */
	private static final Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);
	/**
	 * Cursor used wgen the user is not hovering a title bar button.
	 */
	private static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);

	/**
	 * A reference to the main window, allowing the title bar
	 * to drag the window with it.
	 */
	private AppFrame frame;

	/**
	 * Is the title bar being dragged ?
	 */
	private boolean isDragging = false;
	/**
	 * The location where the user started the drag event.
	 * This is the x component.
	 */
	private int startX;
	/**
	 * The location where the user started the drag event.
	 * This is the y component.
	 */
	private int startY;
	
	/**
	 * Top left icon
	 */
	private Image icon = new ImageIcon("res/Nadine.png").getImage();
	
	/**
	 * Font used to print the title
	 */
	private Font titleFont = new Font("Monospaced", Font.BOLD, 24);

	/**
	 * List of the buttons
	 */
	private List<TitleBarButton> buttons = new ArrayList<>();
	
	public TitleBar(AppFrame frame) {
		this.frame = frame;
		// Register mouse listeners
		super.addMouseMotionListener(this);
		super.addMouseListener(this);

		// Register this component to the ThemeManager, to be updated
		ThemeManager.registerComponent(this, this::setBackground, ThemeValue.TITLE_BAR);
		super.setBackground(ThemeManager.get(ThemeValue.TITLE_BAR));
		
		// Creating and defining actions of all the buttons
		
		// Close button:
		//		Shape: Cross
		//		Action: Close the window and end the processus
		CloseButton closeButton = new CloseButton();
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				System.exit(0);
			}
		});
		
		// Maximize button:
		//		Shape: - Square
		//			   - Two squares
		//		Action: Maximize / Set to normal the size of the window
		MaximizeButton maximizeButton = new MaximizeButton();
		maximizeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(maximizeButton.state) {
					frame.setExtendedState(JFrame.NORMAL); 
					frame.setSize(AppFrame.WINDOW_SIZE); 
					frame.setRobotMapFullscreen(false);
				} else {
					frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
					frame.setRobotMapFullscreen(true);
				}
				frame.setVisible(true);
				frame.repaint();
				maximizeButton.color = ThemeManager.get(ThemeValue.BACKGROUND_BUTTONS);
				repaint();
			}
		});
		
		// Minimize button
		//		Shape: Line (like an underscore)
		//		Action: Iconify the window
		MinimizeButton minimizeButton = new MinimizeButton();
		minimizeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setState(JFrame.ICONIFIED);
				minimizeButton.color = ThemeManager.get(ThemeValue.BACKGROUND_BUTTONS);
				repaint();
			}
		});
		
		// Theme button
		//		Shape: - Sun
		//			   - Moon
		//		Action: Change the current theme used
		ThemeButton themeButton = new ThemeButton();
		themeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(ThemeManager.getCurrentThemeType().equals(Themes.LIGHT_THEME)) {
					ThemeManager.useTheme(Themes.DARK_THEME);
				} else {
					ThemeManager.useTheme(Themes.LIGHT_THEME);
				}
				repaint();
			}
		});
		
		// Add all the buttons
		buttons.add(closeButton);
		buttons.add(maximizeButton);
		buttons.add(minimizeButton);
		buttons.add(themeButton);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// Draw the icon
		g.drawImage(icon, 5, 5, 20, 20, frame);
		
		// Draw the title
		g.setColor(ThemeManager.get(ThemeValue.TEXT));
		g.setFont(titleFont);
		g.drawString(frame.getTitle() + " - Connected to " + BluetoothWrapper.getConnectedSerialPortName(), 30, 23);
		

		// Draw buttons
		drawButtons(g);
	}
	
	/**
	 * Draw all registered buttons
	 * @param g Current graphic context
	 */
	private void drawButtons(Graphics g) {
		for(TitleBarButton button : buttons) {
			button.draw(g, getWidth(), getHeight());
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (!isDragging)
			return;
		int x = e.getXOnScreen();
		int y = e.getYOnScreen();
		// Update window's current position
		frame.setLocation(x - startX, y - startY);
		e.consume();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// If the mouse moved and the cursor is over any button,
		// the current cursor will be set to an hand cursor, or
		// be set back to a normal one if the cursor is not over 
		// any button.
		Point loc = e.getPoint();
		boolean isHoverSomething = false;
		for(TitleBarButton button : buttons) {
			if(button.isHover(loc.x - getWidth(), loc.y)) {
				button.color = Color.RED;
				setCursor(HAND_CURSOR);
				isHoverSomething = true;
			} else {
				button.color = ThemeManager.get(ThemeValue.BACKGROUND_BUTTONS);
			}
		}
		if(!isHoverSomething)
			setCursor(DEFAULT_CURSOR);
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// Trigger the button's click when the mouse clicked over a button
		Point loc = e.getPoint();
		for(TitleBarButton button : buttons) {
			if(button.isHover(loc.x - getWidth(), loc.y)) {
				button.click();
				return;
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// This method is used to fetch the start position
		// of the drag event.
		// With this, we can compute relative positions to update this frame's position
		if (e.getButton() == MouseEvent.BUTTON1) {
			isDragging = true;
			startX = e.getX();
			startY = e.getY();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// If the mouse button is released,
		// we can set the related boolean at false.
		if (e.getButton() == MouseEvent.BUTTON1) {
			isDragging = false;
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

}
