package com.google.ar.sceneform.samples.gltf.model;

public class Cover {
    String link;
    boolean iscover;

    public Cover() {
    }

    public Cover(String link, boolean iscover) {
        this.link = link;
        this.iscover = iscover;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isIscover() {
        return iscover;
    }

    public void setIscover(boolean iscover) {
        this.iscover = iscover;
    }
}
