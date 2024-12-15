package com.wlu.epic_earth.nasa;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.text.ParseException;
import org.json.JSONObject;


public class EPICImage {
    private static final Logger logger = Logger.getLogger(EPICImage.class.getName());
    private static final SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private String identifier;
    private String caption;
    private String name;
    private String version;
    private Date date;
    private String url;
    private BufferedImage image;
    private String localPath;

    public EPICImage(String identifier, String caption, String name, String version, Date date, String url, BufferedImage image, String localPath) {
        this.identifier = identifier;
        this.caption = caption;
        this.name = name;
        this.version = version;
        this.date = date;
        this.url = url;
        this.image = image;
        this.localPath = localPath;
        //Test
    }

    public static EPICImage fromJSONObject(JSONObject jsonObject) {
        String identifier = jsonObject.optString("identifier", "");
        String caption = jsonObject.optString("caption", "");
        String name = jsonObject.optString("image", "");
        String version = jsonObject.optString("version", "");
        Date date;
        try {
            date = parseDatetime(name);
        } catch (ParseException e) {
            logger.log(Level.SEVERE, "Failed to parse date.", e);
            date = new Date();
        }
        String url = String.format("https://epic.gsfc.nasa.gov/archive/natural/%1$tY/%1$tm/%1$td/png/%2$s.png", date, name);
        BufferedImage image = getImage(name, date);
        String localPath = String.format("data/images/%s/%s.png", fileDateFormat.format(date), name);

        return new EPICImage(identifier, caption, name, version, date, url, image, localPath);
    }

    private static BufferedImage getImage(String name, Date date) {
        String dateStr = fileDateFormat.format(date);
        String localImagePath = String.format("data/images/%s/%s.png", dateStr, name);
        logger.log(Level.INFO, "Local image path: {0}", localImagePath);

        File localImageFile = new File(localImagePath);
        if (localImageFile.exists()) {
            try {
                BufferedImage image = ImageIO.read(localImageFile);
                logger.log(Level.INFO, "Loaded image {0} from cache.", name);
                return image;
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to load image from cache.", e);
            }
        }

        logger.log(Level.INFO, "Image {0} not found in cache, fetching from NASA API.", name);
        String urlEndpoint = String.format("https://epic.gsfc.nasa.gov/archive/natural/%1$tY/%1$tm/%1$td/png/%2$s.png", date, name);

        try {
            URL url = new URL(urlEndpoint);
            BufferedImage image = ImageIO.read(url);
            logger.log(Level.INFO, "Downloaded image {0}", name);
            Utils.saveImage(image, localImagePath);
            return image;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error fetching image {0}: {1}", new Object[]{name, e});
            return null;
        }
    }


    private static Date parseDatetime(String name) throws ParseException {
        // Extract date from image name: 
        // e.g. "epic_1b_20150724003613" -> "2015-07-24 00:36:13"
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateTimePart = name.substring(name.lastIndexOf('_') + 1);
        logger.log(Level.INFO, "Extracted date from image name: {0}", dateTimePart);
        return formatter.parse(dateTimePart);
    
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public BufferedImage getImage() {
        return image;
    }

    public String getCaption() {
        return caption;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getUrl() {
        return url;
    }

    public Date getDate() {
        return date;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }
}