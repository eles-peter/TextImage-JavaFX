package general;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WriteImageFile {

    BufferedImage image;

    public void createFromLuminosityArray(int[][] luminosityArray) {
        int width = luminosityArray[0].length;
        int height = luminosityArray.length;
        image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                int luminosity = luminosityArray[h][w];
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