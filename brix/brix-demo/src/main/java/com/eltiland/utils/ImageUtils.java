package com.eltiland.utils;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author knorr
 * @version 1.0
 * @since 7/30/12
 */
public class ImageUtils {

    public static BufferedImage resizeImage(BufferedImage original, int imageType, int toWidth, int toHeight) {
        BufferedImage resized = new BufferedImage(toWidth, toHeight, imageType);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(original, 0, 0, toWidth, toHeight, null);
        g2d.dispose();
        return resized;
    }

}
