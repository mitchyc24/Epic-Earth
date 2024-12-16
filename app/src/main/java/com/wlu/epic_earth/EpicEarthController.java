package com.wlu.epic_earth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;

import com.wlu.epic_earth.nasa.EpicImage;
import com.wlu.epic_earth.nasa.EpicImageDao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import java.text.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Controller
public class EpicEarthController {

    private static final Logger logger = LoggerFactory.getLogger(EpicEarthController.class);

    @Autowired
    private EpicImageDao epicImageDao;

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
            logger.info("\n-----------------------------------------\n\nIMAGES: " + epicImages + "\n--------------------------\n\n\n");
            model.addAttribute("images", epicImages);
        } catch (ParseException e) {
            model.addAttribute("error", "Invalid date format");
        }
        return "image-gallery";
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