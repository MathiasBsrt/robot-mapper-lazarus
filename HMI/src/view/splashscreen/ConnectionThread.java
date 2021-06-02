package view.splashscreen;

import java.util.function.Consumer;

import javax.swing.JOptionPane;

import commande.CommandeForward;
import decision.BluetoothListener;
import jssc.SerialPortException;
import model.BluetoothWrapper;

public class ConnectionThread extends Thread {

	public interface ConnectionCallback {
		 public void accept(boolean connectionAgreed);
	}
	
	private String portName;
	private ConnectionCallback callback;
	
	public void connect(String portName, ConnectionCallback callback) {
		this.portName = portName;
		this.callback = callback;
		start();
	}
	
	public void connectAndWait(String portName, ConnectionCallback callback) {
		connect(portName, callback);
		try {
			join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			BluetoothWrapper.connect(portName);
//			BluetoothWrapper.sendData(new CommandeForward());
			
//			BluetoothWrapper.addBluetoothListener(new BluetoothListener() {
//				@Override
//				public void onReceiveData(String data) {
//					System.out.println("Receive data: " + data);
//				}
//			});
			callback.accept(true);
		} catch (SerialPortException e) {
			JOptionPane.showMessageDialog(null, "Can not connect to " + portName + ".", "Connection error", JOptionPane.ERROR_MESSAGE);
			callback.accept(false);
		} catch (InterruptedException e) {}
	}
	
}
