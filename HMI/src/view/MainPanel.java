package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import commande.CommandeAutomaticMode;
import commande.CommandeBackwards;
import commande.CommandeForward;
import commande.CommandeManualMode;
import commande.CommandeStop;
import jssc.SerialPortException;
import model.BluetoothStateListener;
import model.BluetoothWrapper;
import model.theme.ThemeManager;
import model.theme.ThemeValue;
import view.components.BatteryIcon;
import view.components.ImageButton;
import view.components.ImageIndicator;
import view.components.Joystick;
import view.components.PerformanceAlert;
import view.components.RobotMap;
import view.components.RobotMapReal;
import view.components.ToggleButtonImage;
import view.components.ToggleButtonListener;
import view.components.ToggleSwitch;
import view.components.controls.SpeedSlider;

public class MainPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8668618688820184387L;
	
	private AppFrame frame;
	
	protected Joystick joystick;
	protected SpeedSlider speedSlider;
	protected ToggleSwitch autonomousCartographySwitch;
	protected ToggleSwitch autonomousModeSwitch;
	protected RobotMap robotMap;
	protected ImageIndicator bluetoothIndicator;
	
	public MainPanel(AppFrame frame) {
		this.frame = frame;
		super.setLayout(new GridLayout(1, 1));
		
		ThemeManager.registerComponent(this, this::setBackground, ThemeValue.BACKGROUND);
		this.setBackground(ThemeManager.get(ThemeValue.BACKGROUND));

		speedSlider = new SpeedSlider();
		joystick = new Joystick(speedSlider);
		autonomousCartographySwitch = new ToggleSwitch();
		autonomousModeSwitch = new ToggleSwitch();

		Box boxLayout = Box.createVerticalBox();

		Box topBox = Box.createHorizontalBox();

		Box indicatorsBox = Box.createVerticalBox();

		BatteryIcon batteryIcon = new BatteryIcon(100f);
		batteryIcon.setPreferredSize(new Dimension(100, 50));
		batteryIcon.setMaximumSize(new Dimension(100, 50));
		batteryIcon.setPercentage(100);
		batteryIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
		indicatorsBox.add(batteryIcon);
		indicatorsBox.add(Box.createVerticalStrut(50));
		bluetoothIndicator = new ImageIndicator(new ImageIcon("res/bluetoothIcon.png"),
				ThemeManager.get(ThemeValue.TOGGLE_BUTTONS_MASK), ThemeManager.get(ThemeValue.TOGGLE_BUTTONS_OFF),
				ThemeManager.get(ThemeValue.BLUETOOTH_INDICATOR_ON));
		bluetoothIndicator.setPreferredSize(new Dimension(50, 76));
		bluetoothIndicator.setMaximumSize(new Dimension(50, 76));
		bluetoothIndicator.setAlignmentX(Component.CENTER_ALIGNMENT);
		bluetoothIndicator.setHelpMessage("Bluetooth Indicator");
		BluetoothWrapper.addBluetoothStateListener(new BluetoothStateListener() {
			
			private Timer reconnectTimer = new Timer(0, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("Trying to reconnect ...");
					String serialPort = BluetoothWrapper.getConnectedSerialPortName();
					try {
						BluetoothWrapper.connect(serialPort);
						reconnectTimer.stop();
					} catch (SerialPortException | InterruptedException e1) {
						reconnectTimer.restart();
						System.out.println("Failed to reconnect.");
					}
				}
			});
			
			@Override
			public void onBluetoothStateChange(boolean connected) {
				bluetoothIndicator.setActivated(connected);
				
				if(connected) {
					reconnectTimer.stop();
				} else {
					JOptionPane.showMessageDialog(null, "Bluetooth connection lost.", "A Bluetooth connection error occured", JOptionPane.ERROR_MESSAGE);
					try {
						BluetoothWrapper.disconnect();
					} catch (SerialPortException e) {
						e.printStackTrace();
					}
					reconnectTimer.start();
				}
				
			}
		});
		indicatorsBox.add(bluetoothIndicator);
		topBox.add(Box.createHorizontalGlue());
		topBox.add(indicatorsBox);
		topBox.add(Box.createHorizontalGlue());

		Box rightIndicatorsBox = Box.createVerticalBox();

		ImageIndicator robotIsRunningIndicator = new ImageIndicator(new ImageIcon("res/robotRunningIcon.png"));
		robotIsRunningIndicator.setPreferredSize(new Dimension(50, 38));
		robotIsRunningIndicator.setMaximumSize(new Dimension(50, 38));
		robotIsRunningIndicator.setAlignmentX(Component.CENTER_ALIGNMENT);
		robotIsRunningIndicator.setHelpMessage("Robot is rolling");
		rightIndicatorsBox.add(robotIsRunningIndicator);
		rightIndicatorsBox.add(Box.createVerticalStrut(20));

		ImageIndicator robotStoppedIndicator = new ImageIndicator(new ImageIcon("res/robotStoppedIcon.png"));
		robotStoppedIndicator.setPreferredSize(new Dimension(50, 55));
		robotStoppedIndicator.setMaximumSize(new Dimension(50, 55));
		robotStoppedIndicator.setAlignmentX(Component.CENTER_ALIGNMENT);
		robotStoppedIndicator.setActivated(true);
		robotStoppedIndicator.setHelpMessage("Robot is stopped");
		rightIndicatorsBox.add(robotStoppedIndicator);
		rightIndicatorsBox.add(Box.createVerticalStrut(100));
		
		ToggleButtonImage performanceButton = new ToggleButtonImage(new ImageIcon("res/performanceIcon.png"));
		performanceButton.setPreferredSize(new Dimension(50, 50));
		performanceButton.setMaximumSize(new Dimension(50, 50));
		performanceButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		performanceButton.setHelpMessage("Performance Mode\n   This mode allows you \n   to control the robot\n   at full speed.\n   Be carefull ...");

		performanceButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!PerformanceAlert.alreadyOneIsOpen())
					speedSlider.togglePerformanceMode();
			}
		});

		performanceButton.addToggleButtonListener(new ToggleButtonListener() {
			@Override
			public boolean onStateChange(boolean activated) {
				if (PerformanceAlert.alreadyOneIsOpen())
					return false;
				if (activated) {
					System.out.println(PerformanceAlert.alreadyOneIsOpen());
					if (!PerformanceAlert.alreadyOneIsOpen()) {
						PerformanceAlert.open(getLocation().x + getWidth() / 2, getLocation().y + getHeight() / 2);
					}
				}
				return true;
			}
		});
		rightIndicatorsBox.add(performanceButton);

//		robotMap = new RobotMapSimulation(joystick, speedSlider, performanceButton, autonomousCartographySwitch,
//				robotIsRunningIndicator, robotStoppedIndicator);
		robotMap = new RobotMapReal(joystick, speedSlider, performanceButton, autonomousCartographySwitch,
				robotIsRunningIndicator, robotStoppedIndicator);
		robotMap.setPreferredSize(new Dimension(600, 400));
		robotMap.setMaximumSize(new Dimension(600, 400));

		topBox.add(robotMap);

		topBox.add(Box.createHorizontalGlue());
		topBox.add(rightIndicatorsBox);
		topBox.add(Box.createHorizontalGlue());

		boxLayout.add(topBox);

		Box controlBox = Box.createHorizontalBox();

		// Create, set up and add the autonomous box to the main one.
		setupAutonomousBox(autonomousCartographySwitch, boxLayout);

		// Create, set up and add the control buttons box to the bottom box
		// (controlBox).
		setupControlButtonsBox(controlBox);

		// Create, set up and add the joystick box to the bottom box (controlBox).
		setupJoystickBox(joystick, speedSlider, controlBox);

		ImageButton settingsButton = new ImageButton("res/settingsIcon.png");
		settingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		settingsButton.setHelpMessage("Settings\n   You can re-map all the keys for\n   the game controller.");
		settingsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SettingsFrame.open();
			}
		});
		
		controlBox.add(settingsButton);
		controlBox.add(Box.createHorizontalStrut(85));

		boxLayout.add(controlBox);
		
		super.add(boxLayout);
		
	}
	
	public void setRobotMapFullscreen(boolean fullscreen) {
		Dimension dimension;
		if(fullscreen) {
			dimension = new Dimension(super.getSize().width, super.getSize().height);
			System.out.println(super.getSize().height);
		} else {
			dimension = new Dimension(600, 400);
		}
		robotMap.setSize(dimension);
		robotMap.setPreferredSize(dimension);
		robotMap.setMaximumSize(dimension);
	}
	
	public void setBluetoothState(boolean bluetoothState) {
		bluetoothIndicator.setActivated(bluetoothState);
	}

	/**
	 * Create, set up and add the autonomous box to the main one. This box contains:
	 * - A JLabel - A Switch Button
	 * 
	 * @param boxLayout The main box
	 */
	private void setupAutonomousBox(ToggleSwitch autonomousCartographySwitch, Box boxLayout) {
		Box autonomousBox = Box.createHorizontalBox();
		ThemeManager.registerComponent(autonomousCartographySwitch, autonomousCartographySwitch::setButtonColor, ThemeValue.AUTONOMOUS_SWITCH);
		ThemeManager.registerComponent(autonomousCartographySwitch, autonomousCartographySwitch::setSwitchColor, ThemeValue.AUTONOMOUS_SWITCH_OFF);
		ThemeManager.registerComponent(autonomousCartographySwitch, autonomousCartographySwitch::setActiveSwitch, ThemeValue.AUTONOMOUS_SWITCH_ON);
		autonomousCartographySwitch.setButtonColor(ThemeManager.get(ThemeValue.AUTONOMOUS_SWITCH));
		autonomousCartographySwitch.setSwitchColor(ThemeManager.get(ThemeValue.AUTONOMOUS_SWITCH_OFF));
		autonomousCartographySwitch.setActiveSwitch(ThemeManager.get(ThemeValue.AUTONOMOUS_SWITCH_ON));
		autonomousCartographySwitch.setActivateText("ON");
		autonomousCartographySwitch.setUnactivateText("OFF");
		autonomousCartographySwitch.setPreferredSize(new Dimension(70, 30));
		autonomousCartographySwitch.setMaximumSize(new Dimension(70, 30));
		autonomousCartographySwitch.setSpeed(10);
		autonomousCartographySwitch.setFontSize(15);
		autonomousCartographySwitch.setHelpMessage("Autonomous Cartography\n   Enable autonomous cartography");
		autonomousBox.add(Box.createHorizontalGlue());
		JLabel autonomousLabel = new JLabel("Autonomous Cartography");
		autonomousLabel.setBorder(
				BorderFactory.createCompoundBorder(new LineBorder(Color.BLACK, 1, true), new EmptyBorder(5, 5, 5, 5)));
		autonomousLabel.setFont(new Font("Arial", Font.PLAIN, 15));
		ThemeManager.registerComponent(autonomousLabel, autonomousLabel::setForeground, ThemeValue.TEXT);
		autonomousLabel.setForeground(ThemeManager.get(ThemeValue.TEXT));
		autonomousBox.add(autonomousLabel);
		autonomousBox.add(Box.createHorizontalStrut(10));
		autonomousBox.add(autonomousCartographySwitch);
		
		ThemeManager.registerComponent(autonomousModeSwitch, autonomousModeSwitch::setButtonColor, ThemeValue.AUTONOMOUS_SWITCH);
		ThemeManager.registerComponent(autonomousModeSwitch, autonomousModeSwitch::setSwitchColor, ThemeValue.AUTONOMOUS_SWITCH_OFF);
		ThemeManager.registerComponent(autonomousModeSwitch, autonomousModeSwitch::setActiveSwitch, ThemeValue.AUTONOMOUS_SWITCH_ON);
		autonomousModeSwitch.setButtonColor(ThemeManager.get(ThemeValue.AUTONOMOUS_SWITCH));
		autonomousModeSwitch.setSwitchColor(ThemeManager.get(ThemeValue.AUTONOMOUS_SWITCH_OFF));
		autonomousModeSwitch.setActiveSwitch(ThemeManager.get(ThemeValue.AUTONOMOUS_SWITCH_ON));
		autonomousModeSwitch.setActivateText("ON");
		autonomousModeSwitch.setUnactivateText("OFF");
		autonomousModeSwitch.setPreferredSize(new Dimension(70, 30));
		autonomousModeSwitch.setMaximumSize(new Dimension(70, 30));
		autonomousModeSwitch.setSpeed(10);
		autonomousModeSwitch.setFontSize(15);
		autonomousModeSwitch.setHelpMessage("Autonomous Mode\n   Enable autonomous mode");
		autonomousModeSwitch.setActivated(true);
		autonomousModeSwitch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(autonomousModeSwitch.isActivated()) {
					BluetoothWrapper.sendData(new CommandeAutomaticMode());
					System.out.println("Manual mode off.");
				} else {
					BluetoothWrapper.sendData(new CommandeManualMode());
					System.out.println("Manual mode on.");
				}
			}
		});
		
		autonomousBox.add(Box.createHorizontalStrut(150));
		JLabel autonomousModeLabel = new JLabel("Autonomous Mode");
		autonomousModeLabel.setBorder(
				BorderFactory.createCompoundBorder(new LineBorder(Color.BLACK, 1, true), new EmptyBorder(5, 5, 5, 5)));
		autonomousModeLabel.setFont(new Font("Arial", Font.PLAIN, 15));
		ThemeManager.registerComponent(autonomousModeLabel, autonomousModeLabel::setForeground, ThemeValue.TEXT);
		autonomousModeLabel.setForeground(ThemeManager.get(ThemeValue.TEXT));
		autonomousBox.add(autonomousModeLabel);
		autonomousBox.add(Box.createHorizontalStrut(10));
		autonomousBox.add(autonomousModeSwitch);
		autonomousBox.add(Box.createHorizontalGlue());
		
		boxLayout.add(autonomousBox);
	}

	/**
	 * Create, set up and add the control buttons box to the bottom box
	 * (controlBox). This box contains: - An Up Button (press to let the robot go
	 * forward) - A Down Button (press to let the robot go backward)
	 * 
	 * @param controlBox The box that contains all the controls
	 */
	private void setupControlButtonsBox(Box controlBox) {
		Box controlButtonsBox = Box.createVerticalBox();
		ImageButton upButton = new ImageButton("res/upIcon.png");
		upButton.setHelpMessage("Forward Button\n   Tell the robot to go\n   straight forward");
		ImageButton downButton = new ImageButton("res/downIcon.png");
		downButton.setHelpMessage("Backward Button\n   Tell the robot to go\n   backward");
		upButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
//				robotMap.forward(15);
				BluetoothWrapper.sendData(new CommandeForward());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				BluetoothWrapper.sendData(new CommandeStop());
			}
		});
		downButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
//				robotMap.backward(15);
				BluetoothWrapper.sendData(new CommandeBackwards());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
//				robotMap.stop();
				BluetoothWrapper.sendData(new CommandeStop());
			}
		});
		controlButtonsBox.add(upButton);
		controlButtonsBox.add(Box.createVerticalStrut(10));
		controlButtonsBox.add(downButton);

		controlBox.add(Box.createHorizontalGlue());
		controlBox.add(Box.createHorizontalStrut(300));
		controlBox.add(controlButtonsBox);
		controlBox.add(Box.createHorizontalGlue());

	}

	/**
	 * Create, set up and add the joystick box to the bottom box (controlBox). This
	 * box contains: - A Joystick - A Speed slider
	 * 
	 * @param controlBox The box that contains all the controls
	 */
	private void setupJoystickBox(Joystick joystick, JSlider speedSlider, Box controlBox) {
		Box joystickBox = Box.createHorizontalBox();

		joystick.setPreferredSize(new Dimension(200, 200));
		joystick.setMaximumSize(new Dimension(200, 200));
		joystickBox.add(joystick);

		// Create a vertical slider with values in [0, 100]
		speedSlider.setMajorTickSpacing(25);
		speedSlider.setPaintTicks(true); // Show at every 25 percents a little tick.
		speedSlider.setPaintLabels(true); // Print values every 25 percents.

		// Register an event to capture when the slider's value change.
		speedSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				//
//				robotMap.setMaxSpeed(10 * speedSlider.getValue() / 100);
			}
		});
		joystickBox.add(speedSlider);

		controlBox.add(joystickBox);
		controlBox.add(Box.createHorizontalGlue());

		speedSlider.setSize(speedSlider.getWidth(), 100);
	}

}
