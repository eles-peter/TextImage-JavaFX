package general;


import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;


import java.io.IOException;
import java.util.List;


public class Main {
    public static void main(String[] args) throws IOException {



        List<String> fontNames = Font.getFontNames();
        System.out.println(fontNames);

        List<String> fontFamilies = Font.getFamilies();
        System.out.println(fontFamilies);








        long startTime = System.nanoTime();



//        for (int i = 0; i < 100; i++) {
//            lumMap.setSortedItemsValuesTo(lumMapBase.getSortedItems());
//            lumMap.changeMidTone(80);
//            lumMap.equalize();
//            lumMap.offset(-100);
//            lumMap.changeRange(150, 230);
//        }


        long endTime   = System.nanoTime();
        long totalTime = endTime - startTime;
        double totalMilliSeconds = totalTime / 1000000;
        System.out.println("futási idő: " + totalMilliSeconds + " millisec");

//        lumMapBase.printLumArray();
//        System.out.println(lumMapBase.getSortedItems());
//
//        LumMap lumMap2 = lumMapBase.resizeToNew(4, 4);
//        LumMap lumMap2 = lumMapBase.clone();
//        lumMap2.printLumArray();
//        System.out.println(lumMap2.getSortedItems());
//        System.out.println("Iemlista hossza:" + lumMap2.getSortedItems().size());
//
//        lumMap2.changeRange(200,255);
//        lumMap2.printLumArray();
//        System.out.println(lumMap2.getSortedItems());





    }

}
