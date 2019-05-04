package com.example.dave.assignmentv4;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.dave.assignmentv4.CategoryConstants.*;

/*
Created by David Mackenzie
Date:10/06/2017
Android Assignment Part 3
File:HighscoresDBase.java
Description: Class used to create SQLite database for highscores
 */
public class HighscoresDBase extends SQLiteOpenHelper {
    private static final String DBNAME="highscores.db";
    private static final int DBVERSION = 1;

    //Create a helper object for the hardware database
    public HighscoresDBase(Context ctx){
        super(ctx,DBNAME,null,DBVERSION);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL(
                "CREATE TABLE " + TABLENAME + " (" + _ID
                        + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + NAME + " TEXT NOT NULL, "
                        + DIFFICULTY + " TEXT NOT NULL, "
                        + TIME + " INTEGER NOT NULL, "
                        + GRIDSIZE +" TEXT NOT NULL);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME);
        onCreate(db);
    }
}
