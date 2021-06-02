package view;


import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowStateListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import model.controller.ControllerWrapper;
import model.settings.ProgramSettings;
import model.theme.ThemeManager;
import view.components.HelpHoverable;
import view.components.HelpPopup;
import view.components.titlebar.TitleBar;
import view.splashscreen.LazarusSplashScreen;

/**
 * Defines the main window
 * @author TRAFNY Theo - UPSSITECH
 *
 */
public class AppFrame extends JFrame implements WindowFocusListener,
												WindowStateListener,
												KeyListener,
												MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -912989417505291367L;


	/**
	 * The title of the window.
	 */
	public static final String TITLE = "IVAC Projet";
	/**
	 * The size of the window.
	 */
	public static final Dimension WINDOW_SIZE = new Dimension(1200, 700);

	/**
	 * Is the window currently being focused ?
	 */
	private static boolean hasFocus = false;

	// Some Java Swing components
	private JLayeredPane layeredPane;
	
	private JPanel wrapperPanel;
	private MainPanel mainPanel;
	
	
	// Defining a variable for the Splash Screen
	private LazarusSplashScreen splashScreen;

	// Some variables for the feature "HelpHoverable"
	private HelpPopup helpPopup;
	private boolean helpMenuAsked = false;
	
	/**
	 * 
	 */
	public AppFrame() {
		// Set the window frame less
		super.setUndecorated(true);
		super.setResizable(false);
		super.setTitle(TITLE);

		// Register some usefull listener
		super.addWindowFocusListener(this);
		super.addWindowStateListener(this);
		
		// Initialize the Splash Screen
		splashScreen = new LazarusSplashScreen();
		// Then open it
		splashScreen.open();

		
		// Load the Theme Manager
		ThemeManager.loadThemes();
		splashScreen.incLoading();
		
		// Load settings
		ProgramSettings.initSettings();
		splashScreen.incLoading();

		// Initialize the controller wrapper
		ControllerWrapper.init();
		splashScreen.incLoading();
		
		// Wait until the user select the serial port to connect to
		splashScreen.waitSplashEnd();
		
		// Get the serial port selected by the user
		String serialPortName = splashScreen.getSelectedSerialPortName();
		System.out.println("User select the serial port " + serialPortName + ".");

		// In this part, we are using a JLayeredPane for the feature called "HelpHoverable".
		// HelpHoverable is a feature used to have a simple description of options
		// available on the HMI, making this application user friendly, with
		// a little box when hovering the option.
		// So to display this help box over the main panel, we are using a
		// JLayeredPane with, at the bottom layer the main panel with the title bar,
		// end on the layer 1, we display the help box.
		
		// The next part is just some declarations to position
		// correctly all the components on the window, using
		// the layout manager GridBagConstraints.
		GridBagConstraints c = new GridBagConstraints();
		
		wrapperPanel = new JPanel();
		wrapperPanel.setLayout(new GridBagLayout());
		layeredPane = new JLayeredPane();
		layeredPane.setLayout(null);
		layeredPane.setPreferredSize(WINDOW_SIZE);
		layeredPane.setMinimumSize(WINDOW_SIZE);
		layeredPane.setMaximumSize(WINDOW_SIZE);
		
		mainPanel = new MainPanel(this);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.PAGE_START;
		c.gridheight = 1;
		c.gridwidth = 3;
		c.insets = new Insets(0,0,0,0);  //top padding
		c.ipady = 20;
		c.gridx = 0;
		c.gridy = 0;
		
		wrapperPanel.add(new TitleBar(this), c);
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridheight = 2;
		c.gridwidth = 3;
		c.ipady = 0;
		c.gridx = 0;
		c.gridy = 1;
		wrapperPanel.add(mainPanel, c);
		wrapperPanel.setBounds(0, 0, WINDOW_SIZE.width, WINDOW_SIZE.height);
		
		layeredPane.add(wrapperPanel, 0, 0);
		super.setContentPane(layeredPane);

		super.setPreferredSize(WINDOW_SIZE);
		super.setMaximumSize(WINDOW_SIZE);
		super.pack();
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		super.setVisible(true);
		helpPopup = new HelpPopup(mainPanel.robotMap, "Hi !");
		

		super.setFocusable(true);
		mainPanel.addKeyListener(this);
		mainPanel.addMouseMotionListener(this);
		mainPanel.requestFocus();
	}
	
	/**
	 * Set the robot map at the maximum size possible.
	 * @param fullscreen Is fullscreen
	 */
	public void setRobotMapFullscreen(boolean fullscreen) {
		mainPanel.setRobotMapFullscreen(fullscreen);
	}

	/**
	 * Is the window currently being focused ?
	 * @return
	 */
	public static boolean windowIsFocused() {
		return hasFocus;
	}

	@Override
	public void windowGainedFocus(WindowEvent e) {
		hasFocus = true;
	}

	@Override
	public void windowLostFocus(WindowEvent e) {
		hasFocus = false;
	}

	@Override
	public void windowStateChanged(WindowEvent e) {
		wrapperPanel.setBounds(0, 0, super.getSize().width, super.getSize().height);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		// Not used
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if(helpMenuAsked) {
			Point cursorLocation = e.getPoint();
			Point cursorLocationOnScreen = e.getLocationOnScreen();
			Component componentMaybeBox = getComponentAt(mainPanel, cursorLocation);

			// Here we need to go a the deepest point in the hierarchy,
			// so we are going through boxes and boxes, while translating
			// the "cursorLocation" point into the new component's space.

			// We set a limit in the number of boxes to go through.
			int iterationCount = 0;
			
			// This condition also check if the componentMaybeBox is null or not.
			while((componentMaybeBox instanceof Box) && iterationCount < 10) {
				Box box = (Box) componentMaybeBox;
				cursorLocation.x -= box.getX();
				cursorLocation.y -= box.getY();
				// We are not using the method Component#getComponentAt(Point)
				// because this method acts weirdly.
				componentMaybeBox = getComponentAt(box, cursorLocation);
				iterationCount++;
			}
			
			// If the component found is an HelpHoverable one
			if(componentMaybeBox instanceof HelpHoverable) {
				HelpHoverable helpHoverable = (HelpHoverable) componentMaybeBox;
				// We get its help message
				String helpMessage = helpHoverable.getHelpMessage();
				helpPopup.setMessage(helpMessage);
				
				// Compute some locations
				Rectangle localBounds = componentMaybeBox.getBounds();
				int x = componentMaybeBox.getLocationOnScreen().x - super.getLocationOnScreen().x;
				int y = componentMaybeBox.getLocationOnScreen().y - super.getLocationOnScreen().y;
				int pointerX = cursorLocationOnScreen.x - super.getLocationOnScreen().x;
				int pointerY = cursorLocationOnScreen.y - super.getLocationOnScreen().y;
				
				// And position it
				helpPopup.setPosition(x, y);
				helpPopup.setPointer(pointerX, pointerY);
				
				// Finally, we add the helpPopup if
				// the JLayeredPane does not contain it.
				if(!containerContains(layeredPane, helpPopup))
					layeredPane.add(helpPopup, 1, 0);
			} else {
				layeredPane.remove(helpPopup);
			}
			repaint();
		}
	}
	
	/**
	 * Check if a container contains a component.
	 * @param container The container probably containing the component.
	 * @param component The component
	 * @return boolean
	 */
	private boolean containerContains(Container container, Component component) {
		Component[] components = container.getComponents();
		 for (Component child : components) {
            if (child.equals(component)) {
                return true;
            }
        }
		return false;
	}
	
	/**
	 * A re-make of the method {@link Component#getComponentAt(int, int)}
	 * because this one acts weirdly.
	 * @param parent The container to look in.
	 * @param p The location to look at.
	 * @return The component found, can be null.
	 */
	private Component getComponentAt(Container parent, Point p) {
        for (Component child : parent.getComponents()) {
            if (child.getBounds().contains(p)) {
                return child;
            }
        }
        return null;
    }

	@Override
	public void keyTyped(KeyEvent e) {
		// Not used
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ALT)
			helpMenuAsked = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ALT) {
			layeredPane.remove(helpPopup);
			helpMenuAsked = false;
			repaint();
		}
	}

}
