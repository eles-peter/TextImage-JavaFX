package general;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WriteImageFile {

    private BufferedImage image;

    public void createFromLumMap(LumMap lumMap) {
        int width = lumMap.getWidth();
        int height = lumMap.getHeight();
        image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                Lum actualLum = lumMap.getLumArray()[h][w];
                int luminosity = actualLum.getValue();
                int red = luminosity;
                int green = luminosity;
                int blue = luminosity;
                int alpha = 255;
                int rgb = (alpha<<24) | (red<<16) | (green<<8) | blue;
                image.setRGB(w, h, rgb);
            }
        }
    }

    public void writeToJPG(String directoryPath, String fileName) {
        String fullFileName = directoryPath + "\\" + fileName;
        try {
            ImageIO.write(image, "jpg", new File(fullFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("File is written");
    }

}