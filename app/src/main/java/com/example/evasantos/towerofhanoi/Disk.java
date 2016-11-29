package com.example.evasantos.towerofhanoi;
import android.graphics.Rect;
/**
 * Created by Eva Santos on 11/20/2016.
 */
public class Disk extends GameElement {
    public boolean movable = false;
    public Disk(GameView view, int color, int x, int y, int width,int length) {
        super(view, color, x, y, width, length);
    }

    public void moveY(int newY){
        this.shape = new Rect(getX(),newY,getY() + getWidth(), getY() + getLength());
    }
    public void moveX(int newX){
        this.shape = new Rect(newX,getY(),getY() + getWidth(), getY() + getLength());
    }
    public void moveXandY(int newX, int newY) {
        x = newX;
        y = newY;
        this.shape = new Rect(x, y, width, length);
    }}