package com.google.ar.sceneform.samples.gltf.model;

import java.io.Serializable;

public class Furniture implements Serializable {
    String Name,Detail,Model,Image;
    Long Price;
    int Like;

    public Furniture() {
    }


    public Furniture(String name, String detail, Long price, String model, String image, int Like) {
        Name = name;
        Detail = detail;
        Price = price;
        Model = model;
        Image = image;
        Like = Like;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDetail() {
        return Detail;
    }

    public void setDetail(String detail) {
        Detail = detail;
    }

    public Long getPrice() {
        return Price;
    }

    public void setPrice(Long price) {
        Price = price;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public int getLike() {
        return Like;
    }

    public void setLike(int like) {
        Like = like;
    }

}
