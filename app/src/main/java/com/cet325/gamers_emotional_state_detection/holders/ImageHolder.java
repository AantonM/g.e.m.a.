package com.cet325.gamers_emotional_state_detection.holders;

import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//Singleton
public class ImageHolder {

    private static ImageHolder single_instance = null;

    private int imageNumber;
    private Bitmap image;
    private String timestamp;

    public int getImageNumber()
    {
        return imageNumber;
    }

    public  Bitmap getImage()
    {
        return image;
    }

    public  String getTimestamp()
    {
        return timestamp;
    }

    public void setImage(Bitmap image)
    {

        this.image = image;
        imageNumber ++;
        this.timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
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

    public static void clean()
    {
        single_instance = null;
    }

}
