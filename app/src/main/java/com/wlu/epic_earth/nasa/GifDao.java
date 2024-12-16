package com.wlu.epic_earth.nasa;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.stereotype.Repository;

@Repository
public class GifDao {
    private static final Logger logger = Logger.getLogger(GifDao.class.getName());
    private static final String DB_URL = "jdbc:h2:file:./data/epic-earth";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "password";

    public void saveGif(Gif gif) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO GIF (name, date, data) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, gif.getName());
                pstmt.setDate(2, new java.sql.Date(gif.getDate().getTime()));
                pstmt.setBytes(3, gif.getData());
                pstmt.executeUpdate();
            }
            logger.log(Level.INFO, "Saved GIF to database");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error saving GIF to database", e);
        }
    }

    public Gif getGifByDate(Date date) {
        Gif gif = null;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM GIF WHERE date = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDate(1, new java.sql.Date(date.getTime()));
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    Long id = rs.getLong("id");
                    String name = rs.getString("name");
                    byte[] data = rs.getBytes("data");
                    gif = new Gif(id, name, date, data);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching GIF from database for date " + date, e);
        }
        return gif;
    }

    public List<Gif> getAllGifs() {
        List<Gif> gifs = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM GIF";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    Long id = rs.getLong("id");
                    String name = rs.getString("name");
                    Date date = rs.getDate("date");
                    byte[] data = rs.getBytes("data");
                    Gif gif = new Gif(id, name, date, data);
                    gifs.add(gif);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching all GIFs from database", e);
        }
        return gifs;
    }

    public void createGif(List<EpicImage> images, Date date) {
        // Create GIF from images
        byte[] gifData = new byte[0];
        Gif gif = new Gif(null, "epic-earth-" + date, date, gifData);
        saveGif(gif);
    }
}