package com.example.dave.assignmentv4;

/*
Created by David Mackenzie
Date:10/06/2017
Android Assignment Part 3
File:CategoryConstants.java
Description: Constant variables used for highscore database
 */

import android.provider.BaseColumns;

public interface CategoryConstants extends BaseColumns {
    public static final String TABLENAME = "highscoresTable";

    //Columns in the highscores database
    public static final String NAME = "name";
    public static final String DIFFICULTY="difficulty";
    public static final String TIME="time";
    public static final String GRIDSIZE="gridsize";
}
