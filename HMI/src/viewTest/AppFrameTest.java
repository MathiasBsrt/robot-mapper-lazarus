package viewTest;


import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import model.OSManager;
import view.AppFrame;

/**
 * Defines the main class with the main method.
 * @author TRAFNY Theo - UPSSITECH
 *
 */
public class AppFrameTest {

	public static void main(String[] args) {
		// Apply the best look according to the
		// current operating system.
		try {
			if (OSManager.isOSLinux()) {
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			} else if (OSManager.isOSWindows()) {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} else if (OSManager.isMacOS()) {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		// Initialize and open the main window.
		AppFrame frame = new AppFrame();
	}

}
