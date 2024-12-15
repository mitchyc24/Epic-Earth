package com.wlu.epic_earth.nasa;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.InputStreamReader;
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
                String url = String.format("https://epic.gsfc.nasa.gov/archive/natural/%1$tY/%1$tm/%1$td/png/%2$s.png", date, name);
                BufferedImage image = getImage(url);
                String localPath = saveImage(image, name, date);
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

    private static String saveImage(BufferedImage image, String name, Date date) throws IOException {
        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(date);
        String directoryPath = "data/images/" + dateStr;
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String filePath = directoryPath + "/" + name + ".png";
        File file = new File(filePath);
        ImageIO.write(image, "png", file);
        return filePath;
    }
}