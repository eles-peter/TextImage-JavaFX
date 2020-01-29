package general;

import java.util.Objects;

public class Lum implements Comparable<Lum> {

    private int value;

    public Lum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    //TODO check the method
    @Override
    public int compareTo(Lum otherLum) {
        return Integer.compare(this.value, otherLum.getValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lum lum = (Lum) o;
        return value == lum.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Lum{" +
                "value=" + value +
                '}';
    }
}
