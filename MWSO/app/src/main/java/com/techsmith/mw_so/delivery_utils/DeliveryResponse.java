package com.techsmith.mw_so.delivery_utils;

public class DeliveryResponse {
    private String invoice, items, amount, billno;

    public DeliveryResponse() {
    }

    public DeliveryResponse(String invoice, String items, String amount, String billno) {
        this.invoice = invoice;
        this.items = items;
        this.amount = amount;
        this.billno=billno;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String name) {
        this.invoice = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }


    public String getBillno() {
        return billno;
    }

    public void setBillno(String billno) {
        this.billno = billno;
    }
}
