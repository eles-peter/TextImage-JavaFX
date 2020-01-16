package general;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ReadImageFile {

    private BufferedImage image;

    public ReadImageFile(String directoryPath, String fileName) {
        String fullFileName = directoryPath + "\\" + fileName;
        try {
            this.image = ImageIO.read(new File(fullFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ReadImageFile(String fullPathAndFileName) {
        try {
            this.image = ImageIO.read(new File(fullPathAndFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //TODO should test the speed...
    public int[][] convertToLuminosityArray() {
        int height = image.getHeight();
        int width = image.getWidth();
        int[][] luminosityArray = new int[height][width];
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                int pixel = image.getRGB(w, h);
                int alpha = (pixel >> 24) & 0xff; //TODO deletable?
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;
//                int luminosity = (int) Math.round(((double)(red + green + blue))/3);
                int luminosity = (int) Math.round(0.299 * (double) red + 0.587 * (double) green + 0.114 * (double) blue);
                luminosityArray[h][w] = luminosity;
            }
        }
        return luminosityArray;
    }

}