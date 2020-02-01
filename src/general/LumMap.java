package general;

import java.util.*;
import java.util.List;

public class LumMap {

    private Lum[][] lumArray;
    private List<Lum> sortedItems = new ArrayList<>();

    public LumMap(int width, int height) {
        this.lumArray = new Lum[height][width];
    }

    public LumMap(int width, int height, List<Lum> sortedItems) {
        this.lumArray = new Lum[height][width];
        this.sortedItems = sortedItems;
    }

    public void addValuesToMap(int wPosition, int hPosition, Lum newLum) {
        if (this.sortedItems.isEmpty()) {
            sortedItems.add(newLum);
            this.lumArray[hPosition][wPosition] = newLum;
        } else {
            for (int i = 0; i < sortedItems.size(); i++) {
                if (newLum.getValue() > this.sortedItems.get(i).getValue()) {
                    if (i == sortedItems.size() - 1) {
                        sortedItems.add(newLum);
                        this.lumArray[hPosition][wPosition] = newLum;
                        break;
                    } else {
                        continue;
                    }
                } else if (newLum.getValue() == this.sortedItems.get(i).getValue()) {
                    this.lumArray[hPosition][wPosition] = this.sortedItems.get(i);
                    break;
                } else if (newLum.getValue() < this.sortedItems.get(i).getValue()) {
                    sortedItems.add(i, newLum);
                    this.lumArray[hPosition][wPosition] = newLum;
                    break;
                } else {
                    throw new IllegalCallerException();
                }
            }
        }
    }

    public LumMap clone() {
        int width = lumArray[0].length;
        int height = lumArray.length;
        List<Lum> clonedSortedItems = this.cloneSortedItems();
        LumMap clonedLumMap = new LumMap(width, height, clonedSortedItems);
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                Lum luminosity = lumArray[h][w];
                int index = this.sortedItems.indexOf(luminosity);
                clonedLumMap.getLumArray()[h][w] = clonedSortedItems.get(index);
            }
        }
        return clonedLumMap;
    }

    public List<Lum> cloneSortedItems() {
        List<Lum> result = new ArrayList<>();
        for (int i = 0; i < this.sortedItems.size(); i++) {
            int luminosityValue = sortedItems.get(i).getValue();
            Lum luminosity = new Lum(luminosityValue);
            result.add(i, luminosity);
        }
        return result;
    }

    public void setSortedItemsValuesTo(List<Lum> newSortedLumValues) {
        if (newSortedLumValues.size() != this.sortedItems.size()) {
            throw new IndexOutOfBoundsException();
        }
        for (int i = 0; i < this.sortedItems.size(); i++) {
            int newValue = newSortedLumValues.get(i).getValue();
            this.sortedItems.get(i).setValue(newValue);
        }
    }

    //*********************** RESIZE *************************

    public LumMap resizeToNew(int outputWidth, int outputHeight) {
        LumMap resultLumMap = new LumMap(outputWidth, outputHeight);
        double outputWidthRate = (double) this.getWidth() / outputWidth;
        double outputHeightRate = (double) this.getHeight() / outputHeight;
        double outputArea = outputWidthRate * outputHeightRate;

        for (int h = 0; h < outputHeight; h++) {
            for (int w = 0; w < outputWidth; w++) {
                double outputAreaCoord_W0 = w * outputWidthRate;
                double outputAreaCoord_W1 = round((w + 1) * outputWidthRate, 5); //because binary vs decimal
                double outputAreaCoord_H0 = h * outputHeightRate;
                double outputAreaCoord_H1 = round((h + 1) * outputHeightRate, 5); //because binary vs decimal
                double actualValue = 0;

                for (int scanHCoord = (int) outputAreaCoord_H0; scanHCoord < Math.ceil(outputAreaCoord_H1); scanHCoord++) {
                    for (int scanWCoord = (int) outputAreaCoord_W0; scanWCoord < Math.ceil(outputAreaCoord_W1); scanWCoord++) {

                        double intersectAreaCoord_W0 = Math.max(outputAreaCoord_W0, scanWCoord);
                        double intersectAreaCoord_W1 = Math.min(outputAreaCoord_W1, (scanWCoord + 1));
                        double intersectAreaCoord_H0 = Math.max(outputAreaCoord_H0, scanHCoord);
                        double intersectAreaCoord_H1 = Math.min(outputAreaCoord_H1, (scanHCoord + 1));
                        double intersectArea = (intersectAreaCoord_W1 - intersectAreaCoord_W0) * (intersectAreaCoord_H1 - intersectAreaCoord_H0);
                        actualValue += intersectArea * this.lumArray[scanHCoord][scanWCoord].getValue();
                    }
                }
                int actualLumValue = (int) Math.round(actualValue / outputArea);
                Lum actualLum = new Lum(actualLumValue);
                resultLumMap.addValuesToMap(w, h, actualLum);
            }
        }
        return resultLumMap;
    }

    //*********************** RESIZE BULLSHIT *************************

    public LumMap resizeToNew(int outputWidth, int outputHeight, int maxValue) {
        if (outputWidth > maxValue) {
            outputWidth = maxValue;
            throw new IllegalArgumentException();
        }
        if (outputHeight > maxValue) {
            outputHeight = maxValue;
            throw new IllegalArgumentException();
        }
        return resizeToNew(outputWidth, outputHeight);
    }

    public LumMap resizeToNewMaxSize(int maxValue) {
        LumMap result;
        if (this.getHeight() > this.getWidth() && this.getHeight() > maxValue) {
            result = this.resizeToNewKeepRatioBaseHeight(maxValue);
        } else if (this.getWidth() > this.getHeight() && this.getWidth() > maxValue) {
            result = this.resizeToNewKeepRatioBaseWidth(maxValue);
        } else {
            result = this.clone();
        }
        return result;
    }

    public LumMap resizeToNewKeepRatioBaseWidth(int outputWidth) {
        double imageRatio = ((double) this.getHeight()) / this.getWidth();
        int outputHeight = (int) Math.round(imageRatio * outputWidth);
        return resizeToNew(outputWidth, outputHeight);
    }

    public LumMap resizeToNewKeepRatioBaseWidth(int outputWidth, double baseRatio, int maxValue) {
        if (outputWidth > maxValue) {
//            outputWidth = maxValue;
            throw new IllegalArgumentException();
        }
        int outputHeight = (int) Math.round(outputWidth / baseRatio);
        if (outputHeight > maxValue) {
//            outputHeight = maxValue;
//            outputWidth = (int) Math.round(outputHeight * baseRatio);
            throw new IllegalArgumentException();
        }
        return resizeToNew(outputWidth, outputHeight);
    }

    public LumMap resizeToNewKeepRatioBaseHeight(int outputHeight) {
        double imageRatio = ((double) this.getWidth()) / this.getHeight();
        int outputWidth = (int) Math.round(imageRatio * outputHeight);
        return resizeToNew(outputWidth, outputHeight);
    }

    public LumMap resizeToNewKeepRatioBaseHeight(int outputHeight, double baseRatio, int maxValue) {
        if (outputHeight > maxValue) {
//            outputHeight = maxValue;
            throw new IllegalArgumentException();
        }
        int outputWidth = (int) Math.round(outputHeight * baseRatio);
        if (outputWidth > maxValue) {
//            outputWidth = maxValue;
//            outputHeight = (int) Math.round(outputWidth / baseRatio);
            throw new IllegalArgumentException();
        }
        return resizeToNew(outputWidth, outputHeight);
    }

    //*********************** RESIZE BULLSHIT END ************************

    // *********************** MODIFIERS *************************

    public void offset(int offset) {
        for (Lum luminosity : this.sortedItems) {
            int oldValue = luminosity.getValue();
            int newValue = oldValue + offset;
            if (newValue > 255) newValue = 255;
            if (newValue < 0) newValue = 0;
            luminosity.setValue(newValue);
        }
    }

    public void equalize() {
        double itemDistance = 255.0 / (sortedItems.size() - 1);
        for (int i = 0; i < sortedItems.size(); i++) {
            int newValue = (int) Math.round(itemDistance * i);
            sortedItems.get(i).setValue(newValue);
        }
    }

    public void changeRange(int newMinValue, int newMaxValue) {
        int oldMinValue = this.getFirstValue();
        int oldMaxValue = this.getLastValue();
        int oldLength = oldMaxValue - oldMinValue;
        int newLength = newMaxValue - newMinValue;
        double rate = (double)oldLength/newLength;
        for (Lum luminosity : this.sortedItems) {
            int oldValue = luminosity.getValue();
            int newValue = (int) Math.round(((oldValue-oldMinValue) / rate) + newMinValue);
            luminosity.setValue(newValue);
        }
    }

    public void changeMidTone(int newMidToneInPercent) {
        double newMidTone0To1 = (double)newMidToneInPercent /100;
        double coefficient = Math.log(newMidTone0To1) / Math.log(0.25);
        int valueMin = this.getFirstValue();
        int valueMax = this.getLastValue();
        int valueRange = valueMax - valueMin;
        for (Lum luminosity : this.sortedItems) {
            int oldValue = luminosity.getValue();
            double oldValue0To1 = convertToRangeFrom0To1(oldValue, valueMin, valueMax);
            double newValue0To1 = Math.pow ((oldValue0To1 * oldValue0To1), coefficient);
            int newValue = (int) (newValue0To1 * valueRange) + valueMin;
            luminosity.setValue(newValue);
        }
    }

    private double convertToRangeFrom0To1(int value, int valueMin, int valueMax) {
        int originalRangeLength = valueMax - valueMin;
        return ((double)(value - valueMin)) / originalRangeLength;
    }

    // *********************** AUXILIARIES *************************

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public int getHeight() {
        return lumArray.length;
    }

    public int getWidth() {
        return lumArray[0].length;
    }

    public Lum[][] getLumArray() {
        return this.lumArray;
    }

    public Lum getLum(int coordWidth, int coordHeight) {
        return lumArray[coordHeight][coordWidth];
    }

    public int getLumValue(int coordWidth, int coordHeight) {
        return lumArray[coordHeight][coordWidth].getValue();
    }

    public List<Lum> getSortedItems() {
        return sortedItems;
    }

    public int getFirstValue() {
        return sortedItems.get(0).getValue();
    }

    public int getLastValue() {
        return sortedItems.get(sortedItems.size() - 1).getValue();
    }

    public void printLumArray() {
        for (Lum[] luminositySingleRow : lumArray) {
            System.out.println(Arrays.toString(luminositySingleRow));
        }
    }

    @Override
    public String toString() {
        return "width: " + this.getWidth() + ", height: " + this.getHeight();
    }

}
