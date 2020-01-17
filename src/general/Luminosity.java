package general;

import java.util.*;

public class Luminosity {

    private int[][] luminosityMap;
    private SortedMap<Integer, Integer> sortedItemMap;
    private int midTone;

    public Luminosity(int width, int height) {
        this.luminosityMap = new int[height][width];
        this.sortedItemMap = new TreeMap<>();
        this.midTone = 50;
    }

    public Luminosity(int[][] luminosityMap) {
        this.luminosityMap = luminosityMap;
        this.sortedItemMap = new TreeMap<>();
        this.actualizesSortedItemMap();
        this.midTone = 50;
    }

    public void addLuminosityValuesToMap(int wPosition, int hPosition, int luminosity) {
        this.luminosityMap[hPosition][wPosition] = luminosity;
        this.sortedItemMap.put(luminosity, luminosity); // Lehet az egészet aktualizálni kell!!!
    }

    public Luminosity clone() {
        int width = luminosityMap[0].length;
        int height = luminosityMap.length;
        Luminosity result = new Luminosity(width, height);
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                int luminosity = luminosityMap[h][w];
                result.addLuminosityValuesToMap(w, h, luminosity);
            }
        }
        return result;
    }

    public void actualizesSortedItemMap() {
        this.sortedItemMap.clear();
        for (int h = 0; h < luminosityMap.length; h++) {
            for (int w = 0; w < luminosityMap[0].length; w++) {
                int luminosity = luminosityMap[h][w];
                this.sortedItemMap.put(luminosity, luminosity);
            }
        }
    }

    public void sortedItemMapValueToKey() {
        SortedMap<Integer, Integer> tempMap = new TreeMap<>();
        for (Map.Entry<Integer, Integer> entry : sortedItemMap.entrySet()) {
            tempMap.put(entry.getValue(), entry.getValue());
        }
        sortedItemMap = tempMap;
    }

    public Luminosity resizeToNew(int outputWidth, int outputHeight) {
        Luminosity resultLuminosity = new Luminosity(outputWidth, outputHeight);
        double outputWidthRate = (double) this.getWidth() / outputWidth;
        double outputHeightRate = (double) this.getHeight() / outputHeight;
        double outputArea = outputWidthRate * outputHeightRate;

        for (int h = 0; h < outputHeight; h++) {
            for (int w = 0; w < outputWidth; w++) {
                double outputAreaCoord_W0 = w * outputWidthRate;
                double outputAreaCoord_W1 = round((w+1) * outputWidthRate, 5); //because binary vs decimal
                double outputAreaCoord_H0 = h * outputHeightRate;
                double outputAreaCoord_H1 = round((h+1) * outputHeightRate, 5); //because binary vs decimal
                double actualValue=0;

                for (int scanHCoord = (int)outputAreaCoord_H0; scanHCoord < Math.ceil(outputAreaCoord_H1) ; scanHCoord++) {
                    for (int scanWCoord = (int)outputAreaCoord_W0; scanWCoord < Math.ceil(outputAreaCoord_W1) ; scanWCoord++) {

                        double intersectAreaCoord_W0 = Math.max(outputAreaCoord_W0, scanWCoord);
                        double intersectAreaCoord_W1 = Math.min(outputAreaCoord_W1, (scanWCoord + 1));
                        double intersectAreaCoord_H0 = Math.max(outputAreaCoord_H0, scanHCoord);
                        double intersectAreaCoord_H1 = Math.min(outputAreaCoord_H1, (scanHCoord + 1));
                        double intersectArea = (intersectAreaCoord_W1-intersectAreaCoord_W0) * (intersectAreaCoord_H1-intersectAreaCoord_H0);
                        actualValue += intersectArea * this.luminosityMap[scanHCoord][scanWCoord];
                    }
                }
                int actualLuminosity = (int) Math.round(actualValue / outputArea);
                resultLuminosity.addLuminosityValuesToMap(w, h, actualLuminosity);
            }
        }
        return resultLuminosity;
    }

    public Luminosity resizeToNewKeepRatioBaseWidth(int outputWidth) {
        double imageRatio = ((double) this.getHeight()) / this.getWidth();
        int outputHeight = (int)Math.round(imageRatio * outputWidth);
        return resizeToNew(outputWidth, outputHeight);
    }

    public Luminosity resizeToNewKeepRatioBaseHeight(int outputHeight) {
        double imageRatio = ((double) this.getWidth()) / this.getHeight();
        int outputWidth = (int)Math.round(imageRatio * outputHeight);
        return resizeToNew(outputWidth, outputHeight);
    }

    public Luminosity createModifiedLuminosity(Modifier modifier) {
        int width = luminosityMap[0].length;
        int height = luminosityMap.length;
        Luminosity result = new Luminosity(width, height);
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                int oldLuminosity = luminosityMap[h][w];
                int newLuminosity =  modifier.get(oldLuminosity);
                result.addLuminosityValuesToMap(w, h, newLuminosity);
            }
        }
        return result;
    }

     public void applyItemMapToArray() {
        for (int h = 0; h < luminosityMap.length; h++) {
            for (int w = 0; w < luminosityMap[0].length; w++) {
                int oldLuminosity = luminosityMap[h][w];
                int newLuminosity = sortedItemMap.get(oldLuminosity);
                luminosityMap[h][w] = newLuminosity;
            }
        }
        sortedItemMapValueToKey();
    }

    public void applyAnItemMapToArray(SortedMap<Integer, Integer> itemMap) {
        for (int h = 0; h < luminosityMap.length; h++) {
            for (int w = 0; w < luminosityMap[0].length; w++) {
                int oldLuminosity = luminosityMap[h][w];
                int newLuminosity = itemMap.get(oldLuminosity);
                luminosityMap[h][w] = newLuminosity;
            }
        }
        sortedItemMapValueToKey(); //TODO átgondolni az egészet!!!
    }

    public static double round (double value, int places){
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp/factor;
    }

    public void printSortedItemMap() {
        for (Map.Entry<Integer, Integer> entry : sortedItemMap.entrySet()) {
            System.out.println(entry.getKey() + "->" + entry.getValue());
        }
    }

    public void printLuminosityArray() {
        for (int[] luminositySingleRow : luminosityMap) {
            System.out.println(Arrays.toString(luminositySingleRow));
        }
    }

    public int getHeight() {
        return luminosityMap.length;
    }

    public int getWidth() {
        return luminosityMap[0].length;
    }

    public int[][] getLuminosityMap() {
        return this.luminosityMap;
    }

    public int getMidTone() {
        return midTone;
    }

    public void setMidTone(int midTone) {
        this.midTone = midTone;
    }

    public SortedMap<Integer, Integer> getSortedItemMap() {
        return sortedItemMap;
    }

    public int getLuminosityValue(int coordWidth, int coordHeight) {
        return luminosityMap[coordHeight][coordWidth];
    }

    @Override
    public String toString() {
        return "width: " + this.getWidth() + ", height: " + this.getHeight();
    }

}
