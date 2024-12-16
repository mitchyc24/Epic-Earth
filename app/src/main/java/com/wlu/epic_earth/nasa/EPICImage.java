package com.wlu.epic_earth.nasa;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.text.ParseException;
import org.json.JSONObject;


public class EpicImage {
    private static final Logger logger = Logger.getLogger(EpicImage.class.getName());

    private String identifier;
    private String caption;
    private String name;
    private String version;
    private Date date;
    private String imageUrl;
    private BufferedImage image;
    private String thumbnailUrl;

    public EpicImage() {
        this("", "", "", "", new Date(), "", null, "");
    }

    public EpicImage(String identifier, String caption, String name, String version, Date date, String imageUrl, BufferedImage image, String thumbnailUrl) {
        this.identifier = identifier;
        this.caption = caption;
        this.name = name;
        this.version = version;
        this.date = date;
        this.imageUrl = imageUrl;
        this.image = image;
        this.thumbnailUrl = thumbnailUrl;
    }

    public EpicImage(String identifier, String caption, String name, String version, Date date, String imageUrl, String thumbnailUrl) {
        this(identifier, caption, name, version, date, imageUrl, null, thumbnailUrl);
    }


    public static EpicImage fromJSONObject(JSONObject jsonObject) {
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
        String imageUrl = String.format("https://epic.gsfc.nasa.gov/archive/natural/%1$tY/%1$tm/%1$td/png/%2$s.png", date, name);
        BufferedImage image = fetchImage(imageUrl);
        String thumbnailurl = String.format("https://epic.gsfc.nasa.gov/archive/natural/%1$tY/%1$tm/%1$td/thumbs/%2$s.jpg", date, name);

        return new EpicImage(identifier, caption, name, version, date, imageUrl, image, thumbnailurl);
    }

    private static BufferedImage fetchImage(String urlStr) {
        try {
            URL url = new URL(urlStr);
            BufferedImage image = ImageIO.read(url);
            logger.log(Level.INFO, "Downloaded image from {0}", urlStr);
            return image;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error fetching image {0}: {1}", new Object[]{urlStr, e});
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

    // Setters
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public Date getDate() {
        return date;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

}