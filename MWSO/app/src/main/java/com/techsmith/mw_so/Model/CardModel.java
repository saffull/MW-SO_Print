package com.techsmith.mw_so.Model;

public class CardModel {
    private String name;
    private int imageName;

    public CardModel(String name, int imageName) {
        this.name = name;
        this.imageName = imageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getImageName() {
        return imageName;
    }

    public void setImageName(int imageName) {
        this.imageName = imageName;
    }
}
