/***
"Path of Ants" Android Game
By: Zunair Syed
August 1 2014

*/
package com.cet325.gamers_emotional_state_detection.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.cet325.gamers_emotional_state_detection.R;
import com.cet325.gamers_emotional_state_detection.game.game_attributes.ellipse;
import com.cet325.gamers_emotional_state_detection.game.game_attributes.map;
import com.cet325.gamers_emotional_state_detection.game.game_attributes.rectangle;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class Gameplay_PathOfAnth extends SurfaceView implements Runnable {

    private Context context;

    private Random random = new Random();
    //-----touch controls---//
    private float x[] = new float[4];//x position of touches (gets up to 4 touches)
    private float y[] = new float[4];//y position of touches (gets up to 4 touches)

    private boolean fingerDown[] = new boolean[4];//if that finger is touching (gets up to 4 touches)
    private boolean fingerMoving[] = new boolean[4];//if that finger is touching AND MOVING (gets up to 4 touches)

    private Vibrator v;//the vibrator object needed to vibrarte the phone

    //below are some variables that are self descriptive of what they represent
    private int gameSpeed = 4;
    private int gameStartTimerPauser = 0;
    private int gameTimeRecorder = 0;
    private int backgroundY = 0;
    private int backgroundY1 = 0;
    private int backgroundY2 = 0;
    private int score = 0;

    private int antX = 0;
    private int antY = 0;
    //sprite counter and slower of the ant sprites
    private int antSpriteCounter = 1;
    private int antSpriteCounterSlower = 0;

    private boolean isDragging = false;
    private boolean onlyOnceForFingers = false;


    //-------SCREENS-----//
    private boolean onPlayGame = false;


    //----Screen loader--//
    private boolean playGameLoader = true;


    //calulation loaders (for drawing properly on each phone screen)
    private boolean yCalculationsDone = false;
    private boolean xCalculationsDone = false;

    private int scale = 5;//how many decimals there will be when taking the accuracy of the phone's resolution
    private BigDecimal num1;//to carry a lot of decimals, not applicabe in ordinary double types
    private BigDecimal num2;//same thing as above for another variable
    private double yfinalValue; //y way's final value for phone screen resoultion
    private double xfinalValue;//x way's final value for phone screen resoultion


    private SurfaceHolder ourHolder; //surface holder object is this object that lets us put on a canvas on our mobile screen so we can draw stuff
    private Canvas canvas = (Canvas) ourHolder;//here is how we put on a canvas on our mobile screen
    private Thread ourThread = null;//just a thread variable for the run method
    private boolean isRunning = false; //the main run method variabble


    private map allMaps[] = new map[10]; //our map objects will be placed in various locations in screen

    private map theTopestMap;


    private Paint paint;

    //----------ALL Pictures-----------//
    private Bitmap restrictedRectangle[] = new Bitmap[10]; //for the pictures that are in array is becaus ethere are 10 different sizes of that picture
    private Bitmap restrictedEllipse[] = new Bitmap[10];

    private Bitmap roadHorizontalRect[] = new Bitmap[10];
    private Bitmap roadVerticalRect[] = new Bitmap[10];
    private Bitmap roadEllipse[] = new Bitmap[10];

    private Bitmap antSprites[] = new Bitmap[3];

    private Bitmap menuScreenPic;
    private Bitmap backgroundPic;

    private Bitmap bubble;


    public Gameplay_PathOfAnth(Context context)//the contrutor
    {
        super(context);//calls the super class contructor and pass it this
        this.context = context;
        ourHolder = getHolder();//we get our holder (our canvas)
        onPlayGame = true;

        //because this application requires only single touches (sometimes 2), we will have 2 that will be working
        x[0] = 0;//x of finger 1
        y[0] = 0;//y of finger 1
        x[1] = 0;//x of finger 2
        y[1] = 0;//y of finger 2

        isRunning = true;
        ourThread = new Thread(this);
        ourThread.start();
    }

    //this method basically converts our normal x-coordinates on one phone to the same coordinates on another phone,
    //and meanwhile making sure the game fits the screen
    //this one is for the xcoooridnates.
    private int dpToPx(int dp) {
        if (xCalculationsDone == false) {//below we r going to do some measurements calculations. Since we call this method in our loop, we'll have the calculations in a  if statement so that we do not slow the game down. This makes sure that the calcualtions happen once, but are saved in one variable
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics(); ///we get our display on our current phone and get the measurements
            num1 = new BigDecimal(canvas.getWidth());//then we have 2 number with huge decimal places. This one is like the full screen it self ON THE PHONE
            num2 = new BigDecimal(Math.round(320 * (displayMetrics.ydpi / DisplayMetrics.DENSITY_DEFAULT)));//and this one is the full screen on my emulator
            xfinalValue = (num1.divide(num2, scale, RoundingMode.HALF_UP).doubleValue()) * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT);//this basically is a scale that helps the drawings made respective to my emulator, make the same on the phone, making sure it covers the whole screen
            //below we get rid of garbage variables by making them null to send to garbage collection
            num1 = null;
            num2 = null;
            displayMetrics = null;
            xCalculationsDone = true;
        }
        return (int) (dp * xfinalValue);//then we basically take my drawings's x-cooridinate and times it by the current scale of the phone to get the actual x-coodinate on this phone

    }

    //This method is an exact copy of the one above, expect this is for the y-variabbles
    private int dpToPxY(int dp) {

        if (yCalculationsDone == false) {
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            num1 = new BigDecimal(canvas.getHeight());
            num2 = new BigDecimal(Math.round(533 * (displayMetrics.ydpi / DisplayMetrics.DENSITY_DEFAULT)));
            yfinalValue = (num1.divide(num2, scale, RoundingMode.HALF_UP).doubleValue()) * (displayMetrics.ydpi / DisplayMetrics.DENSITY_DEFAULT);
            num1 = null;
            num2 = null;
            displayMetrics = null;
            yCalculationsDone = true;
        }

        return (int) (yfinalValue * dp);


    }

    // a method that resizes the pictures to my amounts  to a new height and width
    private Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();//get the current width and height

        int height = bm.getHeight();

        float scaleWidth = ((float) newWidth) / width;//find the proper scale (so we're not pixolating it)

        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();//a new matrix object will help scale the picture...hard to explain but basically, we take the scale behind the picture and set it to a new scales which will help us create a non pixolated new picture
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

        return resizedBitmap;//return the rezised picture
    }


    public void run()//our main run method
    {
        while (isRunning)//This is like the run method in any running applicaiton
        {

            if (!ourHolder.getSurface().isValid())//Basically, this is when our surface view is ready to go through another thread, and is when another frame is going to happen (start)
                continue;

            canvas = ourHolder.lockCanvas();

            paint = new Paint(); // a paint object for things like color and sizes and stuff

                /*
                 * For each screen below, you'll see that there is a loader.
                 * A loader is basically an if statement that will load properties into our variables ONCE
                 * ONCE, so that we're not looping it again and again which slows the game down REALLY BAD
                 */

            if (onPlayGame) {


                if (playGameLoader) {//play game loader

                    for (int i = 0; i < 10; i++) {//10 diferent sized pic for each road...and this is how we load it
                        roadEllipse[i] = BitmapFactory.decodeResource(getResources(), R.drawable.game_roadellipse);
                        restrictedEllipse[i] = BitmapFactory.decodeResource(getResources(), R.drawable.game_restrictedellipse);
                        roadHorizontalRect[i] = BitmapFactory.decodeResource(getResources(), R.drawable.game_horizontalpath);
                        roadVerticalRect[i] = BitmapFactory.decodeResource(getResources(), R.drawable.game_verticalpath);
                        restrictedRectangle[i] = BitmapFactory.decodeResource(getResources(), R.drawable.game_restrictedrect);

                    }

                    //loading ant sprites
                    antSprites[0] = BitmapFactory.decodeResource(getResources(), R.drawable.game_game_ant0);
                    antSprites[1] = BitmapFactory.decodeResource(getResources(), R.drawable.game_ant1);
                    antSprites[2] = BitmapFactory.decodeResource(getResources(), R.drawable.game_ant2);

                    //resizeing them
                    antSprites[0] = getResizedBitmap(antSprites[0], dpToPx(40), dpToPxY(40));
                    antSprites[1] = getResizedBitmap(antSprites[1], dpToPx(40), dpToPxY(40));
                    antSprites[2] = getResizedBitmap(antSprites[2], dpToPx(40), dpToPxY(40));

                    //staritng off ant sprite counter at beggigning
                    antSpriteCounter = 1;
                    antSpriteCounterSlower = 0;


                    backgroundPic = BitmapFactory.decodeResource(getResources(), R.drawable.game_grass); //loading the background grass
                    backgroundPic = getResizedBitmap(backgroundPic, dpToPx(320), dpToPxY(533));//resizing to full screen

                    bubble = BitmapFactory.decodeResource(getResources(), R.drawable.game_bubble);
                    bubble = getResizedBitmap(bubble, dpToPx(50), dpToPxY(50));//resizing to full screen

                    //resizing all of our shapes of road to desired legnths and heights
                    restrictedEllipse[0] = getResizedBitmap(restrictedEllipse[0], dpToPx(100), dpToPxY(60));
                    restrictedEllipse[1] = getResizedBitmap(restrictedEllipse[1], dpToPx(150), dpToPxY(70));
                    restrictedEllipse[2] = getResizedBitmap(restrictedEllipse[2], dpToPx(150), dpToPxY(70));
                    restrictedEllipse[3] = getResizedBitmap(restrictedEllipse[3], dpToPx(80), dpToPxY(80));
                    restrictedEllipse[4] = getResizedBitmap(restrictedEllipse[4], dpToPx(60), dpToPxY(60));


                    roadEllipse[0] = getResizedBitmap(roadEllipse[0], dpToPx(200), dpToPxY(120));
                    roadEllipse[1] = getResizedBitmap(roadEllipse[1], dpToPx(300), dpToPxY(140));
                    roadEllipse[2] = getResizedBitmap(roadEllipse[2], dpToPx(300), dpToPxY(140));
                    roadEllipse[3] = getResizedBitmap(roadEllipse[3], dpToPx(180), dpToPxY(180));
                    roadEllipse[4] = getResizedBitmap(roadEllipse[4], dpToPx(140), dpToPxY(140));


                    restrictedRectangle[0] = getResizedBitmap(restrictedRectangle[0], dpToPx(70), dpToPxY(120));
                    restrictedRectangle[1] = getResizedBitmap(restrictedRectangle[1], dpToPx(100), dpToPxY(90));
                    restrictedRectangle[2] = getResizedBitmap(restrictedRectangle[2], dpToPx(70), dpToPxY(30));
                    restrictedRectangle[3] = getResizedBitmap(restrictedRectangle[3], dpToPx(100), dpToPxY(40));
                    restrictedRectangle[4] = getResizedBitmap(restrictedRectangle[4], dpToPx(130), dpToPxY(60));


                    roadVerticalRect[0] = getResizedBitmap(roadVerticalRect[0], dpToPx(70), dpToPxY(157));
                    roadHorizontalRect[0] = getResizedBitmap(roadHorizontalRect[0], dpToPx(270), dpToPxY(60));

                    roadVerticalRect[1] = getResizedBitmap(roadVerticalRect[1], dpToPx(70), dpToPxY(200));
                    roadHorizontalRect[1] = getResizedBitmap(roadHorizontalRect[1], dpToPx(190), dpToPxY(60));

                    roadVerticalRect[2] = getResizedBitmap(roadVerticalRect[2], dpToPx(50), dpToPxY(130));
                    roadHorizontalRect[2] = getResizedBitmap(roadHorizontalRect[2], dpToPx(220), dpToPxY(70));

                    roadVerticalRect[3] = getResizedBitmap(roadVerticalRect[3], dpToPx(70), dpToPxY(130));
                    roadHorizontalRect[3] = getResizedBitmap(roadHorizontalRect[3], dpToPx(110), dpToPxY(53));

                    roadVerticalRect[4] = getResizedBitmap(roadVerticalRect[4], dpToPx(100), dpToPxY(120));
                    roadHorizontalRect[4] = getResizedBitmap(roadHorizontalRect[4], dpToPx(320), dpToPxY(60));

                    roadVerticalRect[5] = getResizedBitmap(roadVerticalRect[5], dpToPx(70), dpToPxY(90));
                    roadVerticalRect[6] = getResizedBitmap(roadVerticalRect[6], dpToPx(60), dpToPxY(90));

                    roadVerticalRect[7] = getResizedBitmap(roadVerticalRect[7], dpToPx(70), dpToPxY(70));
                    roadVerticalRect[8] = getResizedBitmap(roadVerticalRect[8], dpToPx(100), dpToPxY(140));


                    //the background for so that we have a runnning background everytime, they are specifically palced at spots
                    backgroundY = 0;
                    backgroundY1 = -dpToPxY(533);
                    backgroundY2 = -2 * dpToPxY(533);

                    theTopestMap = null;

                    //our main map objects, placed at proper locations (y, height)


                    allMaps[0] = new map(dpToPxY(200), dpToPxY(150), "0");
                    allMaps[1] = new map(dpToPxY(-150), dpToPxY(350), "1");
                    allMaps[2] = new map(dpToPxY(-690), dpToPxY(540), "2");
                    allMaps[3] = new map(dpToPxY(-910), dpToPxY(220), "3");
                    allMaps[4] = new map(dpToPxY(-1130), dpToPxY(220), "4");
                    allMaps[5] = new map(dpToPxY(-1350), dpToPxY(220), "5");
                    theTopestMap = allMaps[5];
                    for (int i = 0; i < 6; i++) {
                        allMaps[i].setrandAssignedInt(random.nextInt(6));
                    }

                    int randNum = random.nextInt(6);
                    int finshedNumbers[] = new int[6];
                    for (int i = 0; i < 6; i++) {
                        finshedNumbers[i] = -89;
                    }
                    int counter = 0;
                    boolean getoutofloop = false;
                    int currentHeight = 0;

                    while (counter < 6) {


                        for (int i = 0; i < 6; i++) {
                            if (randNum == finshedNumbers[i]) {
                                randNum = random.nextInt(6);
                                i = 0;
                            }
                        }

                        if (counter == 0) {
                            allMaps[randNum] = new map(dpToPxY(533) - allMaps[randNum].getHeight(), allMaps[randNum].getHeight(), " ");
                            currentHeight = dpToPxY(533) - allMaps[randNum].getHeight();
                            counter++;
                        } else {
                            allMaps[randNum] = new map(currentHeight - allMaps[randNum].getHeight(), allMaps[randNum].getHeight(), " ");
                            currentHeight = currentHeight - allMaps[randNum].getHeight();
                            counter++;
                            if (counter == 6) {
                                theTopestMap = allMaps[randNum];
                            }
                        }

                        if (counter < 6) {

                            finshedNumbers[counter] = randNum;
                        }

                    }

                    //we purposely set our toch coordinates so that our ant starts off from a road
                    x[0] = dpToPx(140);
                    y[0] = dpToPx(290);
                    antX = Math.round(x[0]);
                    antY = Math.round(y[0]);
                    playGameLoader = false;

                }


                //backgroundPics (we have to draw them , at different places so we don't have a cutt off line which is why we have 3 y variables)
                canvas.drawBitmap(backgroundPic, 0, backgroundY, paint);
                canvas.drawBitmap(backgroundPic, 0, backgroundY1, paint);
                canvas.drawBitmap(backgroundPic, 0, backgroundY2, paint);


                //the if statment below basically help make the grass background go in a loop, so that when they get below the screen, they come back from the top.
                //It's helpful because we don't have a cut off line
                if (backgroundY > dpToPxY(533)) {
                    backgroundY = backgroundY2 - (dpToPxY(533));
                }
                if (backgroundY1 > dpToPxY(533)) {
                    backgroundY1 = backgroundY - (dpToPxY(533));
                }
                if (backgroundY2 > dpToPxY(533)) {
                    backgroundY2 = backgroundY1 - (dpToPxY(533));
                }

                //we add our normal game speed to these background inorder to move them at the game speed
                backgroundY += gameSpeed;
                backgroundY1 += gameSpeed;
                backgroundY2 += gameSpeed;


                //below, we basically convert from float to int so we can use them in different ways
                x[0] = Math.round(x[0]);
                y[0] = Math.round(y[0]);



                    /*
                     * Below are our maps.
                     * Maps are basically what cover our screen.
                     * One screen can have many maps running simontanously
                     * each map goes thru a similar format when coding
                     * First, we have an if statement telling us whether this map is in our screen or  not
                     * if the map is in our screen, then we first clear all [if any] road and restricted shapes on that map by calling the clearAll method of the map
                     * Then we simply add our road rectangles, and ellipse (the one where ants walk upon)
                     * We draw this by drawing a road picture
                     * Then we add restricted shapes (rectangles and ellipse), which is like grass
                     * And we draw a grass picture,
                     * That's it. This process is utilized in all maps
                     */
                if (allMaps[0].getYPositionOfMap() + allMaps[0].getHeight() > -10 && allMaps[0].getYPositionOfMap() < dpToPxY(533) + 10) {

                    createMap0(allMaps[0]);

                }


                if (allMaps[1].getYPositionOfMap() + allMaps[1].getHeight() > -10 && allMaps[1].getYPositionOfMap() < dpToPxY(533) + 10) {
                    createMap1(allMaps[1]);
                }


                if (allMaps[2].getYPositionOfMap() + allMaps[2].getHeight() > -10 && allMaps[2].getYPositionOfMap() < dpToPxY(533) + 10) {
                    createMap2(allMaps[2]);
                }


                if (allMaps[3].getYPositionOfMap() + allMaps[3].getHeight() > -10 && allMaps[3].getYPositionOfMap() < dpToPxY(533) + 10) {
                    createMap3(allMaps[3]);
                }


                if (allMaps[4].getYPositionOfMap() + allMaps[4].getHeight() > -10 && allMaps[4].getYPositionOfMap() < dpToPxY(533) + 10) {
                    createMap4(allMaps[4]);
                }


                if (allMaps[5].getYPositionOfMap() + allMaps[5].getHeight() > -10 && allMaps[5].getYPositionOfMap() < dpToPxY(533) + 10) {
                    createMap5(allMaps[5]);
                }


                //--------------Ant Stuff----------//
                if (fingerDown[0] == false) {
                    antSpriteCounterSlower++;//the speed of the counter of the sprite counter

                    if (antSpriteCounterSlower > 3) {//the counter has reached 3 frames
                        antSpriteCounterSlower = 0;//we make this 0 so we can restart the proccess again
                        antSpriteCounter++;//we bascially increase the picture to the nextt one
                        if (antSpriteCounter > 2) {//if we have reached the end of the picture
                            antSpriteCounter = 0;//we start from the beggining
                        }
                    }
                }

                if (fingerDown[0] == true && (x[0] > antX - Math.round((dpToPx(30)) / 2) && x[0] < antX - Math.round((dpToPx(30)) / 2) + dpToPx(40) && y[0] > antY - Math.round((dpToPxY(40)) / 2) && y[0] < antY - Math.round((dpToPxY(40)) / 2) + dpToPxY(40))) {
                    isDragging = true;
                }

                if (fingerDown[0] == false) {
                    isDragging = false;
                }

                if (isDragging == false) {

                } else {
                    antX = Math.round(x[0]);
                    antY = Math.round(y[0]);
                }

                //drawing the ant
                canvas.drawBitmap(antSprites[antSpriteCounter], antX - Math.round((dpToPx(30)) / 2), antY - Math.round((dpToPxY(40)) / 2), paint);

                //Drawing bubble at the beggining of the game
                if (gameTimeRecorder < 70) {
                    canvas.drawBitmap(bubble, antX - Math.round((dpToPx(30)) / 2) - dpToPx(5), antY - Math.round((dpToPxY(40)) / 2) - dpToPxY(5), paint);
                }



                    /*
                     * Below we basically set the y position of each map by taking it's current position and adding the game speed  to move it down
                     * This is done to each of the map
                     */
                allMaps[0].setYPositionOfMap(allMaps[0].getYPositionOfMap() + gameSpeed);
                allMaps[1].setYPositionOfMap(allMaps[1].getYPositionOfMap() + gameSpeed);
                allMaps[2].setYPositionOfMap(allMaps[2].getYPositionOfMap() + gameSpeed);
                allMaps[3].setYPositionOfMap(allMaps[3].getYPositionOfMap() + gameSpeed);
                allMaps[4].setYPositionOfMap(allMaps[4].getYPositionOfMap() + gameSpeed);
                allMaps[5].setYPositionOfMap(allMaps[5].getYPositionOfMap() + gameSpeed);



                    /*
                     * Below we basically see if the maps have gone below the screen
                     * if they have, we basically make them go on top of the screen, so they can be repeated again
                     * This is just a way to repeat the maps from top to bottom
                     */


                if (allMaps[0].getYPositionOfMap() > dpToPxY(533) + 10) {
                    allMaps[0].setYPositionOfMap(theTopestMap.getYPositionOfMap() - allMaps[0].getHeight());
                    theTopestMap = allMaps[0];
                }
                if (allMaps[1].getYPositionOfMap() > dpToPxY(533) + 10) {
                    allMaps[1].setYPositionOfMap(theTopestMap.getYPositionOfMap() - allMaps[1].getHeight());
                    theTopestMap = allMaps[1];
                }
                if (allMaps[2].getYPositionOfMap() > dpToPxY(533) + 10) {
                    allMaps[2].setYPositionOfMap(theTopestMap.getYPositionOfMap() - allMaps[2].getHeight());
                    theTopestMap = allMaps[2];

                }
                if (allMaps[3].getYPositionOfMap() > dpToPxY(533) + 10) {
                    allMaps[3].setYPositionOfMap(theTopestMap.getYPositionOfMap() - allMaps[3].getHeight());
                    theTopestMap = allMaps[3];

                }
                if (allMaps[4].getYPositionOfMap() > dpToPxY(533) + 10) {
                    allMaps[4].setYPositionOfMap(theTopestMap.getYPositionOfMap() - allMaps[4].getHeight());
                    theTopestMap = allMaps[4];

                }

                if (allMaps[5].getYPositionOfMap() > dpToPxY(533) + 10) {
                    allMaps[5].setYPositionOfMap(theTopestMap.getYPositionOfMap() - allMaps[5].getHeight());
                    theTopestMap = allMaps[5];

                }


                //Validating is user is off track
                if (gameTimeRecorder > 80) { //don't trigger during the bubble time
                    if (allMaps[0].checkOnRoad((int) antX, (int) antY)) {//then we check if his finger is on any map's road
                    } else if (allMaps[1].checkOnRoad((int) antX, (int) antY)) {
                    } else if (allMaps[2].checkOnRoad((int) antX, (int) antY)) {
                    } else if (allMaps[3].checkOnRoad((int) antX, (int) antY)) {
                    } else if (allMaps[4].checkOnRoad((int) antX, (int) antY)) {
                    } else if (allMaps[5].checkOnRoad((int) antX, (int) antY)) {
                    } else {//if not, then we vibrate the phone
                        v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(500);//vibrate for 800 miliseconds
                        v = null;//make this null (makes game faster as we eliminate ram space)
                        score -= 2;
                    }
                }


                gameStartTimerPauser++;//this variable is for the timer of pauser in the start (to give us a head start before pausing)
                gameTimeRecorder++;//and this is just after that, the total time recorderr
                score++;//every frame we also increase the score

                //Set score style
                paint.setColor(Color.RED);
                paint.setTextSize(80);
                canvas.drawText("SCORE: " + score, dpToPx(5), dpToPxY(30), paint);

                //this is when we lock our canvas (to show to user). Basically, we can think of this as the end of our run loop
                ourHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    private void createMap0(map thisMap) {

        thisMap.clearAllRoadsAndRestrictions();//we first clear all [if any] road and restricted shapes on that map by calling the clearAll method of the map

        //---------------Roads Shapes---------------//
        thisMap.addRoadRect(new rectangle(dpToPx(120), dpToPxY(0) + thisMap.getYPositionOfMap(), dpToPx(70), dpToPxY(155)));
        canvas.drawBitmap(roadVerticalRect[0], dpToPx(120), thisMap.getYPositionOfMap() + dpToPxY(0), paint);


    }


    private void createMap1(map thisMap) {

        if (thisMap.getYPositionOfMap() + thisMap.getHeight() > -10 && thisMap.getYPositionOfMap() < dpToPxY(533) + 10) {

            thisMap.clearAllRoadsAndRestrictions();//we first clear all [if any] road and restricted shapes on that map by calling the clearAll method of the map


            //---------------Roads Shapes---------------//
            thisMap.addRoadRect(new rectangle(dpToPx(120), dpToPxY(70) + thisMap.getYPositionOfMap(), dpToPx(270), dpToPxY(60)));
            canvas.drawBitmap(roadHorizontalRect[0], dpToPx(120), thisMap.getYPositionOfMap() + dpToPxY(70), paint);

            thisMap.addRoadRect(new rectangle(dpToPx(250), dpToPxY(80) + thisMap.getYPositionOfMap(), dpToPx(70), dpToPxY(200)));
            canvas.drawBitmap(roadVerticalRect[1], dpToPx(250), thisMap.getYPositionOfMap() + dpToPxY(80), paint);


            thisMap.addRoadRect(new rectangle(dpToPx(120), dpToPxY(250) + thisMap.getYPositionOfMap(), dpToPx(270), dpToPxY(60)));
            canvas.drawBitmap(roadHorizontalRect[0], dpToPx(120), thisMap.getYPositionOfMap() + dpToPxY(250), paint);

            thisMap.addRoadRect(new rectangle(dpToPx(120), dpToPxY(280) + thisMap.getYPositionOfMap(), dpToPx(70), dpToPxY(70)));
            canvas.drawBitmap(roadVerticalRect[7], dpToPx(120), thisMap.getYPositionOfMap() + dpToPxY(280), paint);

            thisMap.addRoadRect(new rectangle(dpToPx(120), dpToPxY(0) + thisMap.getYPositionOfMap(), dpToPx(70), dpToPxY(70)));
            canvas.drawBitmap(roadVerticalRect[7], dpToPx(120), thisMap.getYPositionOfMap() + dpToPxY(0), paint);


        }
    }


    private void createMap2(map thisMap) {
        if (thisMap.getYPositionOfMap() + thisMap.getHeight() > -10 && thisMap.getYPositionOfMap() < dpToPxY(533) + 10) {

            thisMap.clearAllRoadsAndRestrictions();//we first clear all [if any] road and restricted shapes on that map by calling the clearAll method of the map


            //---------------Roads Shapes---------------//
            thisMap.addRoadRect(new rectangle(dpToPx(0), dpToPxY(410) + thisMap.getYPositionOfMap(), dpToPx(190), dpToPxY(60)));
            canvas.drawBitmap(roadHorizontalRect[1], dpToPx(0), thisMap.getYPositionOfMap() + dpToPxY(410), paint);

            thisMap.addRoadRect(new rectangle(dpToPx(0), dpToPxY(320) + thisMap.getYPositionOfMap(), dpToPx(50), dpToPxY(130)));
            canvas.drawBitmap(roadVerticalRect[2], dpToPx(0), thisMap.getYPositionOfMap() + dpToPxY(320), paint);


            thisMap.addRoadRect(new rectangle(dpToPx(120), dpToPxY(60) + thisMap.getYPositionOfMap(), dpToPx(270), dpToPxY(60)));
            canvas.drawBitmap(roadHorizontalRect[0], dpToPx(120), thisMap.getYPositionOfMap() + dpToPxY(60), paint);

            thisMap.addRoadRect(new rectangle(dpToPx(120), dpToPxY(0) + thisMap.getYPositionOfMap(), dpToPx(70), dpToPxY(70)));
            canvas.drawBitmap(roadVerticalRect[7], dpToPx(120), thisMap.getYPositionOfMap() + dpToPxY(0), paint);


            //---------------Roads Shapes map3 mergerd---------------//
            thisMap.addRoadEllipse(new ellipse(dpToPx(90), dpToPxY(220) + thisMap.getYPositionOfMap(), dpToPx(90), dpToPxY(90)));
            canvas.drawBitmap(roadEllipse[3], dpToPx(90) - dpToPx(90), dpToPxY(220) + thisMap.getYPositionOfMap() - dpToPxY(90), paint);

            thisMap.addRoadRect(new rectangle(dpToPx(0), dpToPxY(200) + thisMap.getYPositionOfMap(), dpToPx(70), dpToPxY(130)));
            canvas.drawBitmap(roadVerticalRect[3], dpToPx(0), thisMap.getYPositionOfMap() + dpToPxY(200), paint);

            thisMap.addRoadRect(new rectangle(dpToPx(100), dpToPxY(240) + thisMap.getYPositionOfMap(), dpToPx(220), dpToPxY(70)));
            canvas.drawBitmap(roadHorizontalRect[2], dpToPx(100), thisMap.getYPositionOfMap() + dpToPxY(240), paint);

            thisMap.addRoadRect(new rectangle(dpToPx(250), dpToPxY(110) + thisMap.getYPositionOfMap(), dpToPx(100), dpToPxY(140)));
            canvas.drawBitmap(roadVerticalRect[8], dpToPx(250), thisMap.getYPositionOfMap() + dpToPxY(110), paint);


            thisMap.addRoadRect(new rectangle(dpToPx(120), dpToPxY(470) + thisMap.getYPositionOfMap(), dpToPx(70), dpToPxY(70)));
            canvas.drawBitmap(roadVerticalRect[7], dpToPx(120), thisMap.getYPositionOfMap() + dpToPxY(470), paint);


            //--------------Restricted Shapes map3merged-----------//
            thisMap.addRestrictedEllipse(new ellipse(dpToPx(90), dpToPxY(220) + thisMap.getYPositionOfMap(), dpToPx(40), dpToPxY(40)));
            canvas.drawBitmap(restrictedEllipse[3], dpToPx(90) - dpToPx(40), dpToPxY(220) + thisMap.getYPositionOfMap() - dpToPxY(40), paint);

            thisMap.addRestrictedRect(new rectangle(dpToPx(50), dpToPxY(220) + thisMap.getYPositionOfMap(), dpToPx(70), dpToPxY(120)));
            canvas.drawBitmap(restrictedRectangle[0], dpToPx(50), thisMap.getYPositionOfMap() + dpToPxY(220), paint);

        }
    }


    private void createMap3(map thisMap) {
        if (thisMap.getYPositionOfMap() + thisMap.getHeight() > -10 && thisMap.getYPositionOfMap() < dpToPxY(533) + 10) {


            thisMap.clearAllRoadsAndRestrictions();//we first clear all [if any] road and restricted shapes on that map by calling the clearAll method of the map


            //---------------Roads Shapes---------------//

            thisMap.addRoadEllipse(new ellipse(dpToPx(160), dpToPxY(100) + thisMap.getYPositionOfMap(), dpToPx(100), dpToPxY(60)));
            canvas.drawBitmap(roadEllipse[0], dpToPx(160) - dpToPx(100), dpToPxY(100) + thisMap.getYPositionOfMap() - dpToPxY(60), paint);


            thisMap.addRoadRect(new rectangle(dpToPx(120), dpToPxY(0) + thisMap.getYPositionOfMap(), dpToPx(70), dpToPxY(70)));
            canvas.drawBitmap(roadVerticalRect[7], dpToPx(120), thisMap.getYPositionOfMap() + dpToPxY(0), paint);

            thisMap.addRoadRect(new rectangle(dpToPx(120), dpToPxY(150) + thisMap.getYPositionOfMap(), dpToPx(70), dpToPxY(70)));
            canvas.drawBitmap(roadVerticalRect[7], dpToPx(120), thisMap.getYPositionOfMap() + dpToPxY(150), paint);

            //--------------Restricted Shapes-----------//
            thisMap.addRestrictedEllipse(new ellipse(dpToPx(160), dpToPxY(100) + thisMap.getYPositionOfMap(), dpToPx(50), dpToPxY(30)));
            canvas.drawBitmap(restrictedEllipse[0], dpToPx(160) - dpToPx(50), dpToPxY(100) + thisMap.getYPositionOfMap() - dpToPxY(30), paint);

            thisMap.addRestrictedRect(new rectangle(dpToPx(190), dpToPxY(80) + thisMap.getYPositionOfMap(), dpToPx(70), dpToPxY(30)));
            canvas.drawBitmap(restrictedRectangle[2], dpToPx(190), thisMap.getYPositionOfMap() + dpToPxY(80), paint);


            //--------------Restricted Shapes-----------//
        }

    }


    private void createMap4(map thisMap) {
        if (thisMap.getYPositionOfMap() + thisMap.getHeight() > -10 && thisMap.getYPositionOfMap() < dpToPxY(533) + 10) {

            thisMap.clearAllRoadsAndRestrictions();//we first clear all [if any] road and restricted shapes on that map by calling the clearAll method of the map


            //---------------Roads Shapes---------------//
            thisMap.addRoadRect(new rectangle(dpToPx(120), dpToPxY(0) + thisMap.getYPositionOfMap(), dpToPx(70), dpToPxY(70)));
            canvas.drawBitmap(roadVerticalRect[7], dpToPx(120), thisMap.getYPositionOfMap() + dpToPxY(0), paint);

            thisMap.addRoadRect(new rectangle(dpToPx(120), dpToPxY(150) + thisMap.getYPositionOfMap(), dpToPx(70), dpToPxY(70)));
            canvas.drawBitmap(roadVerticalRect[7], dpToPx(120), thisMap.getYPositionOfMap() + dpToPxY(150), paint);


            thisMap.addRoadEllipse(new ellipse(dpToPx(160), dpToPxY(120) + thisMap.getYPositionOfMap(), dpToPx(150), dpToPxY(70)));
            canvas.drawBitmap(roadEllipse[1], dpToPx(160) - dpToPx(150), dpToPxY(120) + thisMap.getYPositionOfMap() - dpToPxY(70), paint);

            //--------------Restricted Shapes-----------//

            thisMap.addRestrictedEllipse(new ellipse(dpToPx(160), dpToPxY(120) + thisMap.getYPositionOfMap(), dpToPx(75), dpToPxY(35)));
            canvas.drawBitmap(restrictedEllipse[1], dpToPx(160) - dpToPx(75), dpToPxY(120) + thisMap.getYPositionOfMap() - dpToPxY(35), paint);

            thisMap.addRestrictedRect(new rectangle(dpToPx(0), dpToPxY(102) + thisMap.getYPositionOfMap(), dpToPx(100), dpToPxY(40)));
            canvas.drawBitmap(restrictedRectangle[3], dpToPx(0), thisMap.getYPositionOfMap() + dpToPxY(102), paint);


        }

    }


    private void createMap5(map thisMap) {
        if (thisMap.getYPositionOfMap() + thisMap.getHeight() > -10 && thisMap.getYPositionOfMap() < dpToPxY(533) + 10) {

            thisMap.clearAllRoadsAndRestrictions();//we first clear all [if any] road and restricted shapes on that map by calling the clearAll method of the map

            //---------------Roads Shapes---------------//
            thisMap.addRoadRect(new rectangle(dpToPx(120), dpToPxY(0) + thisMap.getYPositionOfMap(), dpToPx(70), dpToPxY(70)));
            canvas.drawBitmap(roadVerticalRect[7], dpToPx(120), thisMap.getYPositionOfMap() + dpToPxY(0), paint);

            thisMap.addRoadRect(new rectangle(dpToPx(120), dpToPxY(150) + thisMap.getYPositionOfMap(), dpToPx(70), dpToPxY(70)));
            canvas.drawBitmap(roadVerticalRect[7], dpToPx(120), thisMap.getYPositionOfMap() + dpToPxY(150), paint);


            thisMap.addRoadEllipse(new ellipse(dpToPx(160), dpToPxY(120) + thisMap.getYPositionOfMap(), dpToPx(150), dpToPxY(70)));
            canvas.drawBitmap(roadEllipse[1], dpToPx(160) - dpToPx(150), dpToPxY(120) + thisMap.getYPositionOfMap() - dpToPxY(70), paint);

            //--------------Restricted Shapes-----------//

            thisMap.addRestrictedEllipse(new ellipse(dpToPx(160), dpToPxY(120) + thisMap.getYPositionOfMap(), dpToPx(75), dpToPxY(35)));
            canvas.drawBitmap(restrictedEllipse[1], dpToPx(160) - dpToPx(75), dpToPxY(120) + thisMap.getYPositionOfMap() - dpToPxY(35), paint);

            thisMap.addRestrictedRect(new rectangle(dpToPx(0), dpToPxY(102) + thisMap.getYPositionOfMap(), dpToPx(100), dpToPxY(40)));
            canvas.drawBitmap(restrictedRectangle[3], dpToPx(0), thisMap.getYPositionOfMap() + dpToPxY(102), paint);

        }
    }

    public boolean touchListener(View v, MotionEvent event) {
        //the three lines below get me my touches.
        // To be honest, I do not know how they work, but I just need them because they provide me with
        // my touches and then I can handle it myself
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
        int pointerId = event.getPointerId(pointerIndex);

        //We're going to go thru each type of finger touch
        switch (action) {
            //if finger is touched on screeen
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:

                if (pointerId == 0)//we get which finger was touched
                {
                    fingerDown[0] = true;//and that specific finger is touched so our variable is true
                    x[0] = event.getX(pointerIndex);//and we get our x coordinate for that touch
                    y[0] = event.getY(pointerIndex);//also our y coordinate
                }
                if (pointerId == 1)//these are simply repeated for additional touches
                {
                    fingerDown[1] = true;
                    x[1] = event.getX(pointerIndex);
                    y[1] = event.getY(pointerIndex);
                }
                if (pointerId == 2) {
                    fingerDown[2] = true;
                    x[2] = event.getX(pointerIndex);
                    y[2] = event.getY(pointerIndex);
                }
                break;//lets us get out of the case

            case MotionEvent.ACTION_UP://else if a finger went offline (or off the screen)
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                //we handle it by changing the relative variables
                if (pointerId == 0)//if the first finger went off
                {
                    fingerDown[0] = false;//that down varbale is false
                    fingerMoving[0] = false;//and that finger is not moving

                    x[0] = event.getX(pointerIndex);//we get the cooridinate of where the finger went offline
                    y[0] = event.getY(pointerIndex);
                }
                if (pointerId == 1)//again simply repeated for additional fingers
                {
                    fingerDown[1] = false;
                    fingerMoving[1] = false;
                    x[1] = event.getX(pointerIndex);
                    y[1] = event.getY(pointerIndex);
                }
                if (pointerId == 2) {
                    fingerDown[2] = false;
                    fingerMoving[2] = false;
                    x[2] = event.getX(pointerIndex);
                    y[2] = event.getY(pointerIndex);
                }

                break;

            case MotionEvent.ACTION_MOVE://this case is like when a finger moves accross a screen (drags)

                int pointerCount = event.getPointerCount();//we get the number of touches that were dragging
                for (int i = 0; i < pointerCount; ++i) {
                    pointerIndex = i;//then we simply go thru all of them to handle it
                    pointerId = event.getPointerId(pointerIndex);
                    if (pointerId == 0)//for example, if the first one is draged
                    {
                        fingerDown[0] = true;//then it is touching
                        fingerMoving[0] = true;//and also moving
                        x[0] = event.getX(pointerIndex);//we get the cooridnates for that finger too
                        y[0] = event.getY(pointerIndex);
                    }
                    if (pointerId == 1)//again this is simply repeated for each finger
                    {
                        fingerDown[1] = true;
                        fingerMoving[1] = true;
                        x[1] = event.getX(pointerIndex);
                        y[1] = event.getY(pointerIndex);
                    }
                    if (pointerId == 2) {
                        fingerDown[2] = true;
                        fingerMoving[2] = true;
                        x[2] = event.getX(pointerIndex);
                        y[2] = event.getY(pointerIndex);
                    }
                }
                break;
        }
        return true;
    }

    public void stop(){
        isRunning=false;
        onPlayGame = false;
    }
}
