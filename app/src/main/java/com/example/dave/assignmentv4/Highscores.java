package com.example.dave.assignmentv4;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;

import static com.example.dave.assignmentv4.CategoryConstants.*;
import static com.example.dave.assignmentv4.R.array.spinGrid;

/*
Created by David Mackenzie
Date:24/05/2017
Android Assignment Part 3
File:Highscores.java
Description: Activity used to highscores for user
 */
public class Highscores extends AppCompatActivity {

    Intent i;
    TableLayout tl;
    RelativeLayout layout;
    TableRow tr;
    TextView headingTV;
    TextView valueTV;
    TextView divider;
    private HighscoresDBase hdb;
    private SQLiteDatabase db;
    Spinner spnGrid;
    Spinner spnDifficulty;
    boolean spinnerTouched;
    public static final String MyPREFERENCES="MyPrefs";
    public static final String col1 ="col1Key";
    public static final String col2 ="col2Key";
    public static final String size="sizeKey";
    public static final String difficulty="difficultyKey";
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscores);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Pass on the activity and color resource
        Utils.darkenStatusBar(this, R.color.red);

        //Set up preferences collection
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        //Get values for spinners
        spnGrid = (Spinner)findViewById(R.id.spnGridsize);
        spnDifficulty = (Spinner)findViewById(R.id.spnDifficulty);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, spinGrid, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                this, R.array.spinDifficulty, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnGrid.setAdapter(adapter);
        spnDifficulty.setAdapter(adapter2);

        //Get values for the layout
        layout = (RelativeLayout)findViewById(R.id.highscoresLayout);

        //Get value for table
        tl = (TableLayout) findViewById(R.id.maintable);

        //Database variables
        hdb = new HighscoresDBase(this);
        db = hdb.getWritableDatabase();

        //Determine high scores values from shared preferences
        spnGrid.setSelection(getIndex(spnGrid,sharedpreferences.getString(size,"")));
        spnDifficulty.setSelection(getIndex(spnDifficulty,sharedpreferences.getString(difficulty,"")));

        //Get current values from spinners
        String grid=spnGrid.getSelectedItem().toString();
        String difficulty=spnDifficulty.getSelectedItem().toString();

        //Use values from spinners to query database
        Cursor cursor = getGroupHighscores(grid,difficulty);
        showHighscores(cursor);

        //Set event listener for when spinner is changed
        spnGrid.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    spinnerTouched = true; // User DID touched the spinner!
                }

                return false;
            }
        });

        //Set item selected listener to update highscores displayed
        spnGrid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                if (spinnerTouched) {
                    //Clear values from the table
                    tl.removeAllViews();

                    //Get current values from spinners
                    String grid=spnGrid.getSelectedItem().toString();
                    String difficulty=spnDifficulty.getSelectedItem().toString();

                    //Use values from spinners to query database
                    Cursor cursor = getGroupHighscores(grid,difficulty);
                    showHighscores(cursor);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        //Set item listener to update highscores display for different difficulties
        spnDifficulty.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    spinnerTouched = true; // User DID touched the spinner!
                }

                return false;
            }
        });
        spnDifficulty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                if (spinnerTouched) {
                    //Clear values from the table
                    tl.removeAllViews();

                    //Get current values from spinners
                    String grid=spnGrid.getSelectedItem().toString();
                    String difficulty=spnDifficulty.getSelectedItem().toString();

                    //Use values from spinners to query database
                    Cursor cursor = getGroupHighscores(grid,difficulty);
                    showHighscores(cursor);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }
    private Cursor getHighscores(){
        SQLiteDatabase db = hdb.getReadableDatabase();

        String sqlQuery = "Select * from " + TABLENAME + " Order By " +TIME + " ASC LIMIT 10 ";
        Cursor cursor = db.rawQuery(sqlQuery,null);
        return cursor;
    }   //End method get Highscores

    private Cursor getGroupHighscores(String grid,String difficulty){
        String gridsize="";
        //Check to see which grid size the user has selected
        if(grid.equalsIgnoreCase("5 x 5")){
            gridsize="5";
        }
        else if(grid.equalsIgnoreCase("6 x 6")){
            gridsize="6";
        }
        else if(grid.equalsIgnoreCase("7 x 7")){
            gridsize="7";
        }
        else{
            gridsize="4";
        }

        String sqlQuery = "Select * from " + TABLENAME + " Where difficulty = '"+difficulty
                + "' AND gridsize = '"+gridsize+"' ORDER BY " +TIME +" ASC LIMIT 10";
        Cursor cursor = db.rawQuery(sqlQuery,null);
        return cursor;
    }

    private void showHighscores(Cursor c){
        //Check to see if there is any records returned
        if(c.getCount()>0&&c!=null){
            addHeaders();
        }else{
            tl.removeAllViews();
        }

        //Variables to store records from database.
        String name;
        String difficulty;
        String time;
        String gridsize;

        int index=1;

        while(c.moveToNext()){
            tr = new TableRow(this);
            tr.setLayoutParams(new LayoutParams(
                    LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));
            name=c.getString(c.getColumnIndexOrThrow(NAME));
            difficulty=c.getString(c.getColumnIndexOrThrow(DIFFICULTY));
            time=c.getString(c.getColumnIndexOrThrow(TIME));
            gridsize=c.getString(c.getColumnIndexOrThrow(GRIDSIZE));

            /** Creating a TextView to add index to the row **/
            headingTV = new TextView(this);
            headingTV.setText(String.valueOf(index)+".");
            headingTV.setPadding(5, 5, 5, 5);
            tr.addView(headingTV);  // Adding textView to tablerow.

            /** Creating a TextView to add name to the row **/
            headingTV = new TextView(this);
            headingTV.setText(name);
            headingTV.setPadding(5, 5, 5, 5);
            tr.addView(headingTV);  // Adding textView to tablerow.

            /** Creating a TextView to add  difficulty to the row **/
            headingTV = new TextView(this);
            headingTV.setText(difficulty);
            headingTV.setPadding(5, 5, 5, 5);
            tr.addView(headingTV);  // Adding textView to tablerow.

            /** Creating a TextView to add time to the row **/
            headingTV = new TextView(this);
            headingTV.setText(time+"seconds");
            headingTV.setPadding(5, 5, 5, 5);
            tr.addView(headingTV);  // Adding textView to tablerow.

            /** Creating a TextView to add gridsize to the row **/
            headingTV = new TextView(this);
            headingTV.setText(gridsize+" X "+gridsize);
            headingTV.setPadding(5, 5, 5, 5);
            tr.addView(headingTV);  // Adding textView to tablerow.

            // Add the TableRow to the TableLayout
            tl.addView(tr, new TableLayout.LayoutParams(
                    LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));
            index++;
        }
    }   //End method showHighscores

    /** This function add the headers to the table **/
    public void addHeaders(){

        /** Create a TableRow dynamically **/
        tr = new TableRow(this);
        tr.setLayoutParams(new LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
        /** Creating a TextView to add to the row **/
        valueTV = new TextView(this);
        valueTV.setText(" ");
        valueTV.setPadding(5, 5, 5, 0);
        tr.addView(valueTV);  // Adding textView to tablerow.

        /** Creating a TextView to add to the row **/
        valueTV = new TextView(this);
        valueTV.setText("Name");
        valueTV.setPadding(5, 5, 5, 0);
        tr.addView(valueTV);  // Adding textView to tablerow.

        /** Creating another textview **/
        valueTV = new TextView(this);
        valueTV.setText("Difficulty");
        valueTV.setPadding(5, 5, 5, 0);
        tr.addView(valueTV); // Adding textView to tablerow.

        /** Creating another textview **/
        valueTV = new TextView(this);
        valueTV.setText("Time");
        valueTV.setPadding(5, 5, 5, 0);
        tr.addView(valueTV); // Adding textView to tablerow.

        /** Creating another textview **/
        valueTV = new TextView(this);
        valueTV.setText("Gridsize");
        valueTV.setPadding(5, 5, 5, 0);
        tr.addView(valueTV); // Adding textView to tablerow.

        // Add the TableRow to the TableLayout
        tl.addView(tr, new TableLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));

        // we are adding two textviews for the divider because we have two columns
        tr = new TableRow(this);
        tr.setLayoutParams(new LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));

        /** Creating another divider textview **/
        divider = new TextView(this);
        divider.setText("---------");
        divider.setPadding(5, 0, 0, 0);
        tr.addView(divider); // Adding textView to tablerow.

        /** Creating another divider textview **/
        divider = new TextView(this);
        divider.setText("------------------");
        divider.setPadding(5, 0, 0, 0);
        tr.addView(divider); // Adding textView to tablerow.

        /** Creating another divider textview **/
        divider = new TextView(this);
        divider.setText("------------------");
        divider.setPadding(5, 0, 0, 0);
        tr.addView(divider); // Adding textView to tablerow.

        /** Creating another divider textview **/
        divider = new TextView(this);
        divider.setText("-------------------");
        divider.setPadding(5, 0, 0, 0);
        tr.addView(divider); // Adding textView to tablerow.

        /** Creating another divider textview **/
        TextView divider = new TextView(this);
        divider.setText("-------------------");
        divider.setPadding(5, 0, 0, 0);
        tr.addView(divider); // Adding textView to tablerow.

        // Add the TableRow to the TableLayout
        tl.addView(tr, new TableLayout.LayoutParams(
                LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT));
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
