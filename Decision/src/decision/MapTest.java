package decision;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import javax.swing.*;

public class MapTest {
	JFrame frame = new JFrame("Map");
	RoomMap rMap = new RoomMap();
	BufferedImage img = new BufferedImage(1000, 1000, BufferedImage.TYPE_BYTE_GRAY);
	
	public static void main(String[] args) throws InterruptedException {
		MapTest test = new MapTest();
		test.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		test.frame.setVisible(true);
		
		test.step(new StatusMessage(0, 0, 0, 1, 1, 1));
		TimeUnit.SECONDS.sleep(1);
		test.step(new StatusMessage(1, 1, 0, 1, 1, 1));
		TimeUnit.SECONDS.sleep(1);
		test.step(new StatusMessage(1, 1, (float)Math.PI / 2, 1, 1, 1));
		TimeUnit.SECONDS.sleep(1);
		test.step(new StatusMessage(10, 1, (float)Math.PI / 2, 1, 1, 1));
	}

	public void step(StatusMessage m) {
		rMap.onReceiveData(m);
		double scale = (double)img.getHeight() / rMap.getImage().getHeight();
		AffineTransform trans = new AffineTransform(scale, 0, 0, scale, 0, 0);
		((Graphics2D)img.getGraphics()).drawImage(rMap.getImage(), trans, null);
		frame.getContentPane().add(new JLabel(new ImageIcon(img)));
		frame.pack();
	}
	
}
