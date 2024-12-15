package com.wlu.epic_earth.nasa;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utils {
    private static final Logger logger = Logger.getLogger(Utils.class.getName());


    public static void saveImage(BufferedImage image, String filePath) {
        try {
            File file = new File(filePath);
            file.mkdirs(); // Ensure directory path exists
            ImageIO.write(image, "png", file);
            logger.log(Level.INFO, "Saved image to {0}", filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
