package general;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LumMapInitializer {

    private LumMap lumMap;
    private Set<Lum> itemsSet;

    public LumMapInitializer(int width, int height) {
        List<Lum> sortedItems = new ArrayList<>();
        for (int i = 0; i <= 255; i++) {
            sortedItems.add(new Lum(i));
        }
        this.lumMap = new LumMap(width, height, sortedItems);
        this.itemsSet = new HashSet<>();
    }

    public void addLum(int wPosition, int hPosition, Lum newLum) {
        int newLumIndex = newLum.getValue();
        this.lumMap.getLumArray()[hPosition][wPosition] = this.lumMap.getSortedItems().get(newLumIndex);
        this.itemsSet.add(newLum);
    }

    public void addLumValue(int wPosition, int hPosition, int newLumValue) {
        this.lumMap.getLumArray()[hPosition][wPosition] = this.lumMap.getSortedItems().get(newLumValue);
        this.itemsSet.add(new Lum(newLumValue));
    }

    public LumMap finish() {
        for (int i = 255; i >= 0 ; i--) {
            if (!itemsSet.contains(this.lumMap.getSortedItems().get(i))) {
                this.lumMap.getSortedItems().remove(i);
            }
        }
        this.itemsSet = null;
        return this.lumMap;
    }

}
