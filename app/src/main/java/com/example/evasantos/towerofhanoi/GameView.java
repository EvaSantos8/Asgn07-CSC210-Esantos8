package com.example.evasantos.towerofhanoi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
/**
 * Created by Eva Santos on 11/20/2016.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback{

    private GameThread gameThread; // controls the game loop
    private Activity activity; // to display Game Over dialog in GUI thread
    private boolean dialogIsDisplayed = false;
    // dimension variables
    private int screenWidth;
    private int screenHeight;
    //Game objects
    Disk[] disks;
    Rod[] rods;
    //user input
    private static int numOfDisks = 3;
    // text size 1/18 of screen width
    public static final double TEXT_SIZE_PERCENT = 1.0 / 18;
    // constants for the Targets
    public static final double TARGET_WIDTH_PERCENT = 1.0 / 40;
    public static final double TARGET_LENGTH_PERCENT = 3.0 / 20;
    public static final double TARGET_FIRST_X_PERCENT = 3.0 / 5;
    public static final double TARGET_SPACING_PERCENT = 1.0 / 60;
    private static final String TAG = "GameView"; // for logging errors
    // variables for the game loop and tracking statistics
    private boolean gameOver; // is the game over?
    private double timeLeft; // time remaining in seconds
    private int shotsFired; // shots the user has fired
    private double totalElapsedTime; // elapsed seconds
    // Paint variables used when drawing each item on the screen
    private Paint textPaint; // Paint used to draw text
    private Paint backgroundPaint; // Paint used to clear the drawing area

    // constructor
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs); // call superclass constructor
        activity = (Activity) context; // store reference to MainActivity

        // register SurfaceHolder.Callback listener
        getHolder().addCallback(this);

        //paint bg
        textPaint = new Paint();
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.WHITE);
    }

    // called when the size of the SurfaceView changes,
    // such as when it's first added to the View hierarchy
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        screenWidth = w; // store CannonView's width
        screenHeight = h; // store CannonView's height

        // configure text properties
        textPaint.setTextSize((int) (TEXT_SIZE_PERCENT * screenHeight));
        textPaint.setAntiAlias(true); // smoothes the text
    }

    // get width of the game screen
    public int getScreenWidth() {
        return screenWidth;
    }

    // get height of the game screen
    public int getScreenHeight() {
        return screenHeight;
    }

    // reset all the screen elements and start a new game
    public void newGame() {
        //instantiate game objects
        rods = new Rod[3];
        disks = new Disk[numOfDisks];
        // initialize targetX for the first Target from the left
        int targetX = (int) (TARGET_FIRST_X_PERCENT * screenWidth);

        // calculate Y coordinate of Targets
        int targetY = (int) ((0.5 - TARGET_LENGTH_PERCENT / 2) *
                screenHeight);
        //rod coordinate variables
        int rodX = (int)(0.2 * screenWidth);
        int rodXGap = (int)(0.25 * screenWidth);
        int rodY= (int)(0.3 * screenHeight);
        int rodWidth = (int)((2.0/40) * screenWidth);
        int rodLength = (int) ((8.0/20) * screenHeight);
        //disk coordinate variables
        int diskX = (int)(0.075 * screenWidth);
        int diskY = (rodY + rodLength) -(int)(0.1 * screenHeight);
        int diskWidth = (int)((12.0/40) * screenWidth);
        int diskLength = (int) ((2.0/20) * screenHeight);
        //loop through disks and create them

        for(int x = 0; x < rods.length;x++)
        {
            int colorRod = Color.GRAY;
            rods[x] = new Rod(this, colorRod, rodX, rodY,
                    rodWidth,
                    rodLength);
            if(x == 0)
            {
                int colorDisk;
                for(int y = 0; y<disks.length;y++)
                {
                    if(y % 2 == 0)
                        colorDisk = Color.MAGENTA;
                    else
                        colorDisk = Color.BLUE;
                    disks[y] = new Disk(this, colorDisk, diskX, diskY,
                            diskWidth,
                            diskLength);
                    diskY -= diskLength;
                    diskX +=(int)(0.025*screenHeight);
                    diskWidth -= (int)(0.05*screenHeight);
                    disks[y].x = diskX;
                    disks[y].y = diskY;
                    disks[y].length = diskLength;
                    disks[y].width = diskWidth;
                }
            }
            rodX += rodXGap;
            rodX += rodWidth;
        }


        if (gameOver) { // start a new game after the last game ended
            gameOver = false; // the game is not over
            gameThread = new GameThread(getHolder()); // create thread
            gameThread.start(); // start the game loop thread
        }
        hideSystemBars();
    }
    // called repeatedly by the CannonThread to update game elements
    private void updatePositions(double elapsedTimeMS) {
        double interval = elapsedTimeMS / 1000.0; // convert to seconds

        timeLeft -= interval; // subtract from time left

        // if the timer reached zero
        /**
        if (timeLeft <= 0) {
            timeLeft = 0.0;
            gameOver = true; // the game is over
            gameThread.setRunning(false); // terminate thread
            showGameOverDialog(R.string.lose); // show the losing dialog
        }
         */
    }
    public void alignDisk(MotionEvent event) {
        // get the location of the touch in this view
        Point touchPoint = new Point((int) event.getX(),
                (int) event.getY());
        Disk target = disks[0];
        for(int y = 0; y<disks.length;y++) {
            if (disks[y].collidesWith(touchPoint)) {
                target = disks[y];
            }
        }
        target.moveXandY(touchPoint.x,touchPoint.y);
    }
    // display an AlertDialog when the game ends
    private void showGameOverDialog(final int messageId) {
        // Dialog fragment to display game stats and start new game
        final DialogFragment gameResult =
                new DialogFragment() {
                    // create an AlertDialog and return it
                    @Override
                    public Dialog onCreateDialog(Bundle bundle) {
                        // create dialog displaying String resource for messageId
                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(getActivity());
                        builder.setTitle(getResources().getString(messageId));

                        // display number of shots fired and total time elapsed
                        builder.setMessage(getResources().getString(
                                R.string.results_format, shotsFired, totalElapsedTime));
                        builder.setPositiveButton(R.string.reset_game,
                                new DialogInterface.OnClickListener() {
                                    // called when "Reset Game" Button is pressed
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialogIsDisplayed = false;
                                        newGame(); // set up and start a new game
                                    }
                                }
                        );

                        return builder.create(); // return the AlertDialog
                    }
                };

        // in GUI thread, use FragmentManager to display the DialogFragment
        activity.runOnUiThread(
                new Runnable() {
                    public void run() {
                        showSystemBars();
                        dialogIsDisplayed = true;
                        gameResult.setCancelable(false); // modal dialog
                        gameResult.show(activity.getFragmentManager(), "results");
                    }
                }
        );
    }
    // draws the game to the given Canvas
    public void drawGameElements(Canvas canvas) {
        // clear the background
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(),
                backgroundPaint);
        // draw all of the rods
        for (GameElement rod : rods)
            rod.draw(canvas);
        // draw all of the disks
        for (GameElement disk : disks)
            disk.draw(canvas);
        // display time remaining
        //canvas.drawText(getResources().getString(
                //R.string.time_remaining_format, timeLeft), 50, 100, textPaint);

    }
    // stops the game: called by CannonGameFragment's onPause method
    public void stopGame() {
        if (gameThread != null)
            gameThread.setRunning(false); // tell thread to terminate
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!dialogIsDisplayed) {
            newGame(); // set up and start a new game
            gameThread = new GameThread(holder); // create thread
            gameThread.setRunning(true); // start game running
            gameThread.start(); // start the game loop thread
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // ensure that thread terminates properly
        boolean retry = true;
        gameThread.setRunning(false); // terminate cannonThread

        while (retry) {
            try {
                gameThread.join(); // wait for cannonThread to finish
                retry = false;
            } catch (InterruptedException e) {
                Log.e(TAG, "Thread interrupted", e);
            }
        }
    }
    // called when the user touches the screen in this activity
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // get int representing the type of action which caused this event
        int action = e.getAction();

        // the user touched the screen or dragged along the screen
        if (action == MotionEvent.ACTION_DOWN ||
                action == MotionEvent.ACTION_MOVE) {
            alignDisk(e);
        }
        return true;
    }
    // Thread subclass to control the game loop
    private class GameThread extends Thread {
        private SurfaceHolder surfaceHolder; // for manipulating canvas
        private boolean threadIsRunning = true; // running by default

        // initializes the surface holder
        public GameThread(SurfaceHolder holder) {
            surfaceHolder = holder;
            setName("GameThread");
        }

        // changes running state
        public void setRunning(boolean running) {
            threadIsRunning = running;
        }

        // controls the game loop
        @Override
        public void run() {
            Canvas canvas = null; // used for drawing
            long previousFrameTime = System.currentTimeMillis();

            while (threadIsRunning) {
                try {
                    // get Canvas for exclusive drawing from this thread
                    canvas = surfaceHolder.lockCanvas(null);

                    // lock the surfaceHolder for drawing
                    synchronized (surfaceHolder) {
                        long currentTime = System.currentTimeMillis();
                        double elapsedTimeMS = currentTime - previousFrameTime;
                        totalElapsedTime += elapsedTimeMS / 1000.0;
                        updatePositions(elapsedTimeMS); // update game state
                        drawGameElements(canvas); // draw using the canvas
                        previousFrameTime = currentTime; // update previous time
                    }
                } finally {
                    // display canvas's contents on the CannonView
                    // and enable other threads to use the Canvas
                    if (canvas != null)
                        surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    // hide system bars and app bar
    private void hideSystemBars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    // show system bars and app bar
    private void showSystemBars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}
