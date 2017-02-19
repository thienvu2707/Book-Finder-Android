package com.example.thienvu.bookfinder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by thienvu on 2/17/17.
 */

public class BookAdapter extends ArrayAdapter<BookDetails> {

    /**
     * Constructor for the book detail
     *
     * @param context
     * @param bookDetails
     */
    public BookAdapter(Context context, ArrayList<BookDetails> bookDetails) {
        super(context, 0, bookDetails);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //check if the existing View is being used or inflate
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_items, parent, false);

        //get  the items at the right position
        BookDetails currentBookDetails = getItem(position);

        //find the title in the list
        TextView findTitle = (TextView) convertView.findViewById(R.id.book_title);
        String bookTitle = currentBookDetails.getmTitle();
        findTitle.setText(bookTitle);

        //find the author in the list
        TextView findAuthors = (TextView) convertView.findViewById(R.id.book_authors);
        String bookAuthors = currentBookDetails.getmAuthor();
        findAuthors.setText(bookAuthors);

        return convertView;
    }
}

