package com.android.ososassignment.model;

import java.util.ArrayList;
import java.util.List;

public class AllTitles {

    public AllTitles() {}

    String title;
    ArrayList<AllItems> itemsList;

    public AllTitles(String title, ArrayList<AllItems> items) {
        this.title = title;
        this.itemsList = items;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<AllItems> getItemsList() {
        return itemsList;
    }

    public void setItemsList(ArrayList<AllItems> itemsList) {
        this.itemsList = itemsList;
    }
}
