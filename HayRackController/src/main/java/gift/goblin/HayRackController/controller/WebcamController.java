/* 
 * Copyright (C) 2019 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.controller;

import gift.goblin.HayRackController.service.io.WebcamDeviceService;
import java.awt.Dimension;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for the view webcam page.
 *
 * @author andre
 */
@Controller
public class WebcamController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WebcamDeviceService webcamService;

    /**
     * Default render method for the webcam view.
     *
     * @param camNumber which webcam shall get controlled.
     * @param model model with the used attributes in the view.
     * @return name of the view.
     */
    @GetMapping(value = "/webcams/{camNumber}")
    public String defaultRender(Model model, @PathVariable int camNumber) {

        List<Dimension> webcamResolutions = webcamService.getWebcamResolutions(camNumber);
        List<Dimension> sortedDimensions = sortDimensionsAscending(webcamResolutions);
        List<String> concatenatedDimensions = concatenateDimensions(sortedDimensions);

        model.addAttribute("dimensions", concatenatedDimensions);
        model.addAttribute("camNumber", camNumber);
        model.addAttribute("webcam_count", webcamService.getWebcamCount());

        return "webcam";
    }

    /**
     * 
     * @param camNumber camnumber, 1 for the first, 2 for second and so on...
     * @param dimension the dimension (width and height), like '720x520'.
     * @return the image of the webcam as byte-array.
     * @throws IOException 
     */
    @PostMapping(
            value = "/webcams/image",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public @ResponseBody
    byte[] getWebcamPicture(@RequestParam("camNumber") String camNumber,
            @RequestParam("dimension") String dimension) throws IOException {

        Integer camNumberInt = Integer.valueOf(camNumber);
        List<Dimension> webcamResolutions = webcamService.getWebcamResolutions(camNumberInt);
        List<Dimension> sortedDimensions = sortDimensionsAscending(webcamResolutions);
        List<String> concatenatedDimensions = concatenateDimensions(sortedDimensions);
        
        
        if (concatenatedDimensions.contains(dimension)) {
            logger.info("Get image in size {} of webcam no. {}", dimension, camNumberInt);
            Dimension extractedDimension = extractDimension(dimension);
            byte[] pic = webcamService.takePicture(camNumberInt, extractedDimension);
            return Base64.getEncoder().encode(pic);
        } else {
            logger.warn("Cant get image in size {} of webcam no. {} - cause it isnt a supported resolution!", dimension, camNumber);
            return null;
        }
    }

    private List<Dimension> sortDimensionsAscending(List<Dimension> dimensions) {
        return dimensions.stream()
                .sorted((o1, o2) -> Double.valueOf(o1.getWidth()).compareTo(Double.valueOf(o2.getWidth())))
                .collect(Collectors.toList());
    }

    private List<String> concatenateDimensions(List<Dimension> dimensions) {
        List<String> formattedDimensions = dimensions.stream()
                .map(d -> new String(Double.valueOf(d.getWidth()).intValue() + "x" + Double.valueOf(d.getHeight()).intValue()))
                .collect(Collectors.toList());
        return formattedDimensions;
    }

    /**
     * Splits the concatenated dimension and generates a Dimension object of the
     * given width and heigth.
     *
     * @param concatenatedDimension the width and height as one String, e.g.
     * '720x420'.
     * @return the Dimension object.
     */
    private Dimension extractDimension(String concatenatedDimension) throws IllegalArgumentException {
        String[] splitted = concatenatedDimension.split("x");
        if (splitted.length < 2) {
            throw new IllegalArgumentException();
        } else {
            Dimension dimension = new Dimension(Integer.valueOf(splitted[0]), Integer.valueOf(splitted[1]));
            return dimension;
        }
    }

}
