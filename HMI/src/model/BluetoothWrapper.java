package model;

import java.util.Arrays;

import bluetooth.CommunicatorV2;
import commande.Commande;
import commande.CommandeParams;
import decision.BluetoothListener;
import jssc.SerialPortException;

/**
 * A Bluetooth Wrapper used to wrap some features of
 * the bluetooth communicator of the Decision Team.
 * @author TRAFNY Theo - UPSSITECH
 *
 */
public class BluetoothWrapper {

	/**
	 * The communicator needed to send and receive data.
	 */
	private static CommunicatorV2 comunicator;
	
	/**
	 * The selected Serial Port name by the user.
	 */
	private static String serialPortName;
	
	/**
	 * An instance of the bluetooth state manager, used to know the
	 * current connection state of the bluetooth with the robot.
	 */
	private static BluetoothStateManager stateManager;
	
	/**
	 * Check and initialize all needed variables.
	 */
	private static void init() {
		if(comunicator == null) {
			comunicator = CommunicatorV2.getInstance();
			stateManager = new BluetoothStateManager();
			addBluetoothListener(stateManager);
		}
	}
	
	/**
	 * Connect to a Serial Port specified by its name.
	 * @param portName The Serial Port name
	 * @throws SerialPortException
	 * @throws InterruptedException
	 */
	public static void connect(String portName) throws SerialPortException, InterruptedException {
		init();
		comunicator.selectPort(portName);
		comunicator.initCOMPort();
		serialPortName = portName;
	}
	
	/**
	 * Disconnect the bluetooth connection with the robot.
	 * @throws SerialPortException
	 */
	public static void disconnect() throws SerialPortException {
		init();
		comunicator.disconnect();
	}
	
	/**
	 * Send a command to the robot.
	 * @param command The command to send
	 */
	public static void sendData(Commande commande) {
		init();
		try {
			comunicator.sendData(commande);
			System.out.println("Sent " + commande.toString());
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send a command to the robot, but if another command
	 * has been sent 'wait' (milliseconds) ago, the command will not be sent.
	 * @param commande The command to send.
	 * @param wait The interval between two send.
	 */
	public static void sendData(CommandeParams commande, long wait) {
		init();
		try {
			comunicator.sendData(commande, wait);
			System.out.println("Sent " + commande.toString());
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Register a bluetooth listener.
	 * @param listener
	 */
	public static void addBluetoothListener(BluetoothListener listener) {
		init();
		comunicator.addBluetoothListener(listener);
	}
	
	/**
	 * Register a bluetooth state listener. 
	 * @param listener
	 */
	public static void addBluetoothStateListener(BluetoothStateListener listener) {
		stateManager.addBluetoothStateListener(listener);
	}
	
	/**
	 * List all available serial ports name.
	 * @return
	 */
	public static String[] searchForPorts() {
		init();
		return comunicator.searchForPorts();
	}
	
	/**
	 * Returns the serial port selected by the user.
	 * @return
	 */
	public static String getConnectedSerialPortName() {
		return serialPortName;
	}
	
	/**
	 * Return the bluetooth connection state (connected or not).
	 * @return The bluetooth connection state
	 */
	public static boolean isConnected() {
		return stateManager.isConnected();
	}
	
	public static boolean isOpen() {
		return comunicator.isOpen();
	}
	
}
