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

    public void equalizeItemMap() {
        double itemDistance = 255.0 / (sortedItemMap.size() - 1);
        int i = 0;
        for (Map.Entry<Integer, Integer> entry : sortedItemMap.entrySet()) {
            int newValue = (int) Math.round(itemDistance * i);
            entry.setValue(newValue);
            i++;
        }
    }

    public void changeItemMapRange(int newMinValue, int newMaxValue) {
        int oldMinValue = sortedItemMap.get(sortedItemMap.firstKey());
        int oldMaxValue = sortedItemMap.get(sortedItemMap.lastKey());
        int oldLength = oldMaxValue - oldMinValue;
        int newLength = newMaxValue - newMinValue;
        double rate = (double)oldLength/newLength;
        for (Map.Entry<Integer, Integer> entry : sortedItemMap.entrySet()) {
            int oldValue = entry.getValue();
            int newValue = (int) Math.round(((oldValue-oldMinValue) / rate) + newMinValue);
            entry.setValue(newValue);
        }
    }

    //TODO thinking of this a little bit more...
    public void changeMidTone(int newMidToneInPercent) {
        double newMidTone0_1 = (double)newMidToneInPercent /100;
        double coefficient = Math.log10(newMidTone0_1) / Math.log10(0.25);
        int valueMin = sortedItemMap.get(sortedItemMap.firstKey());
        int valueMax = sortedItemMap.get(sortedItemMap.lastKey());
        int valueRange = valueMax - valueMin;
        for (Map.Entry<Integer, Integer> entry : sortedItemMap.entrySet()) {
            int oldValue = entry.getValue();
            double oldValue0_1 = convertToRangeFrom0To1(oldValue, valueMin, valueMax);
            double newValue0_1 = Math.pow ((oldValue0_1 * oldValue0_1), coefficient);
            int newValue = (int) (newValue0_1 * valueRange) + valueMin;
            entry.setValue(newValue);
        }
        this.midTone = newMidToneInPercent;
    }

    //TODO thinking of this a little bit more...
    public double convertToRangeFrom0To1(int value, int valueMin, int valueMax) {
        int originalRangeLength = valueMax - valueMin;
        return ((double)(value - valueMin)) / originalRangeLength;
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

    public int getLuminosityValue(int coordWidth, int coordHeight) {
        return luminosityMap[coordHeight][coordWidth];
    }

    @Override
    public String toString() {
        return "width: " + this.getWidth() + ", height: " + this.getHeight();
    }

}
