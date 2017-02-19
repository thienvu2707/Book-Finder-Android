package com.example.thienvu.bookfinder;

import android.os.Parcel;

/**
 * Created by thienvu on 2/13/17.
 */

public class BookDetails{
    //variable for book
    private String mTitle;
    private String mAuthor;

    /**
     * Constructor for book details
     */
    public BookDetails(String title, String author) {
        mTitle = title;
        mAuthor = author;
    }

    /**
     * Get the title fo the book
     */
    public String getmTitle() {
        return mTitle;
    }

    /**
     * get authors of the book
     *
     * @return
     */
    public String getmAuthor() {
        return mAuthor;
    }

    protected BookDetails(Parcel in) {
        mTitle = in.readString();
        mAuthor = in.readString();
    }
}
