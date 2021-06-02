package view.splashscreen;

import java.awt.Dimension;

import javax.swing.JFrame;

/**
 * Describes the Splash Screen's window at the opening.
 * 
 * @author TRAFNY Theo - UPSSITECH
 *
 */
public class LazarusSplashScreen extends JFrame implements Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8034163981302630057L;

	/**
	 * The size of the Splash Screen's window.
	 */
	private static final Dimension WINDOW_SIZE = new Dimension(600, 300);

	// Some variables usefull for the thread of this window.
	private Thread thread;
	private boolean running = false;
	
	/**
	 * The main canvas on which we are drawing some components.
	 */
	private SplashScreenPanel canvas;
	
	public LazarusSplashScreen() {
		// Setting up the window, so it can acts
		// like a real Splash Screen.
		super.setMinimumSize(WINDOW_SIZE);
		super.setMaximumSize(WINDOW_SIZE);
		super.setSize(WINDOW_SIZE);
		super.setResizable(false);
		super.setUndecorated(true);
		
		// Set its location at the center of the screen.
		super.setLocationRelativeTo(null);

		// Initialize its canvas.
		canvas = new SplashScreenPanel();
		super.getContentPane().add(canvas);
		
		// Finalize the setup and display it.
		super.pack();
		super.setVisible(false);
	}
	
	/**
	 * Display the Splash Screen
	 */
	public void open() {
		super.setVisible(true);
		initThread();
	}
	
	/**
	 * Display the SplashScreen and wait its closure
	 */
	public void openAndWait() {
		super.setVisible(true);
		initThread();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Wait the closure of the Splash Screen
	 */
	public void waitSplashEnd() {
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Close the Splash Screen
	 */
	public void close() {
		running = false;
		super.setVisible(false);
		super.dispose();
	}
	
	/**
	 * Increments the loading counter.
	 */
	public void incLoading() {
		canvas.incLoading();
	}
	
	/**
	 * Get the serial port name selected by the user.
	 * @return The serial port name.
	 */
	public String getSelectedSerialPortName() {
		return canvas.getSelectedSerialPortName();
	}
	
	/**
	 * Initialize the main thread of the window.
	 */
	private void initThread() {
		thread = new Thread(this);
		running = true;
		thread.start();
	}
	

	@Override
	public void run() {
		while(running) {
			
			repaint();
			
			// If the canvas's status is the last one,
			// we can end this loop and close the Splash Screen.
			if(canvas.getStatus() == SplashScreenPanel.STATUS_END)
				running = false;
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// Close the Splash Screen.
		super.setVisible(false);
		super.dispose();
	}
	
}
