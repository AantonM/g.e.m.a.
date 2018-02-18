package com.cet325.gamers_emotional_state_detection.holders;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

//Singleton
public class ImageHolder {

    private static ImageHolder single_instance = null;

    private List<Bitmap> images;

    public  List<Bitmap> getImages()
    {
        return images;
    }

    public void setImage(Bitmap image)
    {
        this.images.add(image);
    }

    private ImageHolder()
    {
        images = new ArrayList<>();
    }

    public static ImageHolder getInstance()
    {
        if (single_instance == null)
            single_instance = new ImageHolder();

        return single_instance;
    }

}
