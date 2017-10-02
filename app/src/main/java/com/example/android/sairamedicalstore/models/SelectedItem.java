package com.example.android.sairamedicalstore.models;

/**
 * Created by chandan on 17-09-2017.
 */

public class SelectedItem {
    String itemName;

    public SelectedItem() {
    }

    public SelectedItem(String itemName) {
        this.itemName = itemName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
