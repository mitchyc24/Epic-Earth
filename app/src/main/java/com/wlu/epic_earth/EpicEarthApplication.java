package com.wlu.epic_earth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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

            return images;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void createGif(List<EPICImage> images, String outputPath) throws IOException {
        File outputDir = new File(outputPath).getParentFile();
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        
        // Create a list of image paths
        List<String> imagePaths = new ArrayList<>();
        for (EPICImage image : images) {
            imagePaths.add(image.getLocalPath());
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