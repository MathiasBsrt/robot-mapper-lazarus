package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Timer;

import decision.BluetoothListener;

/**
 * A bluetooth state manager used to know
 * if the application is still connected or not
 * with the robot.
 * @see BluetoothStateListener
 * 
 * @author TRAFNY Theo - UPSSITECH
 *
 */
public class BluetoothStateManager implements BluetoothListener {

	/**
	 * If the application did not received any data from the robot since INTERVAL (in milliseconds),
	 * the application while understand that the connection has been lost.
	 */
	private static final int INTERVAL = 3000;
	
	private long lastReceiveTime = 0;
	
	private List<BluetoothStateListener> listeners = new LinkedList<>();
	
	private boolean wasConnected = false;
	private boolean isConnected = false;
	
	private Timer timer;
	
	public BluetoothStateManager() {
		timer = new Timer(INTERVAL, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				wasConnected = isConnected;
				if(!BluetoothWrapper.isOpen()) {
					isConnected = false;
				} else {
					isConnected = true;
				}
				
				if(wasConnected && !isConnected)
					notifyAllBluetoothStateListener(false);
				else if(!wasConnected && isConnected)
					notifyAllBluetoothStateListener(true);
			}
		});
		timer.setRepeats(true);
	}
	
	@Override
	public void onReceiveData(String data) {
		lastReceiveTime = System.currentTimeMillis();
		if(!timer.isRunning()) {
			timer.start();
		}
	}
	
	/**
	 * Is the bluetooth connection is still alive or not.
	 * @return The bluetooth connection state.
	 */
	public boolean isConnected() {
		return BluetoothWrapper.isOpen();
	}
	
	/**
	 * Add a bluetooth state listener
	 * @param listener
	 */
	public void addBluetoothStateListener(BluetoothStateListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Notify all registered bluetooth state listeners
	 * @param isConnected
	 */
	private void notifyAllBluetoothStateListener(boolean isConnected) {
		for(BluetoothStateListener listener : listeners) {
			listener.onBluetoothStateChange(isConnected);
		}
	}

}
