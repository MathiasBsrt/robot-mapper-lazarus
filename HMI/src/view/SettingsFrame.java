package view;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import view.settings.SettingsTable;

/**
 * Defines the settings window
 * @author TRAFNY Theo - UPSSITECH
 *
 */
public class SettingsFrame extends JFrame {

	/**
	 * The title of the settings window.
	 */
	public static final String TITLE = "Settings";
	/**
	 * The size of the settings window.
	 */
	public static final Dimension WINDOW_SIZE = new Dimension(600, 400);
	
	/**
	 * As this object is a singleton, we store its own instance here.
	 */
	private static SettingsFrame instance;
	
	/**
	 * Is this window currently open and visible ?
	 */
	private static boolean isOpen = false;
	
	/**
	 * A reference to the settings table.
	 */
	private SettingsTable settingsTable;

	private SettingsFrame() {
		// Setting up the window.
		super.setTitle(TITLE);
		super.setResizable(false);
		
		// Initalizing the setting table and adding it to the window.
		settingsTable = new SettingsTable();
		JScrollPane scrollPane = new JScrollPane(settingsTable);
		super.add(scrollPane);
		
		// Finalizing the setup of the window and display it.
		super.setPreferredSize(WINDOW_SIZE);
		super.setMaximumSize(WINDOW_SIZE);
		super.pack();
		super.setVisible(true);
	}
	
	/**
	 * Display the settings window.
	 */
	public static void open() {
		if(instance == null) {
			instance = new SettingsFrame();
		} else {
			instance.setVisible(true);
		}
		isOpen = true;
	}
	
	/**
	 * Hide the settings window.
	 */
	public static void close() {
		if(instance != null) {
			instance.setVisible(false);
			instance.settingsTable.stopEditing();
			isOpen = false;
		}
	}
	
	/**
	 * Toggle the visibility of the settings window.
	 */
	public static void toggle() {
		if(isOpen) {
			close();
		} else open();
	}
	
	/**
	 * Is the settings window visible or not ?
	 * @return boolean
	 */
	public static boolean isOpen() {
		return isOpen;
	}
	
}
