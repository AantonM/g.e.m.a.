package com.cet325.gamers_emotional_state_detection.holders;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

//Singleton
public class ImageHolder {

    private static ImageHolder single_instance = null;

    private int imageNumber;
    private Bitmap image;

    public int getImageNumber()
    {
        return imageNumber;
    }

    public  Bitmap getImage()
    {
        return image;
    }

    public void setImage(Bitmap image)
    {

        this.image = image;
        imageNumber ++;
    }

    private ImageHolder()
    {
        image = null;
        imageNumber = 0;
    }

    public static ImageHolder getInstance()
    {
        if (single_instance == null)
            single_instance = new ImageHolder();

        return single_instance;
    }

}
