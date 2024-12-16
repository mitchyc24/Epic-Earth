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
    private String url;
    private BufferedImage image;

    public EpicImage() {
        this("", "", "", "", new Date(), "", null);
    }

    public EpicImage(String identifier, String caption, String name, String version, Date date, String url, BufferedImage image) {
        this.identifier = identifier;
        this.caption = caption;
        this.name = name;
        this.version = version;
        this.date = date;
        this.url = url;
        this.image = image;
    }

    public EpicImage(String identifier, String caption, String name, String version, Date date, String url){
        this(identifier, caption, name, version, date, url, null);
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
        String url = String.format("https://epic.gsfc.nasa.gov/archive/natural/%1$tY/%1$tm/%1$td/png/%2$s.png", date, name);
        BufferedImage image = fetchImage(url);

        return new EpicImage(identifier, caption, name, version, date, url, image);
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

    public void setUrl(String url) {
        this.url = url;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
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

}