/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.service;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author msi_ge72
 */
public class ImageService {

    public String processImage(String base64Image) {
        base64Image=base64Image.replace(" ", "+");
        String processedImage="";
        try {
            byte[] byteImage = Base64.getDecoder().decode(base64Image);
            String imageExtension=URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(byteImage)).split("/")[1];
            BufferedImage imBuff = ImageIO.read(new ByteArrayInputStream(byteImage));
            BufferedImage resizedImBuff;
            double height = (double) imBuff.getHeight();
            double width = (double) imBuff.getWidth();
            if (height > width && height > 100) {
                resizedImBuff = scale(imBuff, (int) (width / (height / 100)), 100);
            } else if (height < width && width > 200) {
                resizedImBuff = scale(imBuff, 200, (int) (height / (width / 200)));
            } else {
                resizedImBuff = imBuff;
            }
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(resizedImBuff, imageExtension, os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            byte[] bytes = IOUtils.toByteArray(is);
            processedImage=Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            Logger.getLogger(ImageService.class.getName()).log(Level.SEVERE, null, e);
        }
        return processedImage;
    }

    public static BufferedImage scale(BufferedImage imageToScale, int dWidth, int dHeight) {
        BufferedImage scaledImage = null;
        if (imageToScale != null) {
            scaledImage = new BufferedImage(dWidth, dHeight, imageToScale.getType());
            Graphics2D graphics2D = scaledImage.createGraphics();
            graphics2D.drawImage(imageToScale, 0, 0, dWidth, dHeight, null);
            graphics2D.dispose();
        }
        return scaledImage;
    }
}
