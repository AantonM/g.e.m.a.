package com.cet325.gamers_emotional_state_detection;

public class EmotionRecognitionManager
{
    private ImageHolder imageHolder;

    public EmotionRecognitionManager()
    {
        imageHolder = ImageHolder.getInstance();
    }

    private String getEmotion()
    {
        //TODO: pass bitmap of the photo
        EmotionRecognitionApiHandler emah = new EmotionRecognitionApiHandler();
        String emotionPerPhoto = emah.runEmotionalFaceRecognition(null);
        return null;
    }

}
