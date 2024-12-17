package com.wlu.epic_earth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.wlu.epic_earth.nasa.EpicGif;
import com.wlu.epic_earth.nasa.EpicGifDao;
import com.wlu.epic_earth.nasa.EpicImage;
import com.wlu.epic_earth.nasa.EpicImageDao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller
public class EpicEarthController {

    private static final Logger logger = LoggerFactory.getLogger(EpicEarthController.class);

    @Autowired
    private EpicImageDao epicImageDao;

    @Autowired EpicGifDao epicGifDao;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("dateForm", new DateForm());
        return "index";
    }

    
    @GetMapping("/image-gallery")
    public String images(@RequestParam(required = false) String date, Model model) {
        if (date == null) {
            model.addAttribute("error", "Date parameter is required");
            return "index";
        }
        try {
            Date parsedDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            List<EpicImage> epicImages = epicImageDao.getEpicImagesByDate(parsedDate);
            logger.info("Found " + epicImages.size() + " images for date " + date);
            model.addAttribute("images", epicImages);
        } catch (ParseException e) {
            model.addAttribute("error", "Invalid date format");
        }
        return "image-gallery";
    }

    @GetMapping("/image/{identifier}")
    public ResponseEntity<byte[]> getImage(@PathVariable String identifier) {
        EpicImage image = epicImageDao.findByIdentifier(identifier);
        if (image != null && image.getImage() != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ImageIO.write(image.getImage(), "png", baos);
                baos.flush();
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            byte[] imageBytes = baos.toByteArray();
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(imageBytes);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/gif")
    public String gif(@RequestParam(required = false) String date, Model model) {
        if (date == null) {
            model.addAttribute("error", "Date parameter is required");
            return "index";
        }
        try {
            Date parsedDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            EpicGif epicGif = epicGifDao.getEpicGifByDate(parsedDate);
            if (epicGif == null) {
                model.addAttribute("error", "No GIF found for the given date");
                return "index";
            }
            model.addAttribute("gif", epicGif);
            model.addAttribute("date", date); 
        } catch (ParseException e) {
            model.addAttribute("error", "Invalid date format");
        }
        return "gif";
    }

    @GetMapping("/gif/{date}")
    public ResponseEntity<byte[]> getGif(@PathVariable String date) {
        try {
            Date parsedDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            EpicGif epicGif = epicGifDao.getEpicGifByDate(parsedDate);
            if (epicGif != null && epicGif.getData() != null) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_GIF)
                        .body(epicGif.getData());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
}

class DateForm {
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}