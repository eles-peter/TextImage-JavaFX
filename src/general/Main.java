package general;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        ReadImageFile RGBImage = new ReadImageFile("C:\\Users\\Pepa\\Desktop\\TextImage", "big-colorful-lollipop.jpg");
        Luminosity test01 = new Luminosity(RGBImage.convertToLuminosityArray());



        Modifier modifier = new Modifier();

        modifier.getValuesFrom(test01.getSortedItemMap());

        modifier.changeRange(200,255);
        modifier.changeMidTone(80);

        Luminosity test02 = test01.createModifiedLuminosity(modifier);

        test02.printSortedItemMap();




        WriteImageFile outputImage = new WriteImageFile();
        outputImage.createFromLuminosityArray(test01.getLuminosityMap());
        outputImage.writeToJPG("C:\\Users\\Pepa\\Desktop\\TextImage", "big-colorful-lollipop_Grey.jpg" );
        outputImage.createFromLuminosityArray(test02.getLuminosityMap());
        outputImage.writeToJPG("C:\\Users\\Pepa\\Desktop\\TextImage", "big-colorful-lollipop_Grey_mod2.jpg" );

    }
}
