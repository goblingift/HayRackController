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
public class WebcamController {

    @Autowired
    ShutterController shutterController;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private List<Webcam> webcams = new ArrayList<>();

    private Dimension hd = new Dimension(1280, 720);
    private Dimension sd = new Dimension(640, 480);

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
            webcams.stream().forEach(wc -> logger.info("Initialized webcam: {}", wc.getName()));
        } catch (Exception e) {
            logger.error("Couldnt initialize webcams!", e);
        }
    }

    public int getWebcamCount() {
        return webcams.size();
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

        try {
            Webcam webcam = webcams.get(camNumber);
            
            webcam.open();
            
            BufferedImage image = webcam.getImage();
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            logger.info("Successful took picture on cam {} - result size: ", camNumber, imageInByte.length);
            baos.close();
            
            webcam.close();
            return imageInByte;
        } catch (IOException e) {
            logger.error("Exception while takePicture from webcam #" + camNumber, e);
            return null;
        }
    }

}
