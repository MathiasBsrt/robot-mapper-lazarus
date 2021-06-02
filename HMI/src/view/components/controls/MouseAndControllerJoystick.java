package view.components.controls;

import com.studiohartman.jamepad.ControllerManager;

import model.Vector;
import model.controller.ControllerWrapper;
import view.components.Joystick;
import view.components.RobotMap;
import view.components.ToggleButtonImage;
import view.components.ToggleSwitch;

/**
 * Describes as well a mouse / keyboard mouvement inputs 
 * and a game controller mouvement inputs.
 * By default, the mouse / keyboard mouvement inputs will be called,
 * but if the game controller is being used, it will be the game controller to be called.
 * @see ControllerJoystick
 * @see Joystick
 * 
 * @author TRAFNY Theo - UPSSITECH
 *
 */
public class MouseAndControllerJoystick implements MouvementInputs {

	private Joystick joystick;
	private SpeedSlider speedSlider;
	private ToggleButtonImage performanceButton;
	private ControllerJoystick controllerJoystick;
	private ControllerManager controllerManager = ControllerWrapper.getManager();

	private boolean wasUsing = false;

	public MouseAndControllerJoystick(Joystick joystick, SpeedSlider speedSlider, ToggleButtonImage performanceButton,
			RobotMap robotMap, ToggleSwitch autonomousCartographySwitch) {
		this.joystick = joystick;
		this.speedSlider = speedSlider;
		this.performanceButton = performanceButton;
		this.controllerJoystick = new ControllerJoystick(joystick, speedSlider, performanceButton, robotMap,
				autonomousCartographySwitch);
	}

	@Override
	public float computeAngle() {
		if (controllerJoystick.isUsing()) {
			return controllerJoystick.computeAngle();
		} else if (wasUsing) {
			joystick.setConfiguration(0, 0, 0);
			return 0.0f;
		} else {
			return joystick.computeAngle();
		}
	}

	@Override
	public float computeMagnitude() {
		if (controllerJoystick.isUsing()) {
			return controllerJoystick.computeMagnitude();
		} else if (wasUsing) {
			joystick.setConfiguration(0, 0, 0);
			return 0.0f;
		} else {
			return joystick.computeMagnitude();
		}
	}

	@Override
	public Vector computeDirection() {
		if (controllerJoystick.isUsing()) {
			return controllerJoystick.computeDirection();
		} else if (wasUsing) {
			joystick.setConfiguration(0, 0, 0);
			return new Vector(0, 0);
		} else {
			return joystick.computeDirection();
		}
	}

	@Override
	public void update() {
		wasUsing = false;
		// Reset the speed slider to the mean speed
		speedSlider.setMean();
		controllerJoystick.updateButtons();
		if (controllerJoystick.isUsing()) {
			controllerJoystick.update();
			wasUsing = true;
		} else {
			joystick.update();
		}
	}

}
