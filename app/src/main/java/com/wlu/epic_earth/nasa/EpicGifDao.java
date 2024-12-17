package com.wlu.epic_earth.nasa;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.madgag.gif.fmsware.AnimatedGifEncoder;
import java.io.ByteArrayOutputStream;


@Repository
public class EpicGifDao {
    private static final Logger logger = Logger.getLogger(EpicGifDao.class.getName());
    private static final String DB_URL = "jdbc:h2:file:./data/epic-earth";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "password";

    @Autowired
    private EpicImageDao epicImageDao;

    public void saveEpicGif(EpicGif gif) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "MERGE INTO EPICGIF (date, data) KEY(date) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDate(1, new java.sql.Date(gif.getDate().getTime()));
                pstmt.setBytes(2, gif.getData());
                pstmt.executeUpdate();
            }
            logger.log(Level.INFO, "Saved EpicGif to database");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error saving EpicGif to database", e);
        }
    }

    public EpicGif getEpicGifByDate(Date date) {
        EpicGif gif = null;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM EPICGIF WHERE date = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDate(1, new java.sql.Date(date.getTime()));
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    byte[] data = rs.getBytes("data");
                    gif = new EpicGif(date, data);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching EpicGif from database", e);
        }

        if (gif == null) {
            gif = generateAndSaveGif(date);
        }

        return gif;
    }

    private EpicGif generateAndSaveGif(Date date) {
        List<EpicImage> epicImages = epicImageDao.getEpicImagesByDate(date);
        if (epicImages.isEmpty()) {
            logger.log(Level.WARNING, "No images found for date " + date);
            return null;
        }

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            AnimatedGifEncoder gifEncoder = new AnimatedGifEncoder();
            gifEncoder.start(baos);
            gifEncoder.setRepeat(0); // Loop indefinitely
            gifEncoder.setDelay(500); // ms delay between frames

            for (EpicImage epicImage : epicImages) {
                gifEncoder.addFrame(epicImage.getImage());
            }

            gifEncoder.finish();

            byte[] gifBytes = baos.toByteArray();
            EpicGif epicGif = new EpicGif(date, gifBytes);
            saveEpicGif(epicGif);

            return epicGif;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error generating GIF for date " + date, e);
            return null;
        }
    }
}