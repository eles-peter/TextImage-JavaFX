package general;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class WriteImage {

    private WritableImage writableImage;

    public WriteImage(LumMap lumMap) {
        int width = lumMap.getWidth();
        int height = lumMap.getHeight();
        this.writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                Lum actualLum = lumMap.getLumArray()[h][w];
                int luminosity = actualLum.getValue();
                int red = luminosity;
                int green = luminosity;
                int blue = luminosity;
                int alpha = 255;
                int rgb = (alpha<<24) | (red<<16) | (green<<8) | blue;
                pixelWriter.setArgb(w, h, rgb);
            }
        }
    }

    public WritableImage getWritableImage() {
        return writableImage;
    }
}
