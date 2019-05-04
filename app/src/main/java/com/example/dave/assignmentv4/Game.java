package com.example.dave.assignmentv4;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;

import static com.example.dave.assignmentv4.CategoryConstants.*;

/*
Created by David Mackenzie
Date:24/05/2017
Android Assignment Part 3
File:Game.java
Description: Game file that is the view for the user and has the game logic
 */

public class Game extends AppCompatActivity {

    GridView gridview;
    Item[] gridArray;
    ImageAdapter iAdapter;
    int numClick = 0;
    int c=0;
    boolean matchStatus=false;
    boolean gameStatus=false;
    Random random = new Random();
    Intent i;
    Button btnNew;
    ImageView colorPreview;
    final int colGrey=R.drawable.grey;
    int color1;
    int color2;
    int gridSize;
    int columnCount;
    String gameMode;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES="MyPrefs";
    public static final String col1 ="col1Key";
    public static final String col2 ="col2Key";
    public static final String size="sizeKey";
    public static final String difficulty="difficultyKey";
    public static final String DEBUG_TAG= "DEBUG_TAG";
    TextView txtTimer;
    TextView lblColour;
    int timeLimit;
    CountDownTimer countDown;
    long timeLeft;
    long savedTimeLimit;
    int[] savedGridArray;
    long savedTimer;
    private HighscoresDBase hdb;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        hdb = new HighscoresDBase(this);
        db = hdb.getWritableDatabase();

        // Pass on the activity and color resource
        Utils.darkenStatusBar(this, R.color.red);

        //Set up preferences collection
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        //Get value for countdown timer
        txtTimer = (TextView) findViewById(R.id.txtTimer);

        //Get value for next color imageview
        lblColour = (TextView) findViewById(R.id.nextColour);
        colorPreview = (ImageView) findViewById(R.id.colorPreview);

        //Load user preferences
        getSharedpreferences();

        if(savedInstanceState!=null){
            //Get the remaining time after the activity has been restored
            savedTimeLimit = savedInstanceState.getLong("timeLeft");
            Log.i("RESUME timeLeft",String.valueOf(savedTimeLimit));

            //Restart timer with time remaining
            countDown = new myCountDown(savedTimeLimit,1000);
            countDown.start();
        }
        else{
            //Start the timer
            countDown = new myCountDown(timeLimit,1000);
            countDown.start();
        }

        //Get the values for the gridview
        gridview = (GridView) findViewById(R.id.gridview);
        gridview.setNumColumns(columnCount);
        gridArray = new Item[gridSize];

        //Use the image adapter to generate the board
        iAdapter = new ImageAdapter(this, gridArray);
        gridview.setAdapter(iAdapter);

        //Get color one for preview
        colorPreview.setImageResource(color1);

        //Calls method to start the game for the first time
        startGame();

        //Add an onClick listener to new game button
        btnNew=(Button)findViewById(R.id.btnNew);

        //Create a new game from the button event
        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
                startActivity(new Intent(Game.this, Game.class));
            }
        });

        //Dispose of any unused items in memory
        Runtime.getRuntime().gc();

        //Add an onclick listener to the grid tiles
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                //Disable the current grid position so it cannot be selected again
                v.setOnClickListener(null);

                //Checks to see if the grid square selected is grey
                if(gridArray[position].getColor()==colGrey){
                    //Assign new color to grid position
                    c = gridArray[position].nextColor(numClick,color1,color2);

                    //Assign new colour to grid
                    ((ImageView)v).setImageResource(c);

                    //Increase counter to alternate colour
                    numClick++;

                    Log.d(DEBUG_TAG,Integer.toString(numClick));
                }

                //Checks to see if there is three in a with color1
                matchStatus = checkForMatch(position);

                Log.d(DEBUG_TAG, "Check for lose color1: " + Boolean.toString(matchStatus));

                //Stops game if there is three in a row
                if (matchStatus) {
                    stopTimer();

                    //Disable gameboard
                    stopGame(gridview);
                    Toast.makeText(getApplicationContext(), "You lost, Try Again!",
                            Toast.LENGTH_SHORT).show();
                }

                //Checks if the board is full
                gameStatus = checkGameStatus();

                Log.d(DEBUG_TAG,"Check if board is full: "+Boolean.toString(gameStatus));

                // Display message if board is full and there is not 3 in a row
                if (gameStatus&&!matchStatus) {
                    //Disable game board
                    stopGame(gridview);
                    Toast.makeText(getApplicationContext(), "You won, Game Over!",
                            Toast.LENGTH_SHORT).show();
                    showInputDialog();
                }
            }
        });
    }

    class myCountDown extends CountDownTimer{

        public myCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {

            timeLeft=l;

            txtTimer.setText("" + l / 1000);

            //If extreme mode is selected add a tile at 15second, 10second, 5second interval
            if (gameMode.equalsIgnoreCase("Extreme")) {
                if (l <= 16000 && l >= 15000) {
                    addRandomTile();
                }

                if (l <= 11000 && l >= 10000) {
                    addRandomTile();
                }

                if (l <= 6000 && l >= 5000) {
                    addRandomTile();
                }
            }

            //Make gridboard flash with 6 seconds remaining
            if (l <= 6000) {
                Animation a = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
                a.reset();
                gridview.clearAnimation();
                gridview.startAnimation(a);
            }
        }

        @Override
        public void onFinish() {
            gridview.clearAnimation();
            txtTimer.setText("0");
            stopGame(gridview);
            Toast.makeText(getApplicationContext(), "You lost, Try Again!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //Method used to start the game.
    public void startGame(){
        //Reset click counter back to 0
        numClick=0;
        //Generate grid array depending on what size grid is selected
        // array with all Items in the grid set to the grey image
        for(int i =0; i<gridSize;i++){
            gridArray[i] = new Item(R.drawable.grey,"grey");
        }

        gridview = (GridView)findViewById(R.id.gridview);
        //Randomize 4 tiles
        addRandomTiles(gridview);

        //Display the gridview for the board
        iAdapter = new ImageAdapter(this,gridArray);
        gridview.setAdapter(iAdapter);
        gridview.setEnabled(true);
    }

    //Method used to disable the gameboard after the game is over
    public void stopGame(GridView gridview){
        stopTimer();
        gridview.clearAnimation();
        gridview.setEnabled(false);
    }


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        //Save values into savedInstanceState object
        // Save the values you need from your textview into "outState"-object
        countDown.cancel();

        //Declare array to temporarily store variables
        savedGridArray= new int[gridSize];

        int save;
        //Save values to the array to be displayed on the screen after rotating
        for(int i =0; i<gridSize;i++){
            save = gridArray[i].getColor();
            savedGridArray[i]=save;
        }

        //Save values to be used to recreate after activity is destroyed
        savedInstanceState.putLong("timeLeft",timeLeft);
        savedInstanceState.putIntArray("savedGridArray",savedGridArray);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        //Get value for countdown timer
        txtTimer = (TextView) findViewById(R.id.txtTimer);

        //Get values to repopulate grid
        savedGridArray = savedInstanceState.getIntArray("savedGridArray");

        //Generate grid depending on gridsize with previous values before orientation change
        for(int i =0; i<gridSize;i++){
            gridArray[i].setColor(savedGridArray[i]);
        }

        gridview = (GridView)findViewById(R.id.gridview);

        //Display the gridview for the board
        iAdapter = new ImageAdapter(this,gridArray);
        gridview.setAdapter(iAdapter);
        gridview.setEnabled(true);

        Log.i("savedTimer",String.valueOf(savedTimer));
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        stopTimer();
    }

    //Method used to check if there is a winner
    public boolean checkForMatch(int position){
        //Get colour for current position
        int test=gridArray[position].getColor();

        //Get the X values
        int x = position%columnCount;
        int positionLeft = x-1;
        int positionFarLeft = x-2;
        int positionRight = x+1;
        int positionFarRight = x+2;

        //Check to see if the X positions are out of the row
        int left = position-1;
        int farLeft = position-2;
        int right = position+1;
        int farRight = position+2;

        //Get the Y values
        int y = position;
        int up = (y-columnCount);
        int farUp = (y-columnCount*2);
        int down = (y+columnCount);
        int farDown = (y+columnCount*2);

        Log.d(DEBUG_TAG,"farLEFT: " + farLeft + " Left: " + left
                + " Current Position: " + x + " Right: " + right + " farRight: "
                + farRight);

        Log.d(DEBUG_TAG,"farUp: " + farUp+ " Up: " + up
                + " Current Position: " + y + " Down: " + down+ " farDown: "
                + farDown);

        Log.d(DEBUG_TAG,x + " "+ y);

       //Check for a match horizontally
        //Test if the position is out of bounds -1 for left or 0 right
        if(positionLeft%columnCount!=-1 && positionRight%columnCount!=0 ){
            if(test==gridArray[left].getColor()&&test==gridArray[right].getColor()){
                gridArray[left].setFailColor(test);
                gridArray[right].setFailColor(test);
                gridArray[position].setFailColor(test);

                //Use the image adapter to show where the three in a row occurred on the board
                iAdapter = new ImageAdapter(this, gridArray);
                gridview.setAdapter(iAdapter);
                return true;
            }
        }

        //Test if the position is out of bounds -1 for left or 0 right
        if(positionLeft%columnCount!=-1 && positionFarLeft%columnCount!=-1 ){
            if(test==gridArray[left].getColor()&&test==gridArray[farLeft].getColor()){
                gridArray[left].setFailColor(test);
                gridArray[farLeft].setFailColor(test);
                gridArray[position].setFailColor(test);

                //Use the image adapter to show where the three in a row occurred on the board
                iAdapter = new ImageAdapter(this, gridArray);
                gridview.setAdapter(iAdapter);
                return true;
            }
        }

        //Test if the position is out of bounds -1 for left or 0 right
        if(positionFarRight%columnCount!=0 && positionRight%columnCount!=0){
            if(test==gridArray[farRight].getColor()&&test==gridArray[right].getColor()){
                gridArray[right].setFailColor(test);
                gridArray[farRight].setFailColor(test);
                gridArray[position].setFailColor(test);

                //Use the image adapter to show where the three in a row occurred on the board
                iAdapter = new ImageAdapter(this, gridArray);
                gridview.setAdapter(iAdapter);
                return true;
            }
        }

        //Check for match vertically
        if(up>=0 && up<=gridSize && down>=0 && down<gridSize){
            if(test==gridArray[up].getColor()&&test==gridArray[down].getColor()){
                gridArray[up].setFailColor(test);
                gridArray[down].setFailColor(test);
                gridArray[position].setFailColor(test);

                //Use the image adapter to show where the three in a row occurred on the board
                iAdapter = new ImageAdapter(this, gridArray);
                gridview.setAdapter(iAdapter);
                return true;
            }
        }

        if(up>=0 && up<=gridSize && farUp>=0 && farUp <gridSize){
            if(test==gridArray[up].getColor()&&test==gridArray[farUp].getColor()){
                gridArray[up].setFailColor(test);
                gridArray[farUp].setFailColor(test);
                gridArray[position].setFailColor(test);

                //Use the image adapter to show where the three in a row occurred on the board
                iAdapter = new ImageAdapter(this, gridArray);
                gridview.setAdapter(iAdapter);
                return true;
            }
        }

        if(down>=0 && down<=gridSize && farDown>=0 && farDown <gridSize){
            if(test==gridArray[down].getColor()&&test==gridArray[farDown].getColor()){
                gridArray[down].setFailColor(test);
                gridArray[farDown].setFailColor(test);
                gridArray[position].setFailColor(test);

                //Use the image adapter to show where the three in a row occurred on the board
                iAdapter = new ImageAdapter(this, gridArray);
                gridview.setAdapter(iAdapter);
                return true;
            }
        }

        //Return false if there is not match on the board
        return false;
    }

    //Checks to see if the board is full and the match is over
    //Returns false if the board is full
    public boolean checkGameStatus(){
        int gameCount=0;
        //Counts the amount of squares that are not grey to determine if the board is full
        for(int i=0;i<gridSize;i++){
            if(gridArray[i].getColor()!=R.drawable.grey){
                gameCount++;
            }
        }

        //Update color preview
        colourPreview(gameCount);

        //Checks to see if the board is full
        if(gameCount==gridSize) {
            return true;
        }
        return false;
    }

    //Method to add one tile in extreme setting randomly
    public void addRandomTile(){
        int num1=0;
        int randomColor=random.nextInt(2);

        //Generate number 1
        num1=random.nextInt(gridSize);

        //Find a grid item that does not already have a colour
        do {
            num1 = random.nextInt(gridSize);
        }while(gridArray[num1].getColor()!=colGrey);

        //Set new colour for randomly selected tile
        gridArray[num1].nextColor(randomColor,color1,color2);

        //Use the image adapter to add one random tile to the board
        iAdapter = new ImageAdapter(this, gridArray);
        gridview.setAdapter(iAdapter);

        //Checks to see if there is three in a with random tile
        matchStatus = checkForMatch(num1);

        Log.d(DEBUG_TAG, "Check for lose color1: " + Boolean.toString(matchStatus));

        //Stops game if there is three in a row
        if (matchStatus) {
            stopTimer();

            //Disable gameboard
            stopGame(gridview);
            Toast.makeText(getApplicationContext(), "You lost, Try Again!",
                    Toast.LENGTH_SHORT).show();
        }

        //Checks if the board is full
        gameStatus = checkGameStatus();

        Log.d(DEBUG_TAG,"Check if board is full: "+Boolean.toString(gameStatus));

        // Display message if board is full and there is not 3 in a row
        if (gameStatus&&!matchStatus) {
            //Disable game board
            stopGame(gridview);
            Toast.makeText(getApplicationContext(), "You won, Game Over!",
                    Toast.LENGTH_SHORT).show();
            showInputDialog();
        }
    }

    //Method used to generate 4 random tiles on the board at startup
    public void addRandomTiles(GridView gridview){
        //Random number variables
        int num1=0;
        int num2=0;
        int num3=0;
        int num4=0;

        //Items to be assigned to the GridView
        Item i1 = new Item(color1,"Colour1");
        Item i2 = new Item(color1,"Colour1");
        Item i3 = new Item(color2,"Colour2");
        Item i4 = new Item(color2,"Colour2");

        //Generate number 1
        num1=random.nextInt(gridSize);

        //Generate a number 2 that does not match number 1
        do {
            num2 = random.nextInt(gridSize);
        }while(num1==num2);

        //Generate a number 3 that doesn't match number 1 or 2
        do {
            num3 = random.nextInt(gridSize);
        }while(num3==num2||num3==num1);

        //Generate a number 4 that doesn't match number 1 or 2 or 3
        do{
            num4=random.nextInt(gridSize);
        }while(num4==num3||num4==num2||num4==num1);

        //Generate 4 random tiles on board
        gridArray[num1]=i1;
        gridArray[num2]=i2;
        gridArray[num3]=i3;
        gridArray[num4]=i4;
    }

    //Method used to generate preview color on view
    public void colourPreview(int count){
        if (count % 2 == 0) {
            colorPreview.setImageResource(color1);
        } else {
            colorPreview.setImageResource(color2);
        }
    }

    //Method used to retrieve saved values from local storage
    public void getSharedpreferences(){

        //Get stored colors from sharedpreferences
        String val1 = sharedpreferences.getString(col1,"");
        String val2 = sharedpreferences.getString(col2,"");
        String val3 = sharedpreferences.getString(size,"");
        String val4 = sharedpreferences.getString(difficulty,"");

        //Check to see which color1 the user has selected
        if(val1.equalsIgnoreCase("Yellow")){
            color1=R.drawable.yellow;
        }
        else if(val1.equalsIgnoreCase("Green")){
            color1=R.drawable.green;
        }
        else if(val1.equalsIgnoreCase("Red")){
            color1=R.drawable.red;
        }
        else if(val1.equalsIgnoreCase("White")){
            color1=R.drawable.white;
        }
        else if(val1.equalsIgnoreCase("Purple")) {
            color1=R.drawable.purple;
        }
        else{
            color1=R.drawable.blue;
        }

        //Check to see which color2 the user has selected
        if(val2.equalsIgnoreCase("Blue")){
            color2=R.drawable.blue;
        }
        else if(val2.equalsIgnoreCase("Green")){
            color2=R.drawable.green;
        }
        else if(val2.equalsIgnoreCase("Red")){
            color2=R.drawable.red;
        }
        else if(val2.equalsIgnoreCase("White")){
            color2=R.drawable.white;
        }
        else if(val2.equalsIgnoreCase("Purple")) {
            color2=R.drawable.purple;
        }
        else{
            color2=R.drawable.yellow;
        }

        //Check to see which grid size the user has selected
        if(val3.equalsIgnoreCase("5 x 5")){
            gridSize=25;
            columnCount=5;
        }
        else if(val3.equalsIgnoreCase("6 x 6")){
            gridSize=36;
            columnCount=6;
        }
        else if(val3.equalsIgnoreCase("7 x 7")){
            gridSize=49;
            columnCount=7;
        }
        else{
            gridSize=16;
            columnCount=4;
       }

       //Check to see which difficulty is selected
        if(val4.equalsIgnoreCase("Easy")||val4.equalsIgnoreCase("")){
                timeLimit=61000;
                gameMode="Easy";
                lblColour.setVisibility(View.VISIBLE);
                colorPreview.setVisibility(View.VISIBLE);
            }
            else if(val4.equalsIgnoreCase("Medium")){
                timeLimit=46000;
                gameMode="Medium";
                lblColour.setVisibility(View.VISIBLE);
                colorPreview.setVisibility(View.VISIBLE);
            }
            else if(val4.equalsIgnoreCase("Hard")){
                timeLimit=31000;
                gameMode="Hard";
                lblColour.setVisibility(View.VISIBLE);
                colorPreview.setVisibility(View.VISIBLE);
            }
            else{
                timeLimit=21000;
                gameMode="Extreme";
                lblColour.setVisibility(View.INVISIBLE);
                colorPreview.setVisibility(View.INVISIBLE);
            }
    }

    //Method used to stop timer counting down
    public void stopTimer(){
        //Stop timer
        countDown.cancel();
    }

    //Method used to add a highscore to the database
    private void addHighscore(String name){
        SQLiteDatabase db = hdb.getWritableDatabase();

        String sqlInsert = "Insert into " + TABLENAME + "(" + NAME +", "+ DIFFICULTY +", "+ TIME
                + ", "+ GRIDSIZE + ")" +" values('" + name +"', '" + gameMode
                + "', '"+ (((timeLimit)-timeLeft)/1000) + "', '" +columnCount + "')";
        Log.v("SQL INSERT:",sqlInsert);
        db.execSQL(sqlInsert);

    }   //End method addHighscore

    //Method used to show input dialog for user to enter their name after they have won the game
    protected void showInputDialog() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(Game.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Game.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        addHighscore(editText.getText().toString());
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
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
        //Stop the timer before moving to another activity
        stopTimer();

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
