package general;


import java.io.*;
import java.util.*;

public class FontCharMap {

    private String name;
    private Double maxHeight;
    private Double maxWidth;
    private SortedMap<Integer, FontChar> fontChars = new TreeMap<>();

    public FontCharMap() {
    }

    public FontCharMap(String fullPathAndFileName) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(fullPathAndFileName));
        String name = scanner.nextLine();
        this.name = name;
        String heightAndWidthString = scanner.nextLine();
        String[] heightAndWidthArray = heightAndWidthString.split(":");
        if (heightAndWidthArray.length == 2) {
            try {
                this.maxHeight = Double.valueOf(heightAndWidthArray[0]);
                this.maxWidth = Double.valueOf(heightAndWidthArray[1]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException();
            }
        } else {
            throw new IllegalArgumentException();
        }
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (!line.isEmpty()) {
                String[] lineArray = line.split(":");
                if (lineArray.length != 3) {
                    throw new IllegalArgumentException();
                }
                int value;
                try {
                    value = Integer.parseInt(lineArray[0]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException();
                }
                if (value < 0 || value > 255) {
                    throw new IllegalArgumentException();
                }
                String unicodeValue = lineArray[1];
                int unicodeInt = 0;
                try {
                    unicodeInt = Integer.parseInt(unicodeValue);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException();
                }
                String actualChar = String.valueOf((char)unicodeInt);
                String fontType = lineArray[2];
                this.add(value, new FontChar(actualChar, fontType));
            }
        }
        scanner.close();
    }

    public void add(Integer value, FontChar fontChar) {
        this.fontChars.put(value, fontChar);
    }

    public void add(Integer value, String unicodeChar, String fontFamily) {
        this.fontChars.put(value, new FontChar(unicodeChar, fontFamily));
    }

    public FontChar get(Integer key) {
        return this.fontChars.get(key);
    }

    public void changeRange(int newMinKey, int newMaxKey) {
        SortedMap<Integer, FontChar> resultFontChars = new TreeMap<>();
        int oldMinKey = this.fontChars.firstKey();
        int oldMaxKey = this.fontChars.lastKey();
        int oldLength = oldMaxKey - oldMinKey;
        int newLength = newMaxKey - newMinKey;
        double rate = (double)oldLength/newLength;

        for(Map.Entry<Integer,FontChar> entry : this.fontChars.entrySet()) {
            Integer oldKey = entry.getKey();
            FontChar value = entry.getValue();
            Integer newKey = (int) Math.round(((oldKey-oldMinKey) / rate) + newMinKey);
            resultFontChars.put(newKey, value);
        }
        this.setFontChars(resultFontChars);
    }

    public void inverse() {
        SortedMap<Integer, FontChar> resultFontChars = new TreeMap<>();

        for(Map.Entry<Integer,FontChar> entry : this.fontChars.entrySet()) {
            Integer oldKey = entry.getKey();
            FontChar value = entry.getValue();
            Integer newKey = 255 - oldKey;
            resultFontChars.put(newKey, value);
        }
        this.setFontChars(resultFontChars);
    }

    public FontCharMap createInverse() {
        FontCharMap result = new FontCharMap();
        for (Map.Entry<Integer, FontChar> entry : this.fontChars.entrySet()) {
            Integer newValue = 255 - entry.getKey();
            FontChar newFontCahr = entry.getValue();
            result.add(newValue, newFontCahr);
        }
        result.setMaxWidth(this.maxWidth);
        result.setMaxHeight(this.maxHeight);
        result.setName(this.name + "inverse");
        return result;
    }

    public void fillTheGap() {
        Integer previousKey = this.fontChars.firstKey();

        for (int key = this.fontChars.firstKey(); key <= this.fontChars.lastKey(); key++) {
            if (fontChars.containsKey(key)) {
                if (key - previousKey > 1) {
                    int keyGapSize = key - previousKey;
                    for (int newKey = previousKey + 1; newKey < previousKey + keyGapSize / 2; newKey++) {
                        this.fontChars.put(newKey, this.fontChars.get(previousKey));
                    }
                    for (int newKey = previousKey + keyGapSize / 2; newKey < key; newKey++) {
                        this.fontChars.put(newKey, fontChars.get(key));
                    }
                    previousKey = key;
                }
            }
        }
    }

    public void writeToFile(String fullPathAndFileName) throws IOException {
        FileWriter writer = new FileWriter(fullPathAndFileName);
        writer.write(this.name + "\n");
        writer.write(this.maxHeight.toString() + ":" + this.maxWidth.toString() + "\n");
        for (Map.Entry<Integer, FontChar> entry : this.fontChars.entrySet()) {
            StringBuilder line = new StringBuilder("");
            line.append(entry.getKey());
            line.append(":");
            String actualString = entry.getValue().getUnicodeChar();
            line.append((int) actualString.charAt(0));
            line.append(":");
            line.append(entry.getValue().getFontFamily());
            line.append("\n");
            writer.write(line.toString());
        }
        writer.close();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(Double maxWidth) {
        this.maxWidth = maxWidth;
    }

    public Double getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(Double maxHeight) {
        this.maxHeight = maxHeight;
    }

    public SortedMap<Integer, FontChar> getFontChars() {
        return fontChars;
    }

    public void setFontChars(SortedMap<Integer, FontChar> fontChars) {
        this.fontChars = fontChars;
    }

    public Integer getLastKey() {
        return fontChars.lastKey();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Name: " + this.name + "\n");
        result.append("Max-Height: " + this.maxHeight + ", Max-Width: " + this.maxWidth + "\n");
        for (Map.Entry<Integer, FontChar> entry : this.fontChars.entrySet()) {
            result.append("Value: ");
            result.append(entry.getKey());
            result.append(" " + entry.getValue() + "\n");
        }
        return result.toString();
    }
}
