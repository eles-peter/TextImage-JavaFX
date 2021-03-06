package general;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ReadImageFile {

    private BufferedImage image;

    public ReadImageFile() {
    }

    public ReadImageFile(String directoryPath, String fileName) throws IOException {
        String fullFileName = directoryPath + "\\" + fileName;
        this.image = ImageIO.read(new File(fullFileName));
    }

    public ReadImageFile(String fullPathAndFileName) throws IOException {
        this.image = ImageIO.read(new File(fullPathAndFileName));
    }

    public ReadImageFile(File openFile) throws IOException {
        this.image = ImageIO.read(openFile);
    }

    public List<String> getFormatNames() {
        String[] result= ImageIO.getReaderFormatNames();
        return  Arrays.asList(result);
     }

    public int[][] convertToLuminosityArray() {
        int height = image.getHeight();
        int width = image.getWidth();
        int[][] luminosityArray = new int[height][width];
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                int pixel = image.getRGB(w, h);
                int alpha = (pixel >> 24) & 0xff; //TODO deletable? vagy figyelembe vétele.... kettészedni a methodot...
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;
//                int luminosity = (int) Math.round(((double)(red + green + blue))/3);
                int luminosity = (int) Math.round(0.299 * (double) red + 0.587 * (double) green + 0.114 * (double) blue); //TODO felülvizsgálni a képletet!!!
                luminosityArray[h][w] = luminosity;
            }
        }
        return luminosityArray;
    }

    public LumMap convertToLumMap() {
        int height = image.getHeight();
        int width = image.getWidth();
        LumMapInitializer lumMapInitializer = new LumMapInitializer(width, height);
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                int pixel = image.getRGB(w, h);
                int alpha = (pixel >> 24) & 0xff; //TODO deletable? vagy figyelembe vétele.... kettészedni a methodot...
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;
                int luminosity = (int) Math.round(0.299 * (double) red + 0.587 * (double) green + 0.114 * (double) blue); //TODO felülvizsgálni a képletet!!!
                lumMapInitializer.addLumValue(w, h, luminosity);
            }
        }
        return lumMapInitializer.finish();
    }





}