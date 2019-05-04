package com.example.dave.assignmentv4;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
/*
        Created by David Mackenzie
        Date:24/05/2017
        Android Assignment Part 3
        File:Title.java
        */

public class Title extends AppCompatActivity {

    private static final String DEBUG_TAG = "Gestures";
    private GestureDetectorCompat mDetector;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        // Pass on the activity and color resource
        Utils.darkenStatusBar(this, R.color.red);

        //Get the relative layout
        relativeLayout=(RelativeLayout)findViewById(R.id.RelativeLayout);

        //Set on click listener for the view
        relativeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(Title.this, Game.class));
            }

        });

        //Declare textview object and add an animation to Title Label
        TextView tv= (TextView)findViewById(R.id.textView);
        Animation a = AnimationUtils.loadAnimation(this, R.anim.blink);
        a.reset();
        tv.clearAnimation();
        tv.startAnimation(a);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
