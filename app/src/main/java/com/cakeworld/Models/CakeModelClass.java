package com.cakeworld.Models;

public class CakeModelClass {
    private String CakeName, CakePrice, CakeDescription, CakePhoto;

    public CakeModelClass() {
    }

    public CakeModelClass(String cakeName, String cakePrice, String cakeDescription, String cakePhoto) {
        CakeName = cakeName;
        CakePrice = cakePrice;
        CakeDescription = cakeDescription;
        CakePhoto = cakePhoto;
    }

    public String getCakeName() {
        return CakeName;
    }

    public void setCakeName(String cakeName) {
        CakeName = cakeName;
    }

    public String getCakePrice() {
        return CakePrice;
    }

    public void setCakePrice(String cakePrice) {
        CakePrice = cakePrice;
    }

    public String getCakeDescription() {
        return CakeDescription;
    }

    public void setCakeDescription(String cakeDescription) {
        CakeDescription = cakeDescription;
    }

    public String getCakePhoto() {
        return CakePhoto;
    }

    public void setCakePhoto(String cakePhoto) {
        CakePhoto = cakePhoto;
    }
}
