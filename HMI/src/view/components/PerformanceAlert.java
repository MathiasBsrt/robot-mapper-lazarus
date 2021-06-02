package view.components;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.studiohartman.jamepad.ControllerButton;

import model.controller.ControllerButtonPressedEvent;
import model.controller.ControllerWrapper;

public class PerformanceAlert extends JFrame implements KeyListener, WindowFocusListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6563427292670239020L;

	private static boolean isOpen = false;
	
	private static PerformanceAlert instance;
	
	private PerformanceAlert() {
		isOpen = true;
		super.setUndecorated(true);
		super.setTitle("Wait ...");
		Box box = Box.createVerticalBox();
		box.add(centerComponent(new JLabel("You are now getting into performance mode ...")));
		box.add(Box.createVerticalStrut(10));
		box.add(centerComponent(new JLabel("Enjoy !")));
		box.add(centerComponent(new JLabel("Press any key to continue")));
		
		box.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		super.add(box);
		super.pack();
		super.requestFocus();
		super.setVisible(true);
		
		super.addKeyListener(this);
		super.addWindowFocusListener(this);
		
		ControllerWrapper.addButtonPressedListener(new ControllerButtonPressedEvent() {
			@Override
			public void onControllerButtonPressed(ControllerButton button) {
				if(isOpen) {
					close();
				}
			}
		});
	}
	
	public static boolean alreadyOneIsOpen() {
		return isOpen;
	}
	
	public static void open(int x, int y) {
		if(instance == null) instance = new PerformanceAlert();
		instance.setLocation(x - instance.getWidth() / 2, y - instance.getHeight() / 2);
		instance.setVisible(true);
		isOpen = true;
	}
	
	public static void close() {
		if(instance != null) {
			instance.setVisible(false);
		}
		isOpen = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		close();
	}

	@Override
	public void keyReleased(KeyEvent e) {}
	
	private Component centerComponent(Component component) {
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		box.add(component);
		box.add(Box.createHorizontalGlue());
		return box;
	}

	@Override
	public void windowGainedFocus(WindowEvent e) {}

	@Override
	public void windowLostFocus(WindowEvent e) {
		super.requestFocus();
	}

}
