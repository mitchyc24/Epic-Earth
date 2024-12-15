package com.wlu.epic_earth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

            // Create GIF from images
            List<String> imagePaths = new ArrayList<>();
            for (EPICImage image : images) {
                imagePaths.add(image.getLocalPath());
            }
            String outputPath = "data/gifs/" + dateStr + ".gif";
            createGif(imagePaths, outputPath);

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

    private static void createGif(List<String> imagePaths, String outputPath) throws IOException {
        File outputDir = new File(outputPath).getParentFile();
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        
        List<String> command = new ArrayList<>();
        command.add("python");
        command.add("python/create_gif.py");
        command.add(outputPath);
        command.addAll(imagePaths);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("Python script exited with code " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Python script was interrupted", e);
        }
    }

}