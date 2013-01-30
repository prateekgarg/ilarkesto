package ilarkesto.io;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Awt {

	public static String toHtmlColor(Color c) {
		StringBuilder sb = new StringBuilder("#");
		String s;

		s = Integer.toHexString(c.getRed());
		if (s.length() == 1) sb.append('0');
		sb.append(s);

		s = Integer.toHexString(c.getGreen());
		if (s.length() == 1) sb.append('0');
		sb.append(s);

		s = Integer.toHexString(c.getBlue());
		if (s.length() == 1) sb.append('0');
		sb.append(s);

		return sb.toString();
	}

	public static void writeImage(Image image, String type, String file) {
		writeImage(image, type, new File(file));
	}

	public static void writeImage(Image image, String type, File file) {
		IO.createDirectory(file.getParentFile());
		try {
			ImageIO.write(toBufferedImage(image), type, file);
		} catch (IOException ex) {
			throw new RuntimeException("Writing image to file failed: " + file, ex);
		}
	}

	public static void writeImage(Image image, int width, int height, String type, String file) throws IOException {
		File f = new File(file);
		IO.createDirectory(f.getParentFile());
		BufferedImage bufferedImage = toBufferedImage(image, width, height);
		ImageIO.write(bufferedImage, type, f);
		System.out.println("done");
	}

	public static BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage) return (BufferedImage) img;
		return toBufferedImage(img, img.getWidth(null), img.getHeight(null));
	}

	public static synchronized BufferedImage toBufferedImage(Image img, int width, int height) {
		if (img instanceof BufferedImage) return (BufferedImage) img;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		return image;
	}

	public static BufferedImage loadImage(File file) {
		BufferedImage image;
		try {
			image = ImageIO.read(file);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		if (image == null) throw new RuntimeException("Unsupported image format.");
		return image;
	}

	public static BufferedImage loadImage(byte[] data) {
		BufferedImage image;
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(data);
			image = ImageIO.read(in);
			in.close();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		if (image == null) throw new RuntimeException("Unsupported image format.");
		return image;
	}

	public static BufferedImage loadImage(String resourcePath) {
		try {
			return ImageIO.read(IO.class.getClassLoader().getResource(resourcePath));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void saveScaled(BufferedImage image, String type, String file, int maxWidth, int maxHeight)
			throws IOException {
		Image scaled = getScaled(image, maxWidth, maxHeight);
		writeImage(scaled, type, file);
	}

	public static void scaleImage(String sourceFile, String destinationFile, String destinationType, int maxWidth,
			int maxHeight) throws IOException {
		saveScaled(loadImage(new File(sourceFile)), destinationType, destinationFile, maxWidth, maxHeight);
	}

	public static Image getScaled(BufferedImage image, int maxWidth, int maxHeight) {
		int width = image.getWidth();
		int height = image.getHeight();
		if (width <= maxWidth && height <= maxHeight) { return image; }

		if (width > maxWidth) {
			width = maxWidth;
			height = height * maxWidth / image.getWidth();
		}

		int h;
		int w;
		if (height > maxHeight) {
			h = maxHeight;
			w = width * maxHeight / height;
		} else {
			h = height;
			w = width;
		}

		return image.getScaledInstance(w, h, Image.SCALE_SMOOTH);
	}

	public static Image scaledToWidth(BufferedImage image, int targetWidth) {
		int width = image.getWidth();
		int height = image.getHeight();
		if (width == targetWidth) { return image; }

		width = targetWidth;
		height = height * targetWidth / image.getWidth();

		return image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
	}

	public static Image scaledToHeight(BufferedImage image, int targetHeight) {
		int width = image.getWidth();
		int height = image.getHeight();
		if (height == targetHeight) { return image; }

		height = targetHeight;
		width = width * targetHeight / image.getHeight();

		return image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
	}

	public static BufferedImage quadratize(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		if (width == height) return image;

		if (width > height) {
			int offset = (width - height) / 2;
			return image.getSubimage(offset, 0, height, height);
		} else {
			int offset = (height - width) / 2;
			return image.getSubimage(0, offset, width, width);
		}
	}

	public static Image quadratizeAndLimitSize(BufferedImage image, int maxSize) {
		image = quadratize(image);
		if (image.getWidth() <= maxSize) return image;
		return image.getScaledInstance(maxSize, maxSize, Image.SCALE_SMOOTH);
	}

}
