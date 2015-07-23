package com.orangenote.orangenote;

/**
 * Created by on 2015-07-05.
 */
public class ItemData {

    private String image;
    private String title;
    private String body;

    public ItemData(String image, String title, String body){
        this.image = image;
        this.title = title;
        this.body = body;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }
}
