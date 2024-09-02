package mikita.miscellaneous;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

public interface ImageSource<T extends Exception> {

    BufferedImage getImage() throws T;

    default BufferedImage getScaledImage(int param, Scale paramType) throws T {
        if (param <= 0) {
            throw new IllegalArgumentException("param must be positive");
        }
        if (paramType == null) {
            throw new NullPointerException("paramType must be not null");
        }

        BufferedImage image = getImage();

        int width = image.getWidth();
        int height = image.getHeight();

        double ratio = (double) width / height;

        if (paramType == Scale.WIDTH) {
            width = param;
            height = (int) (width / ratio);
        } else {
            height = param;
            width = (int) (height * ratio);
        }

        return scale(image, width, height);
    }

    default BufferedImage getScaledImage(int width, int height) throws T {
        if (width <= 0) {
            throw new IllegalArgumentException("width must be positive");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("height must be positive");
        }
        return scale(getImage(), width, height);
    }

    default byte[] getImageAsBytes() throws T {
        return imageToBytes(getImage());
    }

    default byte[] getScaledImageAsBytes(int param, Scale paramType) throws T {
        return imageToBytes(getScaledImage(param, paramType));
    }

    default byte[] getScaledImageAsBytes(int width, int height) throws T {
        return imageToBytes(getScaledImage(width, height));
    }

    private static BufferedImage scale(BufferedImage original, int width, int height) {
        Image scaled = original.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.drawImage(scaled, 0, 0, null);
        graphics.dispose();
        return image;
    }

    private static byte[] imageToBytes(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             BufferedOutputStream buff = new BufferedOutputStream(baos)) {
            ImageIO.write(image, "png", buff);
            buff.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("Could not parse image to bytes", e);
        }
    }

    public enum Scale {
        WIDTH, HEIGHT;
    }

}
