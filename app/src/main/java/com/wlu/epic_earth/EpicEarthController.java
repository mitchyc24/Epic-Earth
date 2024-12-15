package com.wlu.epic_earth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.wlu.epic_earth.nasa.EPICImage;

import java.util.List;

@Controller
public class EpicEarthController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("dateForm", new DateForm());
        return "index";
    }

    @PostMapping("/retrieve-images")
    public String retrieveImages(@RequestParam("date") String date, Model model) {
        List<EPICImage> images = EpicEarthApplication.retrieveImages(date);
        model.addAttribute("images", images);
        model.addAttribute("dateForm", new DateForm());
        return "index";
    }

    @GetMapping("/{date}")
    public String generateGif(@PathVariable("date") String date, Model model) {
        List<EPICImage> images = EpicEarthApplication.retrieveImages(date);
        model.addAttribute("images", images);
        String gifPath = "data/gifs/" + date + ".gif";
        model.addAttribute("gifPath", gifPath);
        return "gif";
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