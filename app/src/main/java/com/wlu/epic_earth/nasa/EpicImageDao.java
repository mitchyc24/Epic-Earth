package com.wlu.epic_earth.nasa;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;
import java.util.stream.Collectors;

@Repository
public class EpicImageDao {
    private static final Logger logger = Logger.getLogger(EpicImageDao.class.getName());
    private static final String DB_URL = System.getProperty("DB_URL");
    private static final String DB_USER = System.getProperty("DB_USER");
    private static final String DB_PASSWORD = System.getProperty("DB_PASS");
    private static final String API_KEY = System.getenv("NASA_API_KEY");
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    public List<EpicImage> getEpicImagesByDate(Date date) {
        List<EpicImage> images = new ArrayList<>();
        String dateString = new SimpleDateFormat("yyyy-MM-dd").format(date);

        logger.log(Level.INFO, "Fetching images from DB for date " + dateString + "and user " + DB_USER);
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM EPICIMAGE WHERE date = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, dateString);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    String identifier = rs.getString("identifier");
                    String caption = rs.getString("caption");
                    String name = rs.getString("name");
                    String version = rs.getString("version");
                    Date imageDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("date"));
                    String imageUrl = rs.getString("imageurl");
                    BufferedImage bufferedImage = ImageIO.read(rs.getBinaryStream("image"));
                    String thumbnailUrl = rs.getString("thumbnailUrl");
                    EpicImage image = new EpicImage(identifier, caption, name, version, imageDate, imageUrl, bufferedImage, thumbnailUrl);
                    images.add(image);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching images from DB for date " + dateString, e);
        }

        if (images.isEmpty()) {
            images = fetchImagesFromApi(date);
            if (!images.isEmpty()) {
                logger.log(Level.INFO, "Fetched " + images.size() + " images from API for date " + dateString);
                saveEpicImages(images);
            }
        }

        return images;
    }

    public EpicImage findByIdentifier(String identifier){
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM EPICIMAGE WHERE identifier = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, identifier);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    String caption = rs.getString("caption");
                    String name = rs.getString("name");
                    String version = rs.getString("version");
                    Date imageDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("date"));
                    String imageUrl = rs.getString("imageurl");
                    BufferedImage bufferedImage = ImageIO.read(rs.getBinaryStream("image"));
                    String thumbnailUrl = rs.getString("thumbnailUrl");
                    return new EpicImage(identifier, caption, name, version, imageDate, imageUrl, bufferedImage, thumbnailUrl);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching image from DB", e);
        }
        return null;
    }

    public void saveEpicImages(List<EpicImage> images) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "MERGE INTO EPICIMAGE (identifier, caption, name, version, date, imageurl, image, thumbnailUrl) KEY(identifier) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (EpicImage image : images) {
                    pstmt.setString(1, image.getIdentifier());
                    pstmt.setString(2, image.getCaption());
                    pstmt.setString(3, image.getName());
                    pstmt.setString(4, image.getVersion());
                    pstmt.setString(5, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(image.getDate()));
                    pstmt.setString(6, image.getImageUrl());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(image.getImage(), "png", baos);
                    pstmt.setBinaryStream(7, new ByteArrayInputStream(baos.toByteArray()));
                    pstmt.setString(8, image.getThumbnailUrl());
                    pstmt.executeUpdate();
                }
            }
            logger.log(Level.INFO, "Saved " + images.size() + " EpicImages to DB");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error saving images to DB", e);
        }
    }

    public List<EpicImage> fetchImagesFromApi(Date date) {
        List<EpicImage> epicImages = new ArrayList<>();
        String dateString = dateFormat.format(date);
        String apiUrl = "https://api.nasa.gov/EPIC/api/natural/date/" + dateString + "?api_key=" + API_KEY;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String response = in.lines().collect(Collectors.joining());
                in.close();

                JSONArray data = new JSONArray(response);
                for (int i = 0; i < data.length(); i++) {
                    JSONObject item = data.getJSONObject(i);
                    String name = item.getString("image");
                    String imageUrl = String.format("https://epic.gsfc.nasa.gov/archive/natural/%1$tY/%1$tm/%1$td/png/%2$s.png", date, name);
                    BufferedImage image = getImage(imageUrl);

                    EpicImage epicImage = new EpicImage();
                    epicImage.setIdentifier(item.getString("identifier"));
                    epicImage.setCaption(item.getString("caption"));
                    epicImage.setName(name);
                    epicImage.setVersion(item.getString("version"));
                    epicImage.setDate(date);
                    epicImage.setImageUrl(imageUrl);
                    epicImage.setImage(image);
                    epicImage.setThumbnailUrl(String.format("https://epic.gsfc.nasa.gov/archive/natural/%1$tY/%1$tm/%1$td/thumbs/%2$s.jpg", date, name));
                    epicImages.add(epicImage);
                }
            } else {
                logger.log(Level.SEVERE, "Error fetching images from NASA API: HTTP response code " + responseCode);
            }
            logger.log(Level.INFO, "Found " + epicImages.size() + " images for date " + dateString);

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error fetching images for date " + dateString, e);
        }
        return epicImages;
    }

    private BufferedImage getImage(String imageUrl) throws IOException {
        logger.log(Level.INFO, "Fetching image from URL: " + imageUrl);
        URL url = new URL(imageUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setRequestProperty("Accept", "image/webp,image/apng,image/*,*/*;q=0.8");
        conn.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
        conn.setRequestProperty("Connection", "keep-alive");
    
        int responseCode = conn.getResponseCode();
        String responseMessage = conn.getResponseMessage();
        logger.log(Level.INFO, "HTTP response code: " + responseCode + ", message: " + responseMessage);
        if (responseCode == 200) {
            return ImageIO.read(conn.getInputStream());
        } else {
            logger.log(Level.SEVERE, "Failed to fetch image: HTTP response code " + responseCode + ", message: " + responseMessage);
            throw new IOException("Failed to fetch image: HTTP response code " + responseCode + ", message: " + responseMessage);
        }
    }

    public List<BufferedImage> getImagesByDate(Date date) {
        List<EpicImage> images = getEpicImagesByDate(date);
        return images.stream().map(EpicImage::getImage).collect(Collectors.toList());
    }
}