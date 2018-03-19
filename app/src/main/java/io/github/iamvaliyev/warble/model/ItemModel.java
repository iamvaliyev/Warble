package io.github.iamvaliyev.warble.model;

public class ItemModel {

    private int index;
    private boolean isSelected;

    public ItemModel(int index, boolean isSelected) {
        this.index = index;
        this.isSelected = isSelected;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}