package com.example.dave.assignmentv4;


/*
Created by David Mackenzie
Date:24/05/2017
Android Assignment Part 3
File:Item.java
 */


public class Item {

    private int colorImg;

    public Item(int colImg,String title){
        this.setColor(colImg);
    }

    public int getColor(){
        return colorImg;
    }

    public void setColor(int col){
        colorImg=col;
    }

    public void setFailColor(int col){
        switch(col){
            case R.drawable.blue:
                colorImg=R.mipmap.fail_blue;
                break;
            case R.drawable.green:
                colorImg=R.mipmap.fail_green;
                break;
            case R.drawable.purple:
                colorImg=R.mipmap.fail_purple;
                break;
            case R.drawable.red:
                colorImg=R.mipmap.fail_red;
                break;
            case R.drawable.white:
                colorImg=R.mipmap.fail_white;
                break;
            case R.drawable.yellow:
                colorImg=R.mipmap.fail_yellow;
                break;
        }
    }

    public int nextColor(int numClick,int color1,int color2){
        if(colorImg == R.drawable.grey) {
            if (numClick % 2 == 0) {
                colorImg = color1;
            } else {
                colorImg = color2;
            }
        }
        return colorImg;
    }


}
