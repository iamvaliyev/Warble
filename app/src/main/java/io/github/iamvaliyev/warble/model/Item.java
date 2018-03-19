package io.github.iamvaliyev.warble.model;

public class Item {

    private int index;

    public Item(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        Item itemCompare = (Item) obj;
        if (itemCompare.getIndex() == this.getIndex())
            return true;

        return false;
    }
}