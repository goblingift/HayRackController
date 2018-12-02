/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.service.io;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.ds.buildin.WebcamDefaultDriver;
import com.github.sarxos.webcam.ds.v4l4j.V4l4jDriver;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Bean which offers several functions for accessing the connected webcams.
 *
 * @author andre
 */
@Component
public class WebcamDeviceService {

    @Autowired
    ShutterController shutterController;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private List<Webcam> webcams = new ArrayList<>();

    private final Dimension dimensionHd = new Dimension(1280, 720);
    private final Dimension dimensionSd = new Dimension(320, 240);

    /**
     * Setup the webcam- it is important, to use the correct webcam driver,
     * depending on the environment (raspberry / laptop).
     */
    @PostConstruct
    private void setupWebcams() {

        try {
            if (shutterController.isRaspberryInitialized()) {
                Webcam.setDriver(new V4l4jDriver());
            } else {
                Webcam.setDriver(new WebcamDefaultDriver());
            }

            webcams = Webcam.getWebcams();

            for (Webcam actWebcam : webcams) {
                logger.info("Initialized webcam: {}", actWebcam.getName());
                List<Dimension> dimensions = Arrays.asList(actWebcam.getViewSizes());
                dimensions.stream().forEach(d -> logger.info("Available dimension: {} x {}", d.getWidth(), d.getHeight()));
            }

        } catch (Exception e) {
            logger.error("Couldnt initialize webcams!", e);
        }
    }

    public int getWebcamCount() {
        return webcams.size();
    }

    /**
     * Reads all available resolutions for the given webcam.
     *
     * @param webcamNumber for which webcam you want to read the available
     * resolutions. 1 for the first webcam, 2 for the next.
     * @return list with available dimensions. Can be empty, but not null.
     */
    public List<Dimension> getWebcamResolutions(int webcamNumber) {
        List<Dimension> dimensions = new ArrayList<>();
        try {
            Webcam webcam = webcams.get(webcamNumber - 1);
            dimensions = Arrays.asList(webcam.getViewSizes());
        } catch (IndexOutOfBoundsException e) {
            logger.error("Exception while getting webcam no. " + webcamNumber, e);
        }

        return dimensions;
    }

    /**
     * Takes a picture with a specific webcam device.
     *
     * @param camNumber number of the device- 1 is the first one, 2 for the
     * next, so on.
     * @param resolutionHd true if you want HD resolution (1280x720p). False for
     * smaller resolution (640x480p).
     * @return the picture as byte array.
     */
    public byte[] takePicture(int camNumber, boolean resolutionHd) {
        if (resolutionHd) {
            return takePicture(camNumber, dimensionHd);
        } else {
            return takePicture(camNumber, dimensionSd);
        }
    }

    public byte[] takePicture(int camNumber, Dimension dimension) {

        try {
            // subtract 1, cause lists are zero based
            Webcam webcam = webcams.get(camNumber - 1);

            if (!webcam.isOpen()) {
                webcam.setViewSize(dimension);

                webcam.open();
                
                BufferedImage image = webcam.getImage();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", baos);
                baos.flush();
                byte[] imageInByte = baos.toByteArray();
                logger.info("Successful took picture on cam {} - result size: {} bytes, resolution: {}x{} pixel", camNumber, imageInByte.length,
                        webcam.getViewSize().getWidth(), webcam.getViewSize().getHeight());
                baos.close();

                webcam.close();
                return imageInByte;
            } else {
                logger.info("Webcam {} is already open- skip taking picture and close !");
                webcam.close();
                return null;
            }

        } catch (IOException e) {
            logger.error("Exception while takePicture from webcam #" + camNumber, e);
            return null;
        }
    }

}
