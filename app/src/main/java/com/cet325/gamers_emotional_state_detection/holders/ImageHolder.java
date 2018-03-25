package com.cet325.gamers_emotional_state_detection.holders;

import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Singleton holder object that stores the image taken from the camera
 */
public class ImageHolder {

    private static ImageHolder single_instance = null;

    private int imageNumber;
    private Bitmap image;
    private String timestamp;

    /**
     * Method that returns the id of the current frame
     *
     * @return int: image id
     */
    public int getImageNumber() {
        return imageNumber;
    }

    /**
     * Method that returns the stored image.
     *
     * @return Bitmap: the image
     */
    public Bitmap getImage() {
        return image;
    }

    /**
     * Method that returns the timestamp of the stored image.
     *
     * @return String: timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Method that stores a new image and sets an image id
     */
    public void setImage(Bitmap image) {

        this.image = image;
        imageNumber++;
        this.timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    /**
     * Constructor that initialises the fields where the data will be saved.
     */
    private ImageHolder() {
        image = null;
        imageNumber = 0;
    }

    /**
     * Method that initialises the singleton object.
     *
     * @return ImageHolder: the current instance of the object
     */
    public static ImageHolder getInstance() {
        if (single_instance == null)
            single_instance = new ImageHolder();

        return single_instance;
    }

    /**
     * Clear the holder object
     */
    public static void clean() {
        single_instance = null;
    }

}
