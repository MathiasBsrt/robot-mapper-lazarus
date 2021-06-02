package view.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;

import model.Vector;

/**
 * Represents the help box of the feature "Help Hoverable" objects.
 * You can see {@link HelpHoverable} to know more about
 * this feature.
 * 
 * @author TRAFNY Theo - UPSSITECH
 *
 */
public class HelpPopup extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5017044958976757295L;

	/**
	 * The font used to display the help message.
	 */
	private static final Font FONT = new Font("Arial", Font.PLAIN, 12);
	
	/**
	 * The current location of the popup (top left corner).
	 * This is the x component.
	 */
	private int x;
	/**
	 * The current location of the popup (top left corner).
	 * This is the y component.
	 */
	private int y;
	
	/**
	 * The message to print.
	 */
	private String[] messages;
	/**
	 * Width of the message using the font FONT.
	 */
	private int messageWidth;
	/**
	 * Height of the message using the font FONT.
	 */
	private int messageHeight;
	/**
	 * Height of a single line of a message using the font FONT.
	 */
	private int singleMessageHeight;
	
	/**
	 * Direction where the triangle shape need to follow.
	 */
	private Point pointer;
	
	/**
	 * Center of the message box relative to its top left corner.
	 */
	private Vector centerBounds;
	
	/**
	 * Center of the message box relative to the screen.
	 */
	private Vector centerScreen;
	/**
	 * Vector Center (centerScreen) to Pointer (pointer).
	 */
	private Vector centerToPointer;
	
	/**
	 * The rectangle shape is the main shape of the help popup.
	 * This is the rounded rectangle shape.
	 */
	private RoundRectangle2D.Double rectangleShape;
	
	/**
	 * The triangle shape.
	 */
	private Path2D.Double triangleShape;
	
	/**
	 * Create a message box (a popup style message that needs to be added on the highest layer of
	 * a JLayeredPane).
	 * With this constructor, the message box will indicates the specified coordinates.
	 * @param component Component that the message box indicates
	 * @param message Message of the component
	 */
	public HelpPopup(int x, int y, String message) {
		this.x = x;
		this.y = y;
		this.messages = message.split("\n");
		// Setup a default pointer on the bottom right
		this.pointer = new Point(10000, 10000);

		setPosition(x, y);

		// Init the rectangle's shape
		initRectangleShape();
	
		// Update center positions
		updatePositions();
		
		// Init the triangle's shape
		initTriangleShape();
	}
	
	/**
	 * Create a message box (a popup style message that needs to be added on the highest layer of
	 * a JLayeredPane).
	 * With this constructor, the message box will indicates the specified component.
	 * @param component Component that the message box indicates
	 * @param message Message of the component
	 */
	public HelpPopup(Component component, String message) {
		this((int) component.getBounds().getCenterX(), (int) component.getBounds().getCenterY(), message);
		Rectangle bounds = component.getBounds();
		setPointer((int) bounds.getCenterX(), (int) bounds.getCenterY());
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		initTriangleShape();
		g2d.setFont(FONT);
		g2d.setColor(Color.WHITE);
		g2d.fill(rectangleShape);
		g2d.setColor(Color.BLACK);
		g2d.draw(rectangleShape);
		g2d.setColor(Color.WHITE);
		g2d.fill(triangleShape);
		g2d.setColor(Color.BLACK);
		g2d.draw(triangleShape);
		for(int i = 0; i < messages.length; i++) {
			g2d.drawString(messages[i], 30, (int) rectangleShape.getY() + singleMessageHeight * (i + 1) * 0.8f);
		}
		
//		debugDraw(g2d);
		
		g2d.dispose();
	}
	
	public void setPosition(int x, int y) {
		// Needs to initialize the bounds to be drawn by the JLayeredPane.
		// JLayeredPane has a null LayoutManager, which set initial bounds to (0, 0, 0, 0).
		// With those bounds, the popup could not be shown.
		// Before that, we need to fetch the width and height of the specified message.
		AffineTransform affinetransform = new AffineTransform();     
		FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
		messageWidth = 0;
		messageHeight = 0;
		singleMessageHeight = 0;
		int messageWidthLocal, messageHeightLocal;
		for(String message : messages) {
			Rectangle2D stringBounds = FONT.getStringBounds(message, frc);
			messageWidthLocal = (int) stringBounds.getWidth() + 10;
			messageHeightLocal = (int) stringBounds.getHeight() + 10;
			messageWidth = Math.max(messageWidth, messageWidthLocal);
			messageHeight += messageHeightLocal;
			singleMessageHeight = Math.max(singleMessageHeight, messageHeightLocal);
		}
		x -= messageWidth - 20;
		y -= messageHeight + 30;
		super.setBounds(x, y, messageWidth + 50, messageHeight + 50);
		initRectangleShape();
		updatePositions();
	}
	
	public void setPointer(int x, int y) {
		this.pointer = new Point(x, y);
		centerToPointer = centerScreen.to(new Vector(pointer.x, pointer.y));
		initTriangleShape();
	}
	
	public void setMessage(String message) {
		if(message == null) message = "";
		this.messages = message.split("\n");
		// Re-call setPosition to update its size and location.
		setPosition(x, y);
		initRectangleShape();
		updatePositions();
		initTriangleShape();
	}
	
	private void debugDraw(Graphics2D g2d ) {
		Vector dir = centerToPointer.normalize()._scale(30);
		g2d.setColor(Color.MAGENTA);
		g2d.drawLine((int) centerBounds.x, (int) centerBounds.y, (int) (centerBounds.x + dir.x), (int) (centerBounds.y + dir.y));
		g2d.fillOval((int) centerBounds.x - 3, (int) centerBounds.y - 3, 6, 6);
	}
	
	/**
	 * Update positions
	 */
	private void updatePositions() {
		Rectangle bounds = rectangleShape.getBounds();
		// Center of the message box
		centerBounds = new Vector((float) bounds.getCenterX(), (float) bounds.getCenterY());
		centerScreen = centerBounds._add(getX(), getY());
		centerToPointer = centerScreen.to(new Vector(pointer.x, pointer.y));
	}
	
	/**
	 * Init the rectangle's shape of the message box
	 */
	private void initRectangleShape() {
		rectangleShape = new RoundRectangle2D.Double(25, 25, getWidth() - 50, getHeight() - 50, 10, 10);
	}
	
	/**
	 * Init the triangle's shape of the message box.
	 * The triangle represent what the message box deals with.
	 */
	private void initTriangleShape() {
		// Init the 2D Path
		triangleShape = new Path2D.Double();
		
		// Create the bottom line (from bottom left to bottom right)
		Vector bottomLeft = new Vector(25, getHeight() - 25);
		// Create its direction
		Vector dir1 = new Vector(1, 0);
		
		// Compute the intersection point between the bottom line and the "center -> pointer" line.
		// As a reminder, the pointer variable handle the position (relative to the screen)
		// of the point where the message box needs to indicate.
		Vector intersection = Vector.intersectionPoint(bottomLeft, dir1, centerBounds, centerToPointer);
		
		// Here we start to create the shape. So first we move to the intersection point.
		triangleShape.moveTo(intersection.x, intersection.y);
		
		// Next we draw a line to a point a little bit more far in the direction of the pointer.
		Vector point2 = intersection._add(centerToPointer.normalize()._scale(10));
		triangleShape.lineTo(point2.x, point2.y);
		
		// Then we go back to a point a little bit on the right of the first one.
		Vector point3 = intersection._add(dir1.normalize()._scale(10));
		triangleShape.lineTo(point3.x, point3.y);
	}

}
