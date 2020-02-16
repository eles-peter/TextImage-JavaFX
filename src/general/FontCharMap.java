package general;


import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FontCharMap {

    private Map<Integer, FontChar> fontCharMap = new HashMap<>();
    private String name;
    private Double heightWidthRatio;

    public FontCharMap() {
    }

    public FontCharMap(String fullPathAndFileName) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(fullPathAndFileName));
        String name = scanner.nextLine();
        this.name = name;
        String heightWidthRatioString = scanner.nextLine();
        try {
            this.heightWidthRatio = Double.valueOf(heightWidthRatioString);
        } catch (NumberFormatException e) {
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
        this.fontCharMap.put(value, fontChar);
    }

    public FontChar get(Integer key) {
        return this.fontCharMap.get(key);
    }

    public FontCharMap createInverse() {
        FontCharMap result = new FontCharMap();
        for (Map.Entry<Integer, FontChar> entry : this.fontCharMap.entrySet()) {
            Integer newValue = 255 - entry.getKey();
            FontChar newFontCahr = entry.getValue();
            result.add(newValue, newFontCahr);
        }
        return result;
    }

    public void writeToFile(String fullPathAndFileName) throws IOException {
        FileWriter writer = new FileWriter(fullPathAndFileName);
        writer.write(this.name + "\n");
        writer.write(this.heightWidthRatio.toString() + "\n");
        for (Map.Entry<Integer, FontChar> entry : this.fontCharMap.entrySet()) {
            StringBuilder line = new StringBuilder("");
            line.append(entry.getKey());
            line.append(":");
            line.append(entry.getValue().getUnicodeChar());
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

    public Double getHeightWidthRatio() {
        return heightWidthRatio;
    }

    public void setHeightWidthRatio(Double heightWidthRatio) {
        this.heightWidthRatio = heightWidthRatio;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Name: " + this.name + "\n");
        result.append("Height/Width Ratio: " + this.heightWidthRatio + "\n");
        for (Map.Entry<Integer, FontChar> entry : this.fontCharMap.entrySet()) {
            result.append("Value: ");
            result.append(entry.getKey());
            result.append(" " + entry.getValue() + "\n");
        }
        return result.toString();
    }
}
