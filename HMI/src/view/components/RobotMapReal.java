package view.components;

import java.awt.Graphics;

import bluetooth.CommunicatorV2;
import commande.CommandeListener;
import decision.BluetoothListener;
import decision.RoomMap;
import decision.StatusMessage;
import model.theme.ThemeManager;
import model.theme.ThemeValue;
import view.components.controls.MouseAndControllerJoystick;
import view.components.controls.MouvementInputs;
import view.components.controls.SpeedSlider;

public class RobotMapReal extends RobotMap {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4989467895443204174L;
	
	private CommunicatorV2 communicator = CommunicatorV2.getInstance();
	private RoomMap roomMap;
	
	private int messageCount = 0;
	private int messageCountPerSecond = 0;
	private long lastMessageTime = 0;

	private StatusMessage currentStatusMessage;
	private StatusMessage lastStatusMessage;
	
	private MouvementInputs mouvementInputs;
	
	public RobotMapReal(Joystick joystick, SpeedSlider speedSlider, ToggleButtonImage performanceButton,
			ToggleSwitch autonomousCartographySwitch, ImageIndicator robotIsRunningIndicator,
			ImageIndicator robotStoppedIndicator) {
		super(robotIsRunningIndicator, robotStoppedIndicator);
		this.mouvementInputs = new MouseAndControllerJoystick(joystick, speedSlider, performanceButton, this,
				autonomousCartographySwitch);
		roomMap = new RoomMap();
		
//		communicator.addCommandeListener(roomMap);
		
		communicator.addBluetoothListener(new BluetoothListener() {
			@Override
			public void onReceiveData(String data) {
				messageCount++;
			}
		});
		
		communicator.addCommandeListener(new CommandeListener() {
			@Override
			public void onReceiveData(StatusMessage statusMessage) {
				if(statusMessage != null)  {
					System.out.println(statusMessage);
					currentStatusMessage = statusMessage;
				}
			}
		});
	}

	@Override
	public void beforeDrawing() {
		mouvementInputs.update();
		if(currentStatusMessage == null) return;
		if(lastStatusMessage != currentStatusMessage) {
			robot.update();
		}
		lastStatusMessage = currentStatusMessage;
		robot.location.x = currentStatusMessage.getX();
		robot.location.y = currentStatusMessage.getY();
		
	
		robot.rotation = currentStatusMessage.getRot();
		
		if(currentStatusMessage.getS1Dist() <= 250) {
			robot.setSensorLength(Robot.FRONT_SENSOR, currentStatusMessage.getS1Dist());
			robot.activateSensor(Robot.FRONT_SENSOR);
		} else {
			robot.setSensorLength(Robot.FRONT_SENSOR, 250);
			robot.desactivateSensor(Robot.FRONT_SENSOR);
		}
		
		if(currentStatusMessage.getS2Dist() <= 400) {
			robot.setSensorLength(Robot.RIGHT_FRONT_SENSOR, currentStatusMessage.getS2Dist());
			robot.activateSensor(Robot.RIGHT_FRONT_SENSOR);
		} else {
			robot.setSensorLength(Robot.RIGHT_FRONT_SENSOR, 400);
			robot.desactivateSensor(Robot.RIGHT_FRONT_SENSOR);
		}
		
		if(currentStatusMessage.getS3Dist() <= 400) {
			robot.setSensorLength(Robot.RIGHT_BACK_SENSOR, currentStatusMessage.getS3Dist());
			robot.activateSensor(Robot.RIGHT_BACK_SENSOR);
		} else {
			robot.setSensorLength(Robot.RIGHT_BACK_SENSOR, 400);
			robot.desactivateSensor(Robot.RIGHT_BACK_SENSOR);
		}
	}

	@Override
	public void setConfiguration(float x, float y, float angle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeDrawing(Graphics g) {
		g.setColor(ThemeManager.get(ThemeValue.TEXT));
		int textHeight = g.getFontMetrics().getHeight();
		g.drawString("Bluetooth message received: " + String.format("%d per s", messageCountPerSecond), 200, getHeight() - textHeight);
		if(currentStatusMessage != null)
			g.drawString(currentStatusMessage.toString(), 200, getHeight() - 2 * textHeight);
		if(System.currentTimeMillis() - lastMessageTime >= 1000) {
			messageCountPerSecond = messageCount;
			messageCount = 0;
			lastMessageTime = System.currentTimeMillis();
		}
	}


}
