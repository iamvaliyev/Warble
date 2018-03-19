package io.github.iamvaliyev.warble.model;

public class SelectableItem extends Item{
    private boolean isSelected = false;


    public SelectableItem(int item, boolean isSelected) {
        super(item);
        this.isSelected = isSelected;
    }


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}