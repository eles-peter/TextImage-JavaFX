package userinterface.utils;

import general.FontCharMap;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class FontCharMapService {

    private static final String FILEDIRECTORY = "src\\userinterface\\resources";
    private BooleanProperty isChanged = new SimpleBooleanProperty(false);
    private String actualFCMName;
    private List<FontCharMap> fontCharMapList = new ArrayList<>();
    private static final FontCharMapService INSTANCE = new FontCharMapService();

    private FontCharMapService() {
    }

    public static FontCharMapService getInstance() {
        return INSTANCE;
    }

    public void initialize() {
        File file = new File(FILEDIRECTORY);
        String[] fileArray = file.list();

        for (String filename : fileArray) {
            if (filename.substring(filename.length() - 4).equals(".fcm")) {
                try {
                    FontCharMap fontCharMap = new FontCharMap(FILEDIRECTORY + "\\" + filename);
                    this.fontCharMapList.add(fontCharMap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        this.actualFCMName = fontCharMapList.get(0).getName();
    }

    public FontCharMap getFCMbyName(String FCMName) {
        for (FontCharMap fontCharMap : fontCharMapList) {
            if (fontCharMap.getName().equals(FCMName)) {
                return fontCharMap;
            }
        }
        return null;
    }

    public List<String> getFCMNameList() {
        List<String> result = new ArrayList<>();
        for (FontCharMap fontCharMap : fontCharMapList) {
            result.add(fontCharMap.getName());
        }
        return result;
    }

    public FontCharMap getActualFontCharMap() {
        return getFCMbyName(this.actualFCMName);
    }

    public void addFontCharMap(FontCharMap fontCharMap) {
        this.fontCharMapList.add(fontCharMap);
    }

    public void addFontCharMapAndSetActual(FontCharMap fontCharMap) {
        this.fontCharMapList.add(fontCharMap);
        this.actualFCMName = fontCharMap.getName();
        this.isChanged.setValue(true);
    }

    public boolean isChanged() {
        return isChanged.get();
    }

    public BooleanProperty isChangedProperty() {
        return isChanged;
    }

    public void setIsChanged(boolean isChanged) {
        this.isChanged.set(isChanged);
    }

    public String getActualFCMName() {
        return actualFCMName;
    }

    public void setActualFCMName(String actualFCMName) {
        this.actualFCMName = actualFCMName;
    }
}
