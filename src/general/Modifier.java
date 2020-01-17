package general;

import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

public class Modifier {

    private SortedMap<Integer, Integer> sortedItemMap;

    public Modifier() {
        this.sortedItemMap = new TreeMap<>();
    }

    public Modifier(SortedMap<Integer, Integer> sortedItemMap) {
        this.sortedItemMap = sortedItemMap;
    }

    public void getValuesFrom(SortedMap<Integer, Integer> sortedMap) {
        this.sortedItemMap = new TreeMap<>();
        for (Map.Entry<Integer, Integer> entry : sortedMap.entrySet()) {
            sortedItemMap.put(entry.getKey(), entry.getValue());
        }
    }

    public void getValuesFrom(SortedSet<Integer> sortedSet) {
        this.sortedItemMap = new TreeMap<>();
        for (Integer integer : sortedSet) {
            sortedItemMap.put(integer, integer);
        }
    }

    public Integer get(Integer key) {
        return sortedItemMap.get(key);
    }

    public void clear() {
        this.sortedItemMap.clear();
    }

    public void resetValues() {
        for (Map.Entry<Integer, Integer> entry : sortedItemMap.entrySet()) {
            entry.setValue(entry.getKey());
        }
    }

    public void valueToKey() {
        SortedMap<Integer, Integer> tempMap = new TreeMap<>();
        for (Map.Entry<Integer, Integer> entry : sortedItemMap.entrySet()) {
            tempMap.put(entry.getValue(), entry.getValue());
        }
        sortedItemMap = tempMap;
    }

    public void equalizeI() {
        double itemDistance = 255.0 / (sortedItemMap.size() - 1);
        int i = 0;
        for (Map.Entry<Integer, Integer> entry : sortedItemMap.entrySet()) {
            int newValue = (int) Math.round(itemDistance * i);
            entry.setValue(newValue);
            i++;
        }
    }

    public void changeRange(int newMinValue, int newMaxValue) {
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
    }

    //TODO thinking of this a little bit more...
    public double convertToRangeFrom0To1(int value, int valueMin, int valueMax) {
        int originalRangeLength = valueMax - valueMin;
        return ((double)(value - valueMin)) / originalRangeLength;
    }

    public void printSortedItemMap() {
        for (Map.Entry<Integer, Integer> entry : sortedItemMap.entrySet()) {
            System.out.println(entry.getKey() + "->" + entry.getValue());
        }
    }


}
