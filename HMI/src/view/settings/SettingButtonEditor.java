package view.settings;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JPanel;

import com.studiohartman.jamepad.ControllerButton;

public abstract class SettingButtonEditor extends JPanel {
	
	private String label;

	private boolean isSelected = false;
	private boolean isButtonPressed = false;
	private boolean isClicked = false;
	
	public SettingButtonEditor(String label) {
		this.label = label;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paintComponent(g);
		if(isClicked) {
			drawAfterAClick();
			return;
		}
		if(isButtonPressed) {
			drawAfterButtonPressed();
			return;
		}
		if(isSelected) {
			g.setColor(new Color(0, 255, 255, 100));
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		g.setColor(Color.BLACK);
		
		int textWidth = g.getFontMetrics().stringWidth(label);
		int textHeight = g.getFontMetrics().getHeight();
		
		g.drawString(label, getWidth() / 2 - textWidth / 2, getHeight() / 2 + 2 * textHeight / 5);
	}
	
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
		repaint();
	}
	
	public String getLabel() {
		return label;
	}
	
	public boolean isSelected() {
		return isSelected;
	}
	
	public void doClick() {
		isClicked = true;
	}
	public abstract void onClick();
	public abstract void drawAfterAClick();
	
	public void doButtonPressed() {
		isButtonPressed = true;
	}
	public abstract void onButtonPressed();
	public abstract void drawAfterButtonPressed();
	
	public void resetState() {
		isSelected = false;
		isButtonPressed = false;
		isClicked = false;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public static SettingButtonEditor createFromClass(Class<?> clazz, String label) {
		if(clazz.equals(double.class)) return new DoubleSettingButtonEditor(label);
		if(clazz.equals(ControllerButton.class)) return new EnumSettingButtonEditor(label);
		return null;
	}
	
}
