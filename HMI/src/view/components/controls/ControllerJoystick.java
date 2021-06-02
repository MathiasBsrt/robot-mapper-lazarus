package view.components.controls;

import com.studiohartman.jamepad.ControllerButton;
import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

import commande.CommandeStop;
import model.BluetoothWrapper;
import model.Vector;
import model.controller.ControllerWrapper;
import model.settings.ProgramSettings;
import model.settings.Setting;
import model.settings.SettingChangeListener;
import model.theme.ThemeManager;
import model.theme.Themes;
import view.AppFrame;
import view.SettingsFrame;
import view.components.Joystick;
import view.components.PerformanceAlert;
import view.components.RobotMap;
import view.components.ToggleButtonImage;
import view.components.ToggleSwitch;

/**
 * An implementation used to represent the inputs of
 * the game controller
 * @author TRAFNY Theo - UPSSITECH
 *
 */
public class ControllerJoystick implements MouvementInputs, SettingChangeListener {

	// Components that are controllable by the controller.
	private Joystick joystick;
	private SpeedSlider speedSlider;
	private ToggleButtonImage performanceToggleButton;
	private RobotMap robotMap;
	private ToggleSwitch autonomousCartographySwitch;

	private ControllerManager controllerManager = ControllerWrapper.getManager();

	private double joystick_deadzone = 0.3f;
	private double triggers_deadzone = 0.3f;
	
	private ControllerButton automaticScaleButton;
	private ControllerButton autonomousCartographyButton;
	private ControllerButton performanceButton;
	private ControllerButton settingsButton;
	private ControllerButton themeButton;
	
	public ControllerJoystick(Joystick joystick, SpeedSlider speedSlider, ToggleButtonImage performanceToggleButton,
			RobotMap robotMap, ToggleSwitch autonomousCartographySwitch) {
		this.joystick = joystick;
		this.speedSlider = speedSlider;
		this.performanceToggleButton = performanceToggleButton;
		this.robotMap = robotMap;
		this.autonomousCartographySwitch = autonomousCartographySwitch;
		ProgramSettings.listen(this, Setting.CONTROLLER_JOYSTICK_DEADZONE);
		ProgramSettings.listen(this, Setting.CONTROLLER_TRIGGER_DEADZONE);
		ProgramSettings.listen(this, Setting.CONTROLLER_AUTOMATIC_SCALE_MODE);
		ProgramSettings.listen(this, Setting.CONTROLLER_AUTONOMOUS_CARTOGRAPHY);
		ProgramSettings.listen(this, Setting.CONTROLLER_PERFORMANCE_MODE);
		ProgramSettings.listen(this, Setting.CONTROLLER_SETTINGS);
		ProgramSettings.listen(this, Setting.CONTROLLER_THEME);
		joystick_deadzone = ProgramSettings.getDouble(Setting.CONTROLLER_JOYSTICK_DEADZONE);
		triggers_deadzone = ProgramSettings.getDouble(Setting.CONTROLLER_TRIGGER_DEADZONE);
		automaticScaleButton = (ControllerButton) ProgramSettings.getEnum(Setting.CONTROLLER_AUTOMATIC_SCALE_MODE);
		autonomousCartographyButton = (ControllerButton) ProgramSettings.getEnum(Setting.CONTROLLER_AUTONOMOUS_CARTOGRAPHY);
		settingsButton = (ControllerButton) ProgramSettings.getEnum(Setting.CONTROLLER_SETTINGS);
		themeButton = (ControllerButton) ProgramSettings.getEnum(Setting.CONTROLLER_THEME);
		performanceButton = (ControllerButton) ProgramSettings.getEnum(Setting.CONTROLLER_PERFORMANCE_MODE);
	}

	/**
	 * Is the game controller being used.
	 * @return
	 */
	public boolean isUsing() {
		boolean isUsing = ControllerWrapper.isUsing(0);
		return isUsing;
	}

	/**
	 * Is the game controller available.
	 * @return
	 */
	public boolean isAvailable() {
		return controllerManager.getNumControllers() > 0;
	}

	@Override
	public float computeAngle() {
		if (controllerManager.getNumControllers() > 0 && AppFrame.windowIsFocused()) {
			if (computeMagnitude() >= joystick_deadzone) {
				ControllerState controllerState = controllerManager.getState(0);
				float angle = (float) Math.toRadians(controllerState.leftStickAngle);
				if (angle < 0)
					angle += 2 * Math.PI;
				return angle;
			}
		}
		return 0.0f;
	}

	@Override
	public float computeMagnitude() {
		if (controllerManager.getNumControllers() > 0 && AppFrame.windowIsFocused()) {
			ControllerState controllerState = controllerManager.getState(0);
			if (controllerState.leftStickMagnitude >= joystick_deadzone)
				return controllerState.leftStickMagnitude;
		}
		return 0.0f;
	}

	@Override
	public Vector computeDirection() {
		if (controllerManager.getNumControllers() > 0 && AppFrame.windowIsFocused()) {
			if (computeMagnitude() >= joystick_deadzone) {
				ControllerState controllerState = controllerManager.getState(0);
				return new Vector(controllerState.leftStickX, -controllerState.leftStickY).normalize();
			}
		}
		return new Vector(0, 0);
	}

	@Override
	public void update() {
		if(!AppFrame.windowIsFocused()) return;
		Vector direction = computeDirection();
		float magnitude = computeMagnitude();
		joystick.setConfiguration(direction.x, direction.y, magnitude);

		if (ControllerWrapper.controllerAvailable()) {
			ControllerState controllerState = controllerManager.getState(0);
			float rightStickAngle = (float) Math.toRadians(controllerState.rightStickAngle);
			if (rightStickAngle > 0 && controllerState.rightStickMagnitude >= joystick_deadzone) {
				speedSlider.incSpeed();
			} else if (rightStickAngle < 0 && controllerState.rightStickMagnitude >= joystick_deadzone) {
				speedSlider.decSpeed();
			}
			if (controllerState.rightTrigger >= triggers_deadzone) {
				speedSlider.setValue(speedSlider.getMaximum());
			} else if (controllerState.leftTrigger >= triggers_deadzone) {
				speedSlider.setValue(10);
			}
		}
	}

	/**
	 * Check states of all buttons of the game controller.
	 */
	public void updateButtons() {
		ControllerWrapper.update(0);
		if (ControllerWrapper.controllerAvailable()) {
			
			if(AppFrame.windowIsFocused()) {
				// If the Y button is pressed
				if (ControllerWrapper.isButtonPressed(performanceButton) && !PerformanceAlert.alreadyOneIsOpen()) {
					speedSlider.togglePerformanceMode();
					performanceToggleButton.setActivated(speedSlider.performanceState());
				}
	
				if (ControllerWrapper.isButtonPressed(automaticScaleButton)) {
					robotMap.toggleAutomaticScale();
				}
	
				if (ControllerWrapper.isButtonPressed(autonomousCartographyButton)) {
					autonomousCartographySwitch.toggle();
				}

				if(ControllerWrapper.isButtonPressed(themeButton)) {
					if(ThemeManager.getCurrentThemeType().equals(Themes.LIGHT_THEME)) {
						ThemeManager.useTheme(Themes.DARK_THEME);
					} else {
						ThemeManager.useTheme(Themes.LIGHT_THEME);
					}
				}
			}
			
			if(ControllerWrapper.isButtonPressed(settingsButton)) {
				SettingsFrame.toggle();
			}
			

			// If it was not using
			if(ControllerWrapper.stopUsing()) {
				BluetoothWrapper.sendData(new CommandeStop());
			}
		}
	}

	@Override
	public void onSettingChange(Setting setting) {
		switch (setting) {
		case CONTROLLER_JOYSTICK_DEADZONE: {
			joystick_deadzone = ProgramSettings.getDouble(setting);
			break;
		}
		case CONTROLLER_TRIGGER_DEADZONE: {
			triggers_deadzone = ProgramSettings.getDouble(setting);
			break;
		}
		case CONTROLLER_AUTOMATIC_SCALE_MODE: {
			automaticScaleButton = (ControllerButton) ProgramSettings.getEnum(setting);
			break;
		}
		case CONTROLLER_AUTONOMOUS_CARTOGRAPHY: {
			autonomousCartographyButton = (ControllerButton) ProgramSettings.getEnum(setting);
			break;
		}
		case CONTROLLER_PERFORMANCE_MODE: {
			performanceButton = (ControllerButton) ProgramSettings.getEnum(setting);
			break;
		}
		case CONTROLLER_SETTINGS: {
			settingsButton = (ControllerButton) ProgramSettings.getEnum(setting);
			break;
		}
		case CONTROLLER_THEME: {
			themeButton = (ControllerButton) ProgramSettings.getEnum(setting);
			break;
		}
		}
	}

}
