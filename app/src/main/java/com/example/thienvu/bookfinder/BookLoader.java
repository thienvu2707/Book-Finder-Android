package com.example.thienvu.bookfinder;

import android.content.AsyncTaskLoader;
import android.content.Context;

/**
 * Created by thienvu on 2/15/17.
 */

public class BookLoader extends AsyncTaskLoader<BookDetails> {
    //Tag for a log message
    private static final String LOG_TAG = BookLoader.class.getName();

    //url variable get from Query utilities
    private String mUrl;

    /**
     * Constructor of the loader
     * @param context
     * @param url
     */
    public BookLoader(Context context, String url)
    {
        super(context);
        mUrl = url;
    }

    /**
     * Method on start loading is to trigger Asynctask loader
     */
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * this method is get request from background
     * @return
     */
    @Override
    public BookDetails loadInBackground() {
        //check if url null or not
        if (mUrl == null)
            return null;

        //if not null then we perform a network request
        BookDetails bookDetails = QueryUtils.fetchBookData(mUrl);

        return bookDetails;

    }
}
