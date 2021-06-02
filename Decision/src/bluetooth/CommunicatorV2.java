package bluetooth;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.plaf.basic.BasicBorders.MarginBorder;

import commande.*;
import decision.BluetoothListener;
import decision.StatusMessage;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class CommunicatorV2 {

	public final static long INTERVAL_SLOW = 10;
	public final static long INTERVAL_NORMAL = 30;
	public final static long INTERVAL_HIGH = 50;
	public final static long INTERVAL_SPAM = 100;

	// Singleton
	private CommunicatorV2() {
		buffer = new String();
		lastStatus = null;
	}

	private static class CommunicatorHolder {
		private final static CommunicatorV2 instance = new CommunicatorV2();
	}

	public static CommunicatorV2 getInstance() {
		return CommunicatorHolder.instance;
	}

	// The serial port that represent the connected peripheral
	private SerialPort serialPort;
	private long lastSentTime = 0;

	// The buffer used to treat the raw data;
	private String buffer;

	// The last status of the robot used to check the margins
	private float[] lastStatus;

	private static boolean isConnected = false;
	private String selectedPort;

	private List<BluetoothListener> listeners = new LinkedList<>();
	private List<CommandeListener> commandeListeners = new LinkedList<>();

	// Margins
	private float frontSensorMargin;
	private float frontLeftMargin;
	private float rearLeftMargin;
	private float xMargin;
	private float yMargin;
	private float angleMargin;

	public void setMargins(float frontSensorMargin, float frontLeftMargin, float rearLeftMargin, float xMargin,
			float yMargin, float angleMargin) {
		this.frontSensorMargin = frontSensorMargin;
		this.frontLeftMargin = frontLeftMargin;
		this.rearLeftMargin = rearLeftMargin;
		this.xMargin = xMargin;
		this.yMargin = yMargin;
		this.angleMargin = angleMargin;
	}

	/**
	 * Get the entire COM port list
	 * 
	 * @return String[] Return the Serial port list
	 */
	public String[] searchForPorts() {
		return (SerialPortList.getPortNames());
	}

	/**
	 * Select the right port
	 * 
	 * @param port The selected port
	 */
	public void selectPort(String port) {
		selectedPort = port;
	}

	/**
	 * Add a listener to the bluetooth input
	 * 
	 * @param listener
	 */
	public void addBluetoothListener(BluetoothListener listener) {
		listeners.add(listener);
	}

	/**
	 * Send the raw data received from the robot to the listeners
	 * 
	 * @param data
	 */
	private void notifyAllBluetoothListener(String data) {
		for (BluetoothListener listener : listeners) {
			listener.onReceiveData(data);
		}
	}

	/**
	 * Add a listener to the bluetooth input
	 * 
	 * @param listener
	 */
	public void addCommandeListener(CommandeListener listener) {
		commandeListeners.add(listener);
	}

	/**
	 * Send the formatted data received from the robot to the listeners
	 * 
	 * @param statusMessage
	 */
	public void notifyAllCommandeListener(StatusMessage statusMessage) {
		for (CommandeListener commande : commandeListeners) {
			commande.onReceiveData(statusMessage);
		}
	}

	/**
	 * Send the data to the robot through the bluetooth connection
	 * 
	 * @param c The command you want to send the robot
	 */
	public void sendData(Commande c) throws SerialPortException {
		if (serialPort == null && isConnected) {
			System.err.println("Connection is not established");
		} else {
			serialPort.writeBytes(c.getBytes());
		}
	}

	/**
	 * Send the data to the robot through the bluetooth connection with an interval
	 * 
	 * @param c
	 * @param interval
	 * @throws SerialPortException
	 */
	public void sendData(Commande c, long interval) throws SerialPortException {
		if (System.currentTimeMillis() - lastSentTime >= interval) {
			sendData(c);
			lastSentTime = System.currentTimeMillis();
		}
	}

	/**
	 * purge the serial port
	 */
	public void purgeRX() {
		try {
			serialPort.purgePort(SerialPort.PURGE_RXCLEAR);
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Tell if the port is opened
	 * @return boolean
	 */
	public boolean isOpen() {
		return serialPort.isOpened();
	}
	
	/**
	 * Transform the received data from the robot into a CommandReception
	 * 
	 * @param receivedData
	 * @return
	 */
	public StatusMessage receiveData(String receivedData) {
		String[] parsedString = receivedData.split(" ");
		// Single command like "C"
		if (parsedString.length == 1) {
			return null;
			// Command with params
		} else {
			float[] data = new float[6];
			parsedString = Arrays.copyOfRange(parsedString, 1, parsedString.length);
			if (parsedString.length == 6) {
				for (int i = 0; i < parsedString.length; i++) {
					data[i] = Float.parseFloat(parsedString[i]);
				}
				if (data[1] != 0 && data[2] != 0) {
					lastStatus = data.clone();
					StatusMessage receivedCommande = new StatusMessage(data[3], data[4], data[5], data[0], data[1],
							data[2]);
					return receivedCommande;
				}
			}
		}
		return null;
	}

	/**
	 * Reset the lastStatus received from the robot
	 */
	public void wipeLastStatus() {
		lastStatus = null;
	}

	/**
	 * Compare values between 2 arrays according to margins.
	 * 
	 * @param tabA Array of 6 values
	 * @param tabB Array of 6 values
	 */
	public boolean checkMargin(float[] tabA, float[] tabB) {
		if ((Math.abs(tabA[0] - tabB[0]) > frontSensorMargin) || Math.abs(tabA[1] - tabB[1]) > frontLeftMargin
				|| Math.abs(tabA[2] - tabB[2]) > rearLeftMargin || Math.abs(tabA[3] - tabB[3]) > xMargin
				|| Math.abs(tabA[4] - tabB[4]) > yMargin || Math.abs(tabA[5] - tabB[5]) > angleMargin)
			return false;
		return true;
	}

	/**
	 * Receive the raw data and transform it to a String containing the 6 importants
	 * values
	 * 
	 * @param receivedString the raw data
	 * @return
	 */
	public String bufferizer(String receivedString) {
		buffer += receivedString;
		if(buffer.contains("A") && buffer.contains("Z")) {
			if (buffer.indexOf('Z') > buffer.indexOf('A')) {
				String dataOK = buffer.substring(buffer.indexOf('A') + 1, buffer.indexOf('Z'));
				// System.out.println(dataOK);
				buffer = buffer.substring(buffer.indexOf('Z'), buffer.length());
				buffer="";
				return (dataOK);
			} else {
				buffer = buffer.substring(buffer.indexOf('A'));
			}
		}
		return null;
	}

	/**
	 * 
	 * Initialize the connection between the computer and the specified COM Port
	 * (here, the robot) Wait for a first reception from the robot to establish the
	 * connection
	 * 
	 */
	public void initCOMPort() throws SerialPortException, InterruptedException {
		if (!isConnected) {
			if (selectedPort != null) {

				serialPort = new SerialPort(selectedPort);
				serialPort.openPort();

				serialPort.setParams(SerialPort.BAUDRATE_115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE);

				serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);

				serialPort.addEventListener(new SerialPortEventListener() {

					@Override
					public void serialEvent(SerialPortEvent event) {
						String receivedData;
						if (event.isRXCHAR() && event.getEventValue() > 0) {
							if (!isConnected) { // Handle the first connection

								try {
									receivedData = serialPort.readString(event.getEventValue());
									isConnected = true;
								} catch (SerialPortException e) {
									e.printStackTrace();
								}
							} else { // Get the sensors data
								try {
									receivedData = serialPort.readString(event.getEventValue());
									String formattedString = bufferizer(receivedData);
									if (formattedString != null) {
										notifyAllBluetoothListener(receivedData);
										notifyAllCommandeListener(receiveData(formattedString));
									}

								} catch (SerialPortException e) {
									e.printStackTrace();
								}
							}
						}

					}
				}, SerialPort.MASK_RXCHAR);
				while (!isConnected) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				System.out.println("¡ Connexion established !");

			} else {
				System.err.println("Please select a port to open first.");
			}
		} else {
			System.err.println("The COM port is already connected !");
		}

	}

	/**
	 * Disconnect the COM port (here, the robot) from the computer
	 */

	public void disconnect() throws SerialPortException {
		serialPort.closePort();
		isConnected = false;
		System.out.println("Disconnected successfully.");
	}

}
