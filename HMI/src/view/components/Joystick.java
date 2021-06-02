package view.components;

import java.awt.Color;
<<<<<<< HEAD
=======
import java.awt.Container;
>>>>>>> HMI
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
<<<<<<< HEAD
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
=======
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
>>>>>>> HMI
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
<<<<<<< HEAD
=======
import javax.swing.Timer;

import bluetooth.CommunicatorV2;
import commande.CommandePower;
import commande.CommandeStop;
import commande.CommandeTurn;
import jssc.SerialPortException;
import model.BluetoothWrapper;
import model.Vector;
import model.theme.ThemeManager;
import model.theme.ThemeValue;
import view.MainPanel;
import view.components.controls.JoystickListener;
import view.components.controls.MouvementInputs;
import view.components.controls.SpeedSlider;

/**
 * 
 * @author TRAFNY Theo - UPSSITECH 
 *
 */

public class Joystick extends JPanel implements MouvementInputs, MouseMotionListener, HelpHoverable {


	// Current 2D graphic context
	private Graphics2D g;
	// Image buffer containing the current looking of the switch
	private BufferedImage puffer;
	
	// Is the user dragging the joystick
	private boolean mouseDragging = false;
	// Current location of the mouse
	private int mouseX, mouseY;
	
	private Color fillColor = Color.BLACK;
	private int padding = 100;
	private int halfPadding = padding / 2;
	
	private List<JoystickListener> joystickListeners = new LinkedList<>();
	

	public Joystick() {
		super();
	private Timer joystickNotifyTimer;
	
	private SpeedSlider speedSlider;

	public Joystick(SpeedSlider speedSlider) {
		super();
		this.speedSlider = speedSlider;
		addMouseMotionListener(this);
		setVisible(true);
		// Register a mouse listener to listen mouse entries
		// In this case a mouse click on its release.
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				mouseDragging = false;

				joystickNotifyTimer.stop();
				BluetoothWrapper.sendData(new CommandeStop());
				repaint();
			}
		});

		// In this case when the mouse is dragging something while hoverring the component.
		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				mouseDragging = true;

				joystickNotifyTimer.start();
				
				// Get the cursor location
				mouseX = e.getX();
				mouseY = e.getY();
				
				// Bounds the cursor location
				int size = (Math.min(getWidth(), getHeight()) - halfPadding - 1) / 2;
				float x = mouseX - getWidth() / 2;
				float y = mouseY - getHeight() / 2;
				float distance = (float) Math.hypot(x, y);
				
				if(distance > size) {
					float magnitude = (float) Math.sqrt(x * x + y * y);
					mouseX = (int) ((x / magnitude) * size) + getWidth() / 2;
					mouseY = (int) ((y / magnitude) * size) + getHeight() / 2;
				}


				sendConfigurationToRobot();
				
				repaint();
			}
		});
		
		joystickNotifyTimer = new Timer(1, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				notifyAllJoystickListeners();
			}
		});
		joystickNotifyTimer.setRepeats(true);
		
		setCursor(new Cursor(Cursor.MOVE_CURSOR));
		setBounds(0, 0, 41, 21);
	}

	@Override

	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		if (g == null || puffer.getWidth() != getWidth() || puffer.getHeight() != getHeight()) {
			puffer = (BufferedImage) createImage(getWidth(), getHeight());
			g = (Graphics2D) puffer.getGraphics();
			RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHints(rh);
		}
		
		g.clearRect(0, 0, getWidth(), getHeight());

		g.setColor(ThemeManager.get(ThemeValue.BACKGROUND));
		g.fillRect(0, 0, getWidth(), getHeight());
		
		// DEBUG
//		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		int size = Math.min(getWidth(), getHeight()) - halfPadding - 1;
		int joystickSize = size / 5;

		g.setColor(ThemeManager.get(ThemeValue.BACKGROUND_JOYSTICK));
		g.fillOval(getWidth() / 2 - size / 2, getHeight() / 2 - size / 2, size, size);
		g.setColor(Color.BLACK);
		g.drawOval(getWidth() / 2 - size / 2, getHeight() / 2 - size / 2, size, size);
		g.setColor(ThemeManager.get(ThemeValue.JOYSTICK));
		if(mouseDragging) {
			g.fillOval(mouseX - joystickSize / 2, mouseY - joystickSize / 2, joystickSize, joystickSize);
			g.drawLine(getWidth() / 2, getHeight() / 2, mouseX, mouseY);
		} else {
			g.fillOval(getWidth() / 2 - joystickSize / 2, getHeight() / 2 - joystickSize / 2, joystickSize, joystickSize);
		}

		gr.drawImage(puffer, 0, 0, null);
		notifyAllJoystickListeners();
	}
	
	private float map(float value, float istart, float istop, float ostart, float ostop) {
		return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
	}
	
	private void sendConfigurationToRobot() {
		// 180 is the maximum that the motors can handle.
		// The computed magnitude value is between [0; 1].
		float joystickAngle = computeAngle();
		float robotAngle = map(joystickAngle, 0.0f, (float) Math.PI, -90.0f, 90.0f);
		BluetoothWrapper.sendData(new CommandeTurn(robotAngle), CommunicatorV2.INTERVAL_HIGH);
	}
	
	public void setConfiguration(float x, float y, float magnitude) {
		int size = (Math.min(getWidth(), getHeight()) - halfPadding - 1) / 2;
		float x_ = x * size * magnitude;
		float y_ = y * size * magnitude;
		float distance = (float) Math.hypot(x_, y_);
		
		// Simulate a mouse drag
		mouseDragging = distance >= 0.1;

		mouseX = (int) x_ + getWidth() / 2;
		mouseY = (int) y_ + getHeight() / 2;
		
		if(distance > size) {
			float magnitude_ = (float) Math.sqrt(x_ * x_ + y_ * y_);
			mouseX = (int) ((x_ / magnitude_) * size * magnitude) + getWidth() / 2;
			mouseY = (int) ((y_ / magnitude_) * size * magnitude) + getHeight() / 2;
		}
		
		if(!mouseDragging) {
			mouseX = getWidth() / 2;
			mouseY = getHeight() / 2;
		}
		
		if(magnitude > 0.1f) {
			sendConfigurationToRobot();
		}
		
		repaint();
	}
	
	/**
	 * Compute the angle in radians
	 * @return angle in radians
	 */
	public float computeAngle() {
		if(mouseDragging) {
			float x = mouseX - getWidth() / 2;
			float y = mouseY - getHeight() / 2;
			float magnitudeB = (float) Math.sqrt(x * x + y * y);
			float angle = (float) Math.acos(x / magnitudeB);
			if(y > 0) return -angle;
			Vector direction = computeDirection();
			float angle = (float) direction.angle(new Vector(1, 0));
			if(angle < 0) angle += 2 * Math.PI;
			return angle;
		} else return 0.0f;
	}
	
	/**
	 * Compute the normalized magnitude
	 * @return normalized magnitude
	 */
	public float computeMagnitude() {
		if(mouseDragging) {
			float x = mouseX - getWidth() / 2;
			float y = mouseY - getHeight() / 2;
			int size = Math.min(getWidth(), getHeight()) - halfPadding - 1;
			return (float) (Math.sqrt(x * x + y * y) / size) * 2;
		} else return 0.0f;
	}
	
	public Vector computeDirection() {
		if(mouseDragging) {
			float x = mouseX - getWidth() / 2;
			float y = mouseY - getHeight() / 2;
			Vector direction = new Vector(x, y);
			return direction.normalize();
		} else return new Vector(0f, 0f);
	}
	
	public void addJoystickListener(JoystickListener joystickListener) {
		if(!joystickListeners.contains(joystickListener))
			joystickListeners.add(joystickListener);
	}
	
	private void notifyAllJoystickListeners() {
		float angle = computeAngle();
		float magnitude = computeMagnitude();

		Vector direction = computeDirection();
		for(JoystickListener joystickListener : joystickListeners) {
			joystickListener.onJoystickChange(angle, magnitude, direction);
		}
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
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
		// Not used
	}

	@Override
	public String getHelpMessage() {
		return "Joystick\n   Can be controlled with:\n      A game controller\n      A mouse\n\n"
				+ "Speed Slider\n   Allows you to control the\n   robot's speed";
	}

}