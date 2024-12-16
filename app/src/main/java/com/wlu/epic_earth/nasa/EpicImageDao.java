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
    private static final String DB_URL = "jdbc:h2:file:./data/epic-earth";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "password";
    private static final String API_KEY = System.getenv("NASA_API_KEY");
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    public List<EpicImage> getEpicImagesByDate(Date date) {
        List<EpicImage> images = new ArrayList<>();
        String dateString = new SimpleDateFormat("yyyy-MM-dd").format(date);

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
                    String imageUrl = rs.getString("url");
                    BufferedImage bufferedImage = ImageIO.read(rs.getBinaryStream("image"));
                    EpicImage image = new EpicImage(identifier, caption, name, version, imageDate, imageUrl, bufferedImage);
                    images.add(image);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching images from database for date " + dateString, e);
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

    public void saveEpicImages(List<EpicImage> images) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "MERGE INTO EPICIMAGE (identifier, caption, name, version, date, url, image) KEY(identifier) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (EpicImage image : images) {
                    pstmt.setString(1, image.getIdentifier());
                    pstmt.setString(2, image.getCaption());
                    pstmt.setString(3, image.getName());
                    pstmt.setString(4, image.getVersion());
                    pstmt.setString(5, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(image.getDate()));
                    pstmt.setString(6, image.getUrl());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(image.getImage(), "png", baos);
                    pstmt.setBinaryStream(7, new ByteArrayInputStream(baos.toByteArray()));
                    pstmt.executeUpdate();
                }
            }
            logger.log(Level.INFO, "Saved " + images.size() + " EpicImages to database");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error saving images to database", e);
        }
    }

    public List<EpicImage> fetchImagesFromApi(Date date) {
        List<EpicImage> epicImages = new ArrayList<>();
        String dateString = dateFormat.format(date);
        String urlEndpoint = "https://api.nasa.gov/EPIC/api/natural/date/" + dateString + "?api_key=" + API_KEY;

        try {
            JSONArray data = new JSONArray(sendGetRequest(urlEndpoint));
            for (int i = 0; i < data.length(); i++) {
                JSONObject item = data.getJSONObject(i);
                String name = item.getString("image");
                String url = String.format("https://epic.gsfc.nasa.gov/archive/natural/%1$tY/%1$tm/%1$td/png/%2$s.png", date, name);
                BufferedImage image = getImage(url);

                EpicImage epicImage = new EpicImage();
                epicImage.setIdentifier(item.getString("identifier"));
                epicImage.setCaption(item.getString("caption"));
                epicImage.setName(name);
                epicImage.setVersion(item.getString("version"));
                epicImage.setDate(date);
                epicImage.setUrl(url);
                epicImage.setImage(image);

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

    public List<BufferedImage> getImagesByDate(Date date) {
        List<EpicImage> images = getEpicImagesByDate(date);
        return images.stream().map(EpicImage::getImage).collect(Collectors.toList());
    }
}