package com.example.dave.assignmentv4;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import static com.example.dave.assignmentv4.R.id.spinner;

/*
Created by David Mackenzie
Date:24/05/2017
Android Assignment Part 3
File:Settings.java
Description: Activity used to display settings for user
 */
public class Settings extends AppCompatActivity {

    Button btnSaveSettings;
    Intent i;
    public static final String MyPREFERENCES="MyPrefs";
    public static final String col1 ="col1Key";
    public static final String col2 ="col2Key";
    public static final String size="sizeKey";
    public static final String difficulty="difficultyKey";
    SharedPreferences sharedpreferences;
    Spinner spnColor1;
    Spinner spnColor2;
    Spinner spnGrid;
    Spinner spnDifficulty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Pass on the activity and color resource
        Utils.darkenStatusBar(this, R.color.red);

        //Set up preferences collection
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        //Get values for spinners
        spnColor1 = (Spinner)findViewById(spinner);
        spnColor2 = (Spinner)findViewById(R.id.spinner2);
        spnGrid = (Spinner)findViewById(R.id.spinner3);
        spnDifficulty = (Spinner)findViewById(R.id.spinner4);

        //Get values from string.xml for spinners
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.spinColor, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                this, R.array.spinColor, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(
                this, R.array.spinGrid, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(
                this, R.array.spinDifficulty, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Assign values to spinners
        spnColor1.setAdapter(adapter);
        spnColor2.setAdapter(adapter2);
        spnGrid.setAdapter(adapter3);
        spnDifficulty.setAdapter(adapter4);

        //Get selected values from sharedpreferences
        spnColor1.setSelection(getIndex(spnColor1,sharedpreferences.getString(col1,"")));
        spnColor2.setSelection(getIndex(spnColor2,sharedpreferences.getString(col2,"")));
        spnGrid.setSelection(getIndex(spnGrid,sharedpreferences.getString(size,"")));
        spnDifficulty.setSelection(getIndex(spnDifficulty,sharedpreferences.getString(difficulty,"")));

        //Get click listener for Save button
        btnSaveSettings = (Button)findViewById(R.id.btnSaveSettings);

        btnSaveSettings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Code to save user preferences
                saveValues(v);
            }
        });

    }
    //Method to determine what preference is saved for settings
    private int getIndex(Spinner spinner, String myString)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
    }

    public void saveValues(View view){
        //Get values from spinners for user preferences
        String val = spnColor1.getSelectedItem().toString();
        String val2 = spnColor2.getSelectedItem().toString();
        String val3 = spnGrid.getSelectedItem().toString();
        String val4 = spnDifficulty.getSelectedItem().toString();

        //Checks to see if colour1 and colour 2 are different
        if(val.equalsIgnoreCase(val2)){
            Toast.makeText(Settings.this,"Color 1 and 2 must be different!",
                    Toast.LENGTH_SHORT).show();
        }
        else{
            SharedPreferences.Editor editor = sharedpreferences.edit();

            //Format is:editor.putString("key",value);
            editor.putString(col1,val);
            editor.putString(col2,val2);
            editor.putString(size,val3);
            editor.putString(difficulty,val4);

            //Saves the value to local storage
            editor.commit();

            Toast.makeText(Settings.this,"Settings saved!",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.this, Game.class));
        }

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
