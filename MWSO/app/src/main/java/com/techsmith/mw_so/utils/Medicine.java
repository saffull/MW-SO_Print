package com.techsmith.mw_so.utils;

public class Medicine {
    public String itemName;
    public int itemId;
    public int allocStore;
    public String allocStoreCode;
    public double qty;
    public double freeQty;
    public double rate;
    public double volDiscPer;
    public double cashDiscPer;

    public Medicine(String itemName, String allocStoreCode, int allocStore, double qty, double freeQty, double rate, double volDiscPer, double cashDiscPer) {
        this.itemName = itemName;
        this.allocStoreCode = allocStoreCode;
        this.allocStore = allocStore;
        this.qty = qty;
        this.freeQty = freeQty;
        this.rate = rate;
        this.volDiscPer = volDiscPer;
        this.cashDiscPer = cashDiscPer;
    }


    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getAllocStore() {
        return allocStore;
    }

    public void setAllocStore(int allocStore) {
        this.allocStore = allocStore;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getAllocStoreCode() {
        return allocStoreCode;
    }

    public void setAllocStoreCode(String allocStoreCode) {
        this.allocStoreCode = allocStoreCode;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public double getFreeQty() {
        return freeQty;
    }

    public void setFreeQty(double freeQty) {
        this.freeQty = freeQty;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getVolDiscPer() {
        return volDiscPer;
    }

    public void setVolDiscPer(double volDiscPer) {
        this.volDiscPer = volDiscPer;
    }

    public double getCashDiscPer() {
        return cashDiscPer;
    }

    public void setCashDiscPer(double cashDiscPer) {
        this.cashDiscPer = cashDiscPer;
    }


}
