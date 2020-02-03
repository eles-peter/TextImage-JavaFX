package general;


import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {

//
//        ReadImageFile readImageFile = new ReadImageFile("C:\\Users\\Pepa\\Desktop\\TextImage\\1713238.jpg");
//
        FontCharMap fontCharMap = new FontCharMap("C:\\Users\\Pepa\\Desktop\\TextImage\\ASCII_consolas.txt");


//        fontCharMap.writeToFile("C:\\Users\\Pepa\\Desktop\\TextImage\\test.txt");

//        FontCharMap fontCharMap = new FontCharMap("C:\\Users\\Pepa\\Desktop\\TextImage\\test.txt");
//        System.out.println(fontCharMap);

        FontCharMap inverse = fontCharMap.createInverse();
        inverse.writeToFile("C:\\Users\\Pepa\\Desktop\\TextImage\\inverse.txt");






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
