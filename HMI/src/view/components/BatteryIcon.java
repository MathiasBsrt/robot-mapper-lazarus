package view.components;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import model.theme.ThemeManager;
import model.theme.ThemeValue;

/**
 * This class represents the battery icon on the graphic interface.
 * This class is completely usable, but the robot can not fetch the battery level.
 * So the battery level will always be at 100%.
 * 
 * @author TRAFNY Theo - UPSSITECH
 *
 */
public class BatteryIcon extends JPanel implements HelpHoverable {

	private static final long serialVersionUID = -2356628835314709903L;

	/**
	 * The stroke weight of the outline.
	 */
	private static final int STROKE_WEIGHT = 10;
	
	/**
	 * The current battery level specified as a percentage from 0% to 100%.
	 */
	private float percentage;
	
	public BatteryIcon(float percentage) {
		this.percentage = percentage;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(ThemeManager.get(ThemeValue.BACKGROUND));
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.setColor(Color.BLACK);
		g.fillRoundRect(0, 0, getWidth() - 5, getHeight(), 10, 10);
		g.fillRoundRect(getWidth() - 10, 9, 10, getHeight() - 15, 10, 10);
		
		g.setColor(ThemeManager.get(ThemeValue.BACKGROUND_BATTERY));
		g.fillRoundRect(STROKE_WEIGHT / 2, STROKE_WEIGHT / 2, getWidth() - STROKE_WEIGHT - 5, getHeight() - STROKE_WEIGHT, 10, 10);
		
		int nbToDraw = Math.round(percentage / 20);
		for(int i = 0; i < nbToDraw; i++)
			drawBatteryBar(g, i);
	}
	
	/**
	 * Draw the index th battery bar.
	 * @param g The current graphic context.
	 * @param index The id of the battery bar to draw.
	 */
	private void drawBatteryBar(Graphics g, int index) {
		int width = (getWidth() - STROKE_WEIGHT - 20) / 4 - 3;
		int height = getHeight() - STROKE_WEIGHT - 4;
		int x = STROKE_WEIGHT / 2 + 3 + index * width + 2 * index;
		int y = STROKE_WEIGHT / 2 + 2;
		
		g.setColor(computeColor());
		g.fillRoundRect(x, y, width, height, 5, 5);
	}
	
	/**
	 * Get the current battery bar color based on the
	 * battery level (percentage).
 	 * @return The color based on the battery level.
	 */
	private Color computeColor() {
		if(percentage > 30) return ThemeManager.get(ThemeValue.BATTERY_FULL);
		else if(percentage > 15) return ThemeManager.get(ThemeValue.BATTERY_LOW);
		return ThemeManager.get(ThemeValue.BATTERY_EMPTY);
	}
	
	/**
	 * Set the current battery level (percentage).
	 * @param percentage
	 */
	public void setPercentage(float percentage) {
		this.percentage = percentage;
		repaint();
	}

	@Override
	public String getHelpMessage() {
		return "Battery\nCharge: " + String.format("%.0f%%", percentage) + "\n\nNot usable";
	}

	@Override
	public void setHelpMessage(String message) {
		// As this class can only be used as a battery icon,
		// we can let the static help message set in
		// the method getHelpMessage() above.
	}
	
}
