package userinterface.utils;

import java.util.Objects;

public class IntRange {
    private int minValue;
    private int maxValue;

    public IntRange(int minValue, int maxValue) {
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

    public void setMin(int minValue) {
        this.minValue = minValue;
    }

    public void setMax(int maxValue) {
        this.maxValue = maxValue;
    }

    public void set(int newMinValue, int newMaxValue) {
        this.minValue = newMinValue;
        this.maxValue = newMaxValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntRange intRange = (IntRange) o;
        return minValue == intRange.minValue &&
                maxValue == intRange.maxValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minValue, maxValue);
    }

    @Override
    public String toString() {
        return "intRange{" +
                "minValue=" + minValue +
                ", maxValue=" + maxValue +
                '}';
    }
}
