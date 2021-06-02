package model;

/**
 * An event listener interface used to listen
 * new states of the bluetooth connection.
 * @author TRAFNY Theo - UPSSITECH
 *
 */
public interface BluetoothStateListener {

	/**
	 * Called when a the bluetooth connection state has changed.
	 * @param connected
	 */
	public void onBluetoothStateChange(boolean connected);
	
}
