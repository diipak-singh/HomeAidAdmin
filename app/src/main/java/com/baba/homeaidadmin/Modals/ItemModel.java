package com.baba.homeaidadmin.Modals;

public class ItemModel {
    private String itemname, itemprice, itemid;

    public ItemModel() {
    }

    public ItemModel(String itemname, String itemprice, String itemid) {
        this.itemname = itemname;
        this.itemprice = itemprice;
        this.itemid = itemid;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public String getItemprice() {
        return itemprice;
    }

    public void setItemprice(String itemprice) {
        this.itemprice = itemprice;
    }

    public String getItemid() {
        return itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }
}
