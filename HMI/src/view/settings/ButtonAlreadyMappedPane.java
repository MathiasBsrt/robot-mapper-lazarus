package view.settings;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JLabel;

import com.studiohartman.jamepad.ControllerButton;

import model.controller.ControllerButtonPressedEvent;
import model.controller.ControllerWrapper;
import model.settings.Setting;

public class ButtonAlreadyMappedPane extends JDialog
		implements ControllerButtonPressedEvent, KeyListener, WindowFocusListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3182499973327476767L;

	private static ButtonAlreadyMappedPane instance;
	
	private static boolean isOpen = false;

	private JLabel contentLbl;
	private JLabel closeLbl;

	private ButtonAlreadyMappedPane(ControllerButton button, Setting mappedSetting) {
		super.setTitle("The button is already mapped");

		Box contentBox = Box.createVerticalBox();

		String content = String.format("The button %s is already map to '%s'.", button.toString(),
				mappedSetting.getLabel());
		contentLbl = new JLabel(content);
		contentLbl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		contentLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		contentBox.add(contentLbl);

		closeLbl = new JLabel("(Press any key / button to close)");
		closeLbl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		closeLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		contentBox.add(closeLbl);

		super.add(contentBox);

//		super.setSize(new Dimension(200, 100));
		super.pack();
		super.setVisible(true);
	}
	
	public static void open(ControllerButton button, Setting mappedSetting) {
		if(instance == null) {
			instance = new ButtonAlreadyMappedPane(button, mappedSetting);
		} else {
			instance.contentLbl.setText(String.format("The button %s is already map to '%s'.", button.toString(),
				mappedSetting.getLabel()));
			instance.pack();
			instance.setVisible(true);
		}
		instance.addKeyListener(instance);
		instance.addWindowFocusListener(instance);
		ControllerWrapper.addButtonPressedListener(instance);
		isOpen = true;
	}

	public static void close() {
		if (instance != null) {
			instance.removeKeyListener(instance);
			instance.removeWindowFocusListener(instance);
			ControllerWrapper.removeButtonPressedListener(instance);
			instance.setVisible(false);
			isOpen = false;
		}
	}
	
	public static boolean isOpen() {
		return isOpen;
	}

	@Override
	public void onControllerButtonPressed(ControllerButton button) {
		close();
	}

	@Override
	public void windowGainedFocus(WindowEvent e) {
	}

	@Override
	public void windowLostFocus(WindowEvent e) {
		super.requestFocus();
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		close();
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

}
