package com.wlu.epic_earth.nasa;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.json.JSONArray;
import org.json.JSONObject;

public class EpicImageFetcher {
    private static final String API_KEY = System.getenv("NASA_API_KEY");
    private static final Logger logger = Logger.getLogger(EpicImageFetcher.class.getName());
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    public static List<EPICImage> getEpicImagesOnDate(Date date) {
        List<EPICImage> epicImages = new ArrayList<>();
        String dateString = dateFormat.format(date);
        logger.log(Level.INFO, "Fetching images for date " + dateString);

        String urlEndpoint = "https://api.nasa.gov/EPIC/api/natural/date/" + dateString + "?api_key=" + API_KEY;

        try {
            JSONArray data = new JSONArray(sendGetRequest(urlEndpoint));
            for (int i = 0; i < data.length(); i++) {
                JSONObject item = data.getJSONObject(i);
                String name = item.getString("image");
                String localPath = getLocalImagePath(name, date);
                BufferedImage image;

                // Check if the image exists locally
                File imageFile = new File(localPath);
                if (imageFile.exists()) {
                    logger.log(Level.INFO, "Using local image for " + name);
                    image = ImageIO.read(imageFile);
                } else {
                    logger.log(Level.INFO, "Fetching image from URL for " + name);
                    String url = String.format("https://epic.gsfc.nasa.gov/archive/natural/%1$tY/%1$tm/%1$td/png/%2$s.png", date, name);
                    image = getImage(url);
                    saveImage(image, name, date);
                }

                EPICImage epicImage = EPICImage.fromJSONObject(item);
                epicImages.add(epicImage);
            }
            logger.log(Level.INFO, "Found " + epicImages.size() + " images for date " + dateString);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error fetching images for date " + dateString, e);
        }
        return epicImages;
    }

    private static String sendGetRequest(String urlEndpoint) throws IOException {
        URL url = new URL(urlEndpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } finally {
            connection.disconnect();
        }
    }

    private static BufferedImage getImage(String url) throws IOException {
        URL imageUrl = new URL(url);
        return ImageIO.read(imageUrl);
    }

    private static void saveImage(BufferedImage image, String name, Date date) throws IOException {
        String localPath = getLocalImagePath(name, date);
        File outputFile = new File(localPath);
        outputFile.getParentFile().mkdirs(); // Ensure the directory exists
        ImageIO.write(image, "png", outputFile);
    }

    private static String getLocalImagePath(String name, Date date) {
        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(date);
        return "data/images/" + dateStr + "/" + name + ".png";
    }
}