package com.example.dave.assignmentv4;

/*
Created by David Mackenzie
        Date:05/05/2017
        Android Assignment Part 2
        File:ModelObject.java
        Description: Constructor used to make page viewer for help screen
        */
public enum ModelObject {
    PAGE1(R.string.page1, R.layout.content_page1),
    PAGE2(R.string.page2, R.layout.content_page2),
    PAGE3(R.string.page3, R.layout.content_page3),
    PAGE4(R.string.page4,R.layout.content_page4);

    private int mTitleResId;
    private int mLayoutResId;

    ModelObject(int titleResId, int layoutResId) {
        mTitleResId = titleResId;
        mLayoutResId = layoutResId;
    }

    public int getTitleResId() {
        return mTitleResId;
    }

    public int getLayoutResId() {
        return mLayoutResId;
    }
}
