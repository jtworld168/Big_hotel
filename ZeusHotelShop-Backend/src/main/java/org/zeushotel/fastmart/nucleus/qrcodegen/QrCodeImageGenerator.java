package org.zeushotel.fastmart.nucleus.qrcodegen;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class QrCodeImageGenerator {
    
    private static final int DEFAULT_QR_PIXEL_SIZE = 300;
    private static final int BORDER_PADDING = 2;
    
    public String generateBase64QrImage(String contentData, Integer pixelSize) {
        try {
            int actualSize = (pixelSize != null && pixelSize > 0) ? pixelSize : DEFAULT_QR_PIXEL_SIZE;
            
            Map<EncodeHintType, Object> encodingHints = new HashMap<>();
            encodingHints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            encodingHints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            encodingHints.put(EncodeHintType.MARGIN, BORDER_PADDING);
            
            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                contentData, 
                BarcodeFormat.QR_CODE, 
                actualSize, 
                actualSize, 
                encodingHints
            );
            
            BufferedImage qrImage = createQrBufferedImage(bitMatrix);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "PNG", outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            
            String base64String = Base64.getEncoder().encodeToString(imageBytes);
            
            log.info("二维码生成成功 - 尺寸: {}x{}", actualSize, actualSize);
            
            return "data:image/png;base64," + base64String;
            
        } catch (Exception exception) {
            log.error("二维码生成失败", exception);
            return null;
        }
    }
    
    private BufferedImage createQrBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);
        
        graphics.setColor(Color.BLACK);
        for (int xPos = 0; xPos < width; xPos++) {
            for (int yPos = 0; yPos < height; yPos++) {
                if (matrix.get(xPos, yPos)) {
                    graphics.fillRect(xPos, yPos, 1, 1);
                }
            }
        }
        
        graphics.dispose();
        return image;
    }
}
