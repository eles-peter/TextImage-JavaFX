package general;


import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FontCharMap {

    private Map<Integer, FontChar> fontCharMap = new HashMap<>();

    public FontCharMap() {
    }

    public FontCharMap(String fullPathAndFileName) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(fullPathAndFileName));
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
                if (!unicodeValue.matches("[0-9A-F]{4}")) {
                    throw new IllegalArgumentException();
                }
                String fontType = lineArray[2];
                this.add(value, new FontChar(unicodeValue, fontType));
            }
        }
        scanner.close();
    }

    public void add(Integer value, FontChar fontChar) {
        this.fontCharMap.put(value, fontChar);
    }

    public void writeToFile(String fullPathAndFileName) throws IOException {
        FileWriter writer = new FileWriter(fullPathAndFileName);
        for (Map.Entry<Integer, FontChar> entry : this.fontCharMap.entrySet()) {
            StringBuilder line = new StringBuilder("");
            line.append(entry.getKey());
            line.append(":");
            line.append(entry.getValue().getUnicodeValue());
            line.append(":");
            line.append(entry.getValue().getFontType());
            line.append("\n");
            writer.write(line.toString());
        }
        writer.close();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<Integer, FontChar> entry : this.fontCharMap.entrySet()) {
            result.append("Value: ");
            result.append(entry.getKey());
            result.append(" " + entry.getValue() + "\n");
        }
        return result.toString();
    }
}
