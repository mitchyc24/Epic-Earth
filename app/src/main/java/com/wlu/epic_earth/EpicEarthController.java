package com.wlu.epic_earth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wlu.epic_earth.nasa.EpicImage;
import com.wlu.epic_earth.nasa.EpicImageDao;
import com.wlu.epic_earth.nasa.GifDao;
import com.wlu.epic_earth.nasa.Gif;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.text.ParseException;
import java.awt.image.BufferedImage;


@Controller
public class EpicEarthController {

    @Autowired
    private EpicImageDao epicImageDao;

    @Autowired
    private GifDao gifDao;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("dateForm", new DateForm());
        return "index";
    }

    @PostMapping("/gif/{date}")
    public String gif(@RequestParam("date") String dateStr, RedirectAttributes redirectAttributes, Model model) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = dateFormat.parse(dateStr);

            List<EpicImage> epicImages = epicImageDao.getEpicImagesByDate(date);
            if (epicImages.isEmpty()) {
                epicImages = epicImageDao.fetchImagesFromApi(date);
            }

            if (epicImages == null || epicImages.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Failed to retrieve images for date " + dateStr);
                return "redirect:/";
            }

            List<BufferedImage> images = epicImages.stream()
                .map(EpicImage::getImage)
                .collect(Collectors.toList());

            byte[] gifData = EpicEarthApplication.createGif(images);

            // Save the GIF to the database
            Gif gif = new Gif(null, dateStr + ".gif", date, gifData);
            gifDao.saveGif(gif);

            model.addAttribute("gifData", gif.getData());
            model.addAttribute("date", dateStr);
            return "gif";
        } catch (ParseException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid date format: " + dateStr);
            return "redirect:/";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create gif for date " + dateStr);
            return "redirect:/";
        }
    }

    @GetMapping("/gif/{date}")
    public String getGifByDate(@PathVariable("date") String dateStr, Model model, RedirectAttributes redirectAttributes) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = dateFormat.parse(dateStr);

            Gif gif = gifDao.getGifByDate(date);
            if (gif == null) {
                redirectAttributes.addFlashAttribute("error", "No GIF found for date " + dateStr);
                return "redirect:/";
            }

            model.addAttribute("gifData", gif.getData());
            model.addAttribute("date", dateStr);
            return "gif";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to retrieve gif for date " + dateStr);
            return "redirect:/";
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