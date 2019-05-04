package com.example.dave.assignmentv4;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
/*
Created by David Mackenzie
Date:24/05/2017
Android Assignment Part 3
File:Help.java
Description: Activity used to display help for user
 */

public class Help extends AppCompatActivity {

    Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Pass on the activity and color resource
        Utils.darkenStatusBar(this, R.color.red);

        //Get value for viewpager
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new CustomPagerAdapter(this));

        //Dispose of any unused items in memory
        Runtime.getRuntime().gc();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_title, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

            /*
                An Intent is an object that provides runtime binding between separate
                components (such as two activites). The Intent represents an app's
                "intent to do something." You can use intents for a wide variets of tasks
                but in this app, your intent starts another activity.
             */

        switch (item.getItemId())
        {
            case R.id.new_game:
                i = new Intent(this, Game.class);
                startActivity(i);
                return true;
            case R.id.help:
                i = new Intent(this, Help.class);
                startActivity(i);
                return true;
            case R.id.highscores:
                i = new Intent(this, Highscores.class);
                startActivity(i);
                return true;
            case R.id.action_settings:
                i = new Intent(this, Settings.class);
                startActivity(i);
                return true;
        }
        return false;
    }

}
