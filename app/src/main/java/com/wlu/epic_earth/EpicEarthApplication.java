package com.wlu.epic_earth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.wlu.epic_earth.nasa.EpicImageFetcher;
import com.wlu.epic_earth.nasa.EPICImage;

@SpringBootApplication
public class EpicEarthApplication {

    public static void main(String[] args) {
        SpringApplication.run(EpicEarthApplication.class, args);
    }

    public static List<EPICImage> retrieveImages(String dateStr) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = dateFormat.parse(dateStr);

            // Fetch images using EpicImageFetcher
            List<EPICImage> images = EpicImageFetcher.getEpicImagesOnDate(date);

            // Save metadata to database
            saveImageMetadata(images);

            return images;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void saveImageMetadata(List<EPICImage> images) {
        String url = "jdbc:h2:file:./data/epic-earth";
        String user = "sa";
        String password = "password";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "MERGE INTO images (name, path, datetime) KEY(name) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (EPICImage image : images) {
                    pstmt.setString(1, image.getName());
                    pstmt.setString(2, image.getUrl());
                    pstmt.setString(3, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(image.getDate()));
                    pstmt.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}