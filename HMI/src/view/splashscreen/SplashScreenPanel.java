package view.splashscreen;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import bluetooth.CommunicatorV2;
import commande.CommandeForward;
import decision.BluetoothListener;
import model.BluetoothWrapper;

public class SplashScreenPanel extends JPanel {

	public static final int STATUS_END = 3;
	
	private static final Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);
	private static Font TITLE_FONT;
	private static Font NORMAL_FONT = new Font("Arial", Font.PLAIN, 30);
	
	private static final Paint[] PAINTS = new Paint[] { 
			new GradientPaint(0, 0, Color.white, 50, 50, Color.GRAY),
			new GradientPaint(50, 50, Color.white, 100, 100, Color.GRAY),
			new GradientPaint(100, 100, Color.white, 150, 150, Color.GRAY),
			new GradientPaint(150, 150, Color.white, 200, 200, Color.GRAY),
			new GradientPaint(150, 150, Color.white, 250, 250, Color.GRAY),
			new GradientPaint(150, 150, Color.white, 300, 300, Color.GRAY),
			new GradientPaint(150, 150, Color.white, 250, 250, Color.GRAY),
			new GradientPaint(150, 150, Color.white, 200, 200, Color.GRAY),
			new GradientPaint(100, 100, Color.white, 150, 150, Color.GRAY),
			new GradientPaint(50, 50, Color.white, 100, 100, Color.GRAY),
			new GradientPaint(0, 0, Color.white, 50, 50, Color.GRAY)
	};
	private static double paintCounter = 0;
	
	private static final String TITLE = "Lazarus Bot";

	private static final String[] COMPONENTS = new String[] { 
			"Loading Theme Manager", 
			"Starting Program Settings",
			"Initializing Game Controller Wrapper", 
			"Loading Application Layout" 
	};
	
	/**
	 * Status 0: Loading components
	 * Status 1: User needs to select a serial port
	 */
	private int status = 0;
	
	private int loadingStatus = 1;
	
	private Image backgroundImage;

	private Box verticalBox = Box.createVerticalBox();
	private JProgressBar progressBar;
	private JLabel progressLabel;

	private JComboBox<String> cbSerialPorts;
	private Box closeBox;
	
	private ConnectionThread connectionThread;
	
	public SplashScreenPanel() {
		try {
			TITLE_FONT = Font.createFont(Font.TRUETYPE_FONT, new File("res/fonts/Xolonium.otf")).deriveFont(40f);
		} catch (FontFormatException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		NORMAL_FONT = TITLE_FONT.deriveFont(20f);
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    ge.registerFont(TITLE_FONT);
		verticalBox.add(Box.createVerticalStrut(120));
		
		progressBar = new JProgressBar(0, COMPONENTS.length);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setPreferredSize(new Dimension(200, 20));
		progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
		verticalBox.add(progressBar);
		verticalBox.add(Box.createVerticalStrut(50));
		
		progressLabel = new JLabel("");
		progressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		verticalBox.add(progressLabel);
		
		cbSerialPorts = new JComboBox<>();
		cbSerialPorts.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		verticalBox.add(Box.createVerticalGlue());
		
		
		closeBox = Box.createHorizontalBox();
		JButton closeButton = new JButton("Close");
		closeButton.setFont(NORMAL_FONT);
		closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		closeButton.setForeground(Color.WHITE);
		closeButton.setOpaque(false);
		closeButton.setContentAreaFilled(false);
		closeButton.setBorder(null);
		closeButton.setCursor(HAND_CURSOR);
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		closeBox.add(Box.createHorizontalGlue());
		closeBox.add(closeButton);
		
		verticalBox.add(closeBox);
		super.add(verticalBox);
		
		try {
			backgroundImage = ImageIO.read(new File("res/splashScreenBackground.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void incLoading() {
		loadingStatus++;
		// If we load everything, we can now
		// show all Serial ports available
		// to the user so he can
		// select one to connect to.
		if(finishLoading() && status == 0) {
			incStatus();
			showComboBox();
		}
	}
	
	public void incStatus() {
		status++;
	}
	
	public int getStatus() {
		return status;
	}
	
	public boolean finishLoading() {
		return loadingStatus >= COMPONENTS.length;
	}
	
	public String getSelectedSerialPortName() {
		return cbSerialPorts.getItemAt(cbSerialPorts.getSelectedIndex());
	}
	
	public void showComboBox() {
		status = 1;
		Box serialBox = Box.createHorizontalBox();
		
		String[] ports = BluetoothWrapper.searchForPorts();
		cbSerialPorts.removeAllItems();
		for(String port : ports) {
			cbSerialPorts.addItem(port);
		}
		serialBox.add(Box.createHorizontalGlue());
		serialBox.add(cbSerialPorts);
		serialBox.add(Box.createHorizontalStrut(10));
		JButton refreshButton = new JButton("🗘");
		refreshButton.setFont(getFont().deriveFont(20f));
		refreshButton.setForeground(Color.WHITE);
		refreshButton.setOpaque(false);
		refreshButton.setContentAreaFilled(false);
		refreshButton.setBorder(null);
		refreshButton.setCursor(HAND_CURSOR);
		refreshButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cbSerialPorts.removeAllItems();
				String[] ports = BluetoothWrapper.searchForPorts();
				for(String port : ports) {
					cbSerialPorts.addItem(port);
				}
			}
		});
		serialBox.add(refreshButton);
		serialBox.add(Box.createHorizontalGlue());
		serialBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		verticalBox.removeAll();

		verticalBox.add(Box.createVerticalStrut(120));
		JLabel label = new JLabel("Select a serial port to connect to:");
		label.setForeground(Color.GRAY.brighter());
		label.setFont(NORMAL_FONT);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		verticalBox.add(label);
		verticalBox.add(Box.createVerticalStrut(20));
		verticalBox.add(serialBox);
		verticalBox.add(Box.createVerticalStrut(20));
		JButton selectButton = new JButton("Select");
		selectButton.setFont(NORMAL_FONT.deriveFont(20f));
		selectButton.setForeground(Color.WHITE);
		selectButton.setOpaque(false);
		selectButton.setContentAreaFilled(false);
		selectButton.setBorder(null);
		selectButton.setCursor(HAND_CURSOR);
		selectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		verticalBox.add(selectButton);
		cbSerialPorts.setMaximumSize(new Dimension(80, 50));
		verticalBox.add(closeBox);
		verticalBox.setBorder(BorderFactory.createEmptyBorder());
		
		selectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showConnectionState();
				String selectedPortName = getSelectedSerialPortName();
				connectionThread = new ConnectionThread();
				connectionThread.connect(selectedPortName, (connectionAgreed) -> {
					if(connectionAgreed) {
						status = STATUS_END;
					} else {
						showComboBox();
					}
				});
			}
		});
	}
	
	public void showConnectionState() {
		status = 2;
		verticalBox.removeAll();
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setFont(NORMAL_FONT.deriveFont(20f));
		cancelButton.setForeground(Color.WHITE);
		cancelButton.setOpaque(false);
		cancelButton.setContentAreaFilled(false);
		cancelButton.setBorder(null);
		cancelButton.setCursor(HAND_CURSOR);
		cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				connectionThread.interrupt();
				showComboBox();
			}
		});
		verticalBox.add(Box.createVerticalStrut(200));
		verticalBox.add(cancelButton);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		g.clearRect(0, 0, getWidth(), getHeight());
		g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);

		progressBar.setValue(loadingStatus);
		progressLabel.setText(String.format("%s (%d / %d)", COMPONENTS[loadingStatus - 1], loadingStatus, COMPONENTS.length));
		paintCounter += 0.08;
		if(paintCounter >= PAINTS.length -1)
			paintCounter = 0;
		drawTitle(g);
		
		if(status == 2) {
			drawConnection(g);
		}
		
		paintComponents(g);
	}
	
	private void drawTitle(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		int paintIndex = (int) Math.floor(paintCounter);
		g2d.setPaint(PAINTS[paintIndex]);
		g.setFont(TITLE_FONT);
		FontMetrics fontMetrics = g.getFontMetrics();
		int titleWidth = fontMetrics.stringWidth(TITLE);
		int titleHeight = fontMetrics.getHeight();
		
		g.drawString(TITLE, getWidth() / 2 - titleWidth / 2, titleHeight + 10);
	}
	
	private void drawConnection(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		int paintIndex = (int) Math.floor(paintCounter);
		g2d.setPaint(PAINTS[paintIndex]);
		g.setFont(NORMAL_FONT);
		String text = "Connection to " + getSelectedSerialPortName() + " ... ";
		FontMetrics fontMetrics = g.getFontMetrics();
		int titleWidth = fontMetrics.stringWidth(text);
		int titleHeight = fontMetrics.getHeight();
		
		g.drawString(text, getWidth() / 2 - titleWidth / 2, titleHeight + 100);
	}

}
