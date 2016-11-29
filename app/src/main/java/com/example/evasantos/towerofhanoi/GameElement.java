package com.example.evasantos.towerofhanoi;

/**
 * Created by Eva Santos on 11/20/2016.
 */

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Point;

public class GameElement {
    protected GameView view; // the view that contains this GameElement
    protected Paint paint = new Paint(); // Paint to draw this GameElement
    protected Rect shape; // the GameElement's rectangular bounds
    private float velocityX; // the vertical velocity of this GameElement
    private float velocityY; // the vertical velocity of this GameElement
    private int soundId; // the sound associated with this GameElement
    int x = 0;
    int y = 0;
     int width;
    int length;
    int color;
    // public constructor
    public GameElement(GameView view, int color, int x,
                       int y, int width, int length) {
        this.view = view;
        this.color = color;
        paint.setColor(color);
        shape = new Rect(x, y, x + width, y + length); // set bounds
        this.velocityY = 0;
        this.velocityX = 0;
        this.x = x;
        this.y = y;
        this.width = width;
        this.length = length;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public int getWidth(){
        return width;
    }
    public int getLength(){
        return length;
    }
    // update GameElement position and check for wall collisions
    public void update(double interval) {
        // update vertical position
        shape.offset(0, (int) (velocityY * interval));

        // if this GameElement collides with the wall, reverse direction
        if (shape.top < 0 && velocityY < 0 ||
                shape.bottom > view.getScreenHeight() && velocityY > 0)
            velocityY *= -1; // reverse this GameElement's velocity
    }
    //change velocity
    public void setVelocityY(float newVel)
    {
        velocityY = newVel;
    }
    public void setVelocityX(float newVel)
    {
        velocityX = newVel;
    }
    // draws this GameElement on the given Canvas
    public void draw(Canvas canvas) {
        canvas.drawRect(shape, paint);
    }
    //checks for collision with another object
    public boolean collidesWith(GameElement element) {
        return (Rect.intersects(shape, element.shape));
    }
    public boolean collidesWith(Point pt) {
        Rect element = new Rect(pt.x,pt.y,1,1);
        return Rect.intersects(shape, element);
    }
}

