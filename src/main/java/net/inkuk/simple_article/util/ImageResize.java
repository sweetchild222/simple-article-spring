package net.inkuk.simple_article.util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageResize {

    public static BufferedImage resize(BufferedImage image, int targetWidth, int targetHeight){

        final float widthRatio = (float) targetWidth / image.getWidth();
        final float heightRatio = (float) targetHeight / image.getHeight();

        final float scale = Math.max(widthRatio, heightRatio);

        final int scaledWidth = (int) (image.getWidth() * scale);
        final int scaledHeight = (int) (image.getHeight() * scale);

        final Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        final BufferedImage bufferedScaledImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);

        final Graphics2D g2d = bufferedScaledImage.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();

        return bufferedScaledImage;
    }


    public static BufferedImage resizeAndCrop(BufferedImage image, int targetWidth, int targetHeight) {

        BufferedImage scaledImage = resize(image, targetWidth, targetHeight);

        final int scaledWidth = scaledImage.getWidth();

        final int scaledHeight = scaledImage.getHeight();

        Log.debug(targetWidth);
        Log.debug(targetHeight);

        Log.debug("sdfs");
        Log.debug(scaledWidth);
        Log.debug(scaledHeight);

        final int cropX = Math.max(0, (scaledWidth - targetWidth) / 2);
        final int cropY = Math.max(0, (scaledHeight - targetHeight) / 2);

        final int finalWidth = Math.min(targetWidth, scaledWidth - cropX);
        final int finalHeight = Math.min(targetHeight, scaledHeight - cropY);

        Log.debug(cropY);
        Log.debug(cropX);
        Log.debug(finalWidth);
        Log.debug(finalHeight);

        return scaledImage.getSubimage(cropX, cropY, finalWidth, finalHeight);
    }
}
