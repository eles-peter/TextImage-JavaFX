package userinterface.utils;

import javafx.scene.layout.FlowPane;

public class SelectableFlowPane extends FlowPane {

    private CharPane lastSelected = null;

    public SelectableFlowPane() {
        super();
    }

    public void clearLastSelected() {
        this.lastSelected = null;
    }

    public CharPane getLastSelected() {
        return lastSelected;
    }

    public void setLastSelected(CharPane charPane) {
        this.lastSelected = charPane;
    }

    public boolean isLastSelected() {
        if (lastSelected != null) {
            return true;
        }
        return false;
    }


}
