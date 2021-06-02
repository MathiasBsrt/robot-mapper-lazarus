package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class ImageUtils {

	public static BufferedImage replaceColor(Image sourceImage, Color sourceColor, Color newColor) {
		BufferedImage source = toBufferedImage(sourceImage);
		int width = source.getWidth();
		int height = source.getHeight();
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int rgb;
		Color color;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				rgb = source.getRGB(x, y);
				color = new Color(rgb, true);
				if (color.getAlpha() == 0) {
					newImage.setRGB(x, y, rgb);
				} else if (colorDistance(color, sourceColor) < 50) {
					newImage.setRGB(x, y, newColor.getRGB());
				} else
					newImage.setRGB(x, y, rgb);
			}
		}
		return newImage;
	}

	public static float colorDistance(Color color1, Color color2) {
		return (float) Math.sqrt(
				Math.pow(color2.getRed() - color1.getRed(), 2) + Math.pow(color2.getGreen() - color1.getGreen(), 2)
						+ Math.pow(color2.getBlue() - color1.getBlue(), 2));
	}

	/**
	 * Converts a given Image into a BufferedImage
	 *
	 * @param img The Image to be converted
	 * @return The converted BufferedImage
	 * 
	 *         {@link https://stackoverflow.com/a/13605411}
	 */
	public static BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}

		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		// Return the buffered image
		return bimage;
	}

}
