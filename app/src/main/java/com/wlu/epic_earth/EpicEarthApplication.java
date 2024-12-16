package com.wlu.epic_earth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.madgag.gif.fmsware.AnimatedGifEncoder;
import com.wlu.epic_earth.nasa.EpicImage;
import com.wlu.epic_earth.nasa.EpicImageDao;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;


@SpringBootApplication
public class EpicEarthApplication {

    public static void main(String[] args) {
        SpringApplication.run(EpicEarthApplication.class, args);
    }

    public static List<EpicImage> retrieveImages(String dateStr) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = dateFormat.parse(dateStr);

            EpicImageDao epicImageDao = new EpicImageDao();
            List<EpicImage> images = epicImageDao.getEpicImagesByDate(date);

            return images;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] createGif(List<BufferedImage> images) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.start(baos);
        encoder.setRepeat(0); // Loop indefinitely

        for (BufferedImage image : images) {
            encoder.addFrame(image);
        }

        encoder.finish();
        return baos.toByteArray();
    }
}