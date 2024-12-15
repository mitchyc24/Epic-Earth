package com.wlu.epic_earth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.wlu.epic_earth.nasa.EPICImage;
import java.io.IOException;

import java.io.File;
import java.util.List;

@Controller
public class EpicEarthController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("dateForm", new DateForm());
        return "index";
    }

    @PostMapping("/retrieve-images")
    public String retrieveImages(@RequestParam("date") String date, RedirectAttributes redirectAttributes) {
        return "redirect:/" + date;
    }

    @GetMapping("/{date}")
    public String retrieveGif(@PathVariable("date") String date, Model model) {
        String gifPath = "data/gifs/" + date + ".gif";
        File gifFile = new File(gifPath);

        if (!gifFile.exists()) {
            List<EPICImage> images = EpicEarthApplication.retrieveImages(date);
            model.addAttribute("images", images);
            try {
                EpicEarthApplication.createGif(images, gifPath);
            } catch (IOException e) {
                e.printStackTrace();
                // Handle the exception, e.g., add an error message to the model
                model.addAttribute("errorMessage", "Failed to create GIF for the date: " + date);
                return "error";
            }
        }

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