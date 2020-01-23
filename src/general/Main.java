package general;

import javafx.stage.FileChooser;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        ReadImageFile rgbimage = new ReadImageFile();
        for (String formatName : rgbimage.getFormatNames()) {
            System.out.println("*."+formatName);
        }



    }

}
