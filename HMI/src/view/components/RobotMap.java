package view.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.Vector;
import model.controller.ControllerWrapper;
import model.theme.ThemeManager;
import model.theme.ThemeValue;

public abstract class RobotMap extends JPanel {

	protected static final Vector CENTER = new Vector(0, 0);

	protected Robot robot = new Robot(this, new Vector(0, 0));

	// This thread is used to refresh (repaint) the component.
	private RobotMapThread thread;

	private ImageIndicator robotIsRunningIndicator;
	private ImageIndicator robotStoppedIndicator;

	private float scale = 1.0f;

	private JSlider scaleSlider = new JSlider(30, 200, 100);
	private JCheckBox scaleCheckBox = new JCheckBox();
	private JCheckBox centerCheckBox = new JCheckBox();
	
	private List<Vector> points = new ArrayList<>();

	public RobotMap(ImageIndicator robotIsRunningIndicator, ImageIndicator robotStoppedIndicator) {
		this.robotIsRunningIndicator = robotIsRunningIndicator;
		this.robotStoppedIndicator = robotStoppedIndicator;

		ThemeManager.registerComponent(scaleSlider, scaleSlider::setBackground, ThemeValue.BACKGROUND_ROBOT_MAP);
		ThemeManager.registerComponent(scaleCheckBox, scaleCheckBox::setBackground, ThemeValue.BACKGROUND_ROBOT_MAP);
		ThemeManager.registerComponent(centerCheckBox, centerCheckBox::setBackground, ThemeValue.BACKGROUND_ROBOT_MAP);
		scaleSlider.setBackground(ThemeManager.get(ThemeValue.BACKGROUND_ROBOT_MAP));
		scaleCheckBox.setBackground(ThemeManager.get(ThemeValue.BACKGROUND_ROBOT_MAP));
		centerCheckBox.setBackground(ThemeManager.get(ThemeValue.BACKGROUND_ROBOT_MAP));
		
		super.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.ipady = 0;
		c.weighty = 1.0;
		c.anchor = GridBagConstraints.PAGE_START;
		c.insets = new Insets(10,0,0,0);
		c.gridx = 1;
		c.gridwidth = 2;
		c.gridy = 0;
		
		super.add(scaleSlider, c);
		scaleSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				scale = (scaleSlider.getValue() / 100.0f);
			}
		});
		
		c.insets = new Insets(10,200,0,0);
		c.anchor = GridBagConstraints.FIRST_LINE_END;
		c.gridx = 2;
		super.add(scaleCheckBox, c);
		scaleCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				scaleSlider.setEnabled(!scaleCheckBox.isSelected());
			}
		});
		scaleCheckBox.setSelected(true);
		scaleSlider.setEnabled(!scaleCheckBox.isSelected());
		
		c.insets = new Insets(0,0,0,0);
		c.anchor = GridBagConstraints.LAST_LINE_END;
		c.gridx = 2;
		c.gridy = 2;
		super.add(centerCheckBox, c);
		centerCheckBox.addActionListener(new ActionListener() {
			private boolean lastStateOfScaleCheckbox;
			@Override
			public void actionPerformed(ActionEvent e) {
				if(centerCheckBox.isSelected()) {
					lastStateOfScaleCheckbox = scaleCheckBox.isSelected();
					scaleCheckBox.setSelected(false);
					scaleCheckBox.setEnabled(false);
					scaleSlider.setEnabled(true);
				} else {
					scaleCheckBox.setSelected(lastStateOfScaleCheckbox);
					scaleCheckBox.setEnabled(true);
					scaleSlider.setEnabled(!scaleCheckBox.isSelected());
				}
			}
		});
		centerCheckBox.setSelected(false);

		thread = new RobotMapThread(this);
		thread.start();
	}
	
	@Override
	public void paint(Graphics g) {
		int width = getWidth();
		int height = getHeight();
		g.clearRect(0, 0, width, height);
		
//		g.fillOval(robotLocation.x - , height, width, height);
		
		
		g.setColor(Color.BLACK);
		g.drawRect(10, 10, width - 20, height - 20);
	}

	@Override
	public void onJoystickChange(float angle, float magnitude) {
		float vx = (float) (magnitude * Math.cos(angle));
		float vy = (float) (magnitude * Math.sin(angle));
		robotVelocity.x = vx;
		robotVelocity.y = vy;
	}
	
	public void addPoint(Vector point) {
		points.add(point);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(ThemeManager.get(ThemeValue.BACKGROUND_ROBOT_MAP));
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.BLACK);

		beforeDrawing(g);
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform previousTransform = g2d.getTransform();
		int width = getWidth();
		int height = getHeight();
		g2d.translate(width / 2, height / 2);
		
		if (robot.isMoving()) {
			robotIsRunningIndicator.setActivated(true);
			robotStoppedIndicator.setActivated(false);
		} else {
			robotIsRunningIndicator.setActivated(false);
			robotStoppedIndicator.setActivated(true);
		}

		// Compute auto scaling if the center mode isnt activated.
		if (scaleCheckBox.isSelected() && !centerCheckBox.isSelected())
			computeAutoScale();

		beforeDrawing();

		g.setColor(ThemeManager.get(ThemeValue.ROBOT_COLOR));
		g2d.scale(scale, scale);
		if(centerCheckBox.isSelected()) {
			g2d.rotate(-robot.rotation);
			g2d.translate(-robot.location.x, -robot.location.y);
		}
		
		for(int i = 0; i < points.size(); i++) {
			Vector point1 = points.get(i);
			g2d.drawRect((int) point1.x - 2, (int) point1.y - 2, 4, 4);
		}

		robot.draw(g2d);
		g2d.setTransform(previousTransform);

		g.setColor(Color.BLACK);
		g.drawRect(10, 10, width - 20, height - 20);

		g.setColor(ThemeManager.get(ThemeValue.TEXT));
		int textHeight = g.getFontMetrics().getHeight();
		g.drawString(String.format("Refresh rate: %d, Thread count: %d", thread.getFPS(), Thread.activeCount()), 15,
				getHeight() - textHeight);

		g.drawString("Location:", 15, getHeight() - 5 * textHeight);
		g.drawString(String.format("X: %.4f", robot.location.x), 15, getHeight() - 4 * textHeight);
		g.drawString(String.format("Y: %.4f", robot.location.y), 15, getHeight() - 3 * textHeight);
		g.drawString("Rotation: " + String.format("%.2f", robot.rotation), 15, getHeight() -  2 * textHeight);
		
		if (ControllerWrapper.controllerAvailable()) {
			g.drawString("A controller has been connected.", 15, getHeight() - 6 * textHeight);
		}
	}

	public abstract void beforeDrawing();
	
	public abstract void beforeDrawing(Graphics g);

	public abstract void setConfiguration(float x, float y, float angle);

	public void setScale(float scale) {
		this.scale = scale;
	}

	public void setAutomaticScale(boolean enable) {
		scaleSlider.setEnabled(!enable);
		scaleCheckBox.setSelected(enable);
	}

	public void toggleAutomaticScale() {
		scaleSlider.setEnabled(!scaleSlider.isEnabled());
		scaleCheckBox.setSelected(!scaleCheckBox.isSelected());
	}

	/**
	 * Compute a percentage that represents how much the value is close to min or
	 * max.
	 * 
	 * @param value   The current value to test
	 * @param min     The minimum boundary
	 * @param max     The maximum boundary
	 * @param padding If the value distance of min or max is equals to padding, the
	 *                method will returns 100%
	 * @return A percentage that represents how much the value is close to min or
	 *         max.
	 */
	private float diff(float value, float min, float max, float padding) {
		float distanceFromMin = Math.abs(value - min);
		float distanceFromMax = Math.abs(max - value);
		if (distanceFromMin < distanceFromMax) {
			return 1 - Math.min(distanceFromMin, padding) / padding;
		} else {
			return 1 - Math.min(distanceFromMax, padding) / padding;
		}
	}

	/**
	 * Check if the robot's location is close or not from walls and performs an
	 * automatic scale of the map if needed.
	 */
	private void computeAutoScale() {
		float xDistanceFromWalls = diff(robot.location.x, -getWidth() / (2 * scale), getWidth() / (2 * scale),
				getWidth() / (6 * scale));
		float yDistanceFromWalls = diff(robot.location.y, -getHeight() / (2 * scale), getHeight() / (2 * scale),
				getHeight() / (8 * scale));

		float distanceFromWalls = Math.max(xDistanceFromWalls, yDistanceFromWalls);
		if (distanceFromWalls > 0.5) {
			scale = Math.max(scale - 0.2f, 0.3f);
			scaleSlider.setValue((int) (scale * 100));
		}

		float padding = 100 / scale;
		float distanceFromCenter = 1 - Math.min(robot.location.dist(CENTER), padding) / padding;

		if (distanceFromCenter > 0.5) {
			scale = Math.min(scale + 0.2f, 1.0f);
			scaleSlider.setValue((int) (scale * 100));
		}
	}
	
	private Vector localToGlobalCoords(Vector localCoords) {
		return localCoords._scale(scale);
	}
}
