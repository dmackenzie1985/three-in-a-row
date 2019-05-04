package com.example.dave.assignmentv4;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
/*
        Created by David Mackenzie
        Date:24/05/2017
        Android Assignment Part 3
        File:ImageAdapter.java
        */

public class ImageAdapter extends BaseAdapter {
    Item[] gridArray;   //An array of the Item class - our Item class represent color images
    private Context mContext;   //The current state of an app or object or Activity

    public ImageAdapter(Context c,Item[] gridArray){
        mContext=c;
        this.gridArray=gridArray;
    }

    public int getCount(){
        return gridArray.length;
    }

    public Object getItem(int position){
        return null;
    }

    public long getItemId(int position){
        return 0;
    }

    //Create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent){
        ImageView imageView;
        if(convertView==null){
            //If it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            int width = ((GridView)parent).getColumnWidth();
            imageView.setLayoutParams(new GridView.LayoutParams(width,width));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0,0,0,0);

        }
        else{
            imageView=(ImageView) convertView;
        }
        imageView.setImageResource(gridArray[position].getColor());
        return imageView;
    }
}

