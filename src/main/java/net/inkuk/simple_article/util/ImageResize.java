package net.inkuk.simple_article.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

public class ImageResize {

    public static @NotNull BufferedImage scale(BufferedImage srcImage, int targetWidth, int targetHeight){

        final float widthRatio = (float) targetWidth / srcImage.getWidth();
        final float heightRatio = (float) targetHeight / srcImage.getHeight();

        int scaledWidth = widthRatio >= heightRatio ? targetWidth : (int) (srcImage.getWidth() * heightRatio);
        int scaledHeight = widthRatio <= heightRatio ? targetHeight : (int) (srcImage.getHeight() * widthRatio);

        final Image scaledImage = srcImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        final BufferedImage newImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);

        final Graphics2D g2d = newImage.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();

        return newImage;
    }


    public static @NotNull BufferedImage crop(BufferedImage srcImage, int targetWidth, int targetHeight) {

        final int srcWidth = srcImage.getWidth();
        final int srcHeight = srcImage.getHeight();

        final int cropX = Math.max(0, (srcWidth - targetWidth) / 2);
        final int cropY = Math.max(0, (srcHeight - targetHeight) / 2);

        final int finalWidth = Math.min(targetWidth, srcWidth - cropX);
        final int finalHeight = Math.min(targetHeight, srcHeight - cropY);

        return srcImage.getSubimage(cropX, cropY, finalWidth, finalHeight);
    }




    public static BufferedImage resize(BufferedImage srcImage, int orientation, int targetWidth, int targetHeight) {

        BufferedImage rotatedImage = rotate(srcImage, orientation);

        if(rotatedImage == null)
            return null;

        if(rotatedImage.getWidth() == targetWidth && rotatedImage.getHeight() == targetHeight)
            return srcImage;

        final BufferedImage scaledImage = scale(rotatedImage, targetWidth, targetHeight);

        return crop(scaledImage, targetWidth, targetHeight);
    }


    private static BufferedImage rotate(BufferedImage srcImage, int orientation) {

        final int radians = (Map.of(1, 0, 6, 90, 3, 180, 8, 270)).getOrDefault(orientation, -1);

        BufferedImage newImage;

        if (radians == 90 || radians == 270)
            newImage = new BufferedImage(srcImage.getHeight(), srcImage.getWidth(), srcImage.getType());
        else if (radians == 180)
            newImage = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), srcImage.getType());
        else if(radians == 0)
            return srcImage;
        else
            return null;

        Graphics2D graphics = (Graphics2D) newImage.getGraphics();

        graphics.rotate(Math.toRadians(radians), (double)newImage.getWidth() / 2, (double)newImage.getHeight() / 2);
        graphics.translate((newImage.getWidth() - srcImage.getWidth()) / 2, (newImage.getHeight() - srcImage.getHeight()) / 2);
        graphics.drawRenderedImage(srcImage, null);

        return newImage;
    }
}
