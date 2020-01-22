package UserInterface.utils;

public class intRange {
    private int minValue;
    private int maxValue;

    public intRange(int minValue, int maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public int min() {
        return minValue;
    }

    public int max() {
        return maxValue;
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }
}
