package com.wlu.epic_earth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.wlu.epic_earth.nasa.EpicImage;
import com.wlu.epic_earth.nasa.EpicImageDao;



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

}