package com.example.thienvu.bookfinder;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Tag for log message
    private static final String LOG_TAG = MainActivity.class.getName();

    /**
     * The URL for the book data
     */
    private static final String BOOK_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private static final String KEY_INDEX = "index";
    ArrayList<? extends BookDetails> mBookDetails;
    private Button mButton;
    private EditText mEditText;
    private int mCurrentIndex;
    private BookAdapter mAdapter;
    private String userInput;
    private ListView listView;
    private TextView mEmptyView;
    private View mProcessBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProcessBar = findViewById(R.id.process_bar);
        mProcessBar.setVisibility(View.INVISIBLE);

        //Check if the savedInstanceState is contain saved data
        if (savedInstanceState != null) {
            //if there is a saved data then save it in Current indext
            //and the KEY_INDEX to retrieve data in this line
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
        }

        //1st find the ListView
        listView = (ListView) findViewById(R.id.book_list);
        //find the emptyView
        mEmptyView = (TextView) findViewById(R.id.empty_view);
        listView.setEmptyView(mEmptyView);

        //set Adapter
        mAdapter = new BookAdapter(this, new ArrayList<BookDetails>());
        listView.setAdapter(mAdapter);

        //find search bar of the listView
        final EditText searchBar = (EditText) findViewById(R.id.search_bar);

        //find Button and set on click listener for it
        Button button = (Button) findViewById(R.id.book_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check if search bar is null or not
                //if search bar is null then send a error message
                if (searchBar.getText().toString().trim().equals("")) {
                    mEmptyView.setText(getString(R.string.fill_in_search_bar));
                } else {

                    mProcessBar = findViewById(R.id.process_bar);
                    mProcessBar.setVisibility(View.VISIBLE);

                    //if search bar is not null
                    //1st find editText
                    mEditText = (EditText) findViewById(R.id.search_bar);
                    //Replace user input "space" into "plus"
                    userInput = mEditText.getText().toString().replace(" ", "+");

                    //Create a new BookAsyncTask
                    BookAsyncTask task = new BookAsyncTask();

                    //check for connectivity network information
                    ConnectivityManager connect = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                    //get network information
                    NetworkInfo networkInfo = connect.getActiveNetworkInfo();
                    //checking if Edit Text is null or not
                    if (searchBar.getText().toString().trim().equals("")) {
                        //if edit text is null
                        //display an error message
                        mEmptyView.setText(R.string.fill_in_search_bar);
                    } else {
                        mEmptyView.setText(R.string.no_data_found);
                    }

                    if (networkInfo != null && networkInfo.isConnected()) {
                        task.execute();
                        mAdapter.clear();
                    } else {
                        mProcessBar = findViewById(R.id.process_bar);
                        mProcessBar.setVisibility(View.GONE);
                        //set empty state to message no connection
                        mEmptyView.setText(R.string.no_connection);
                        Toast toast = Toast.makeText(MainActivity.this, R.string.no_connection, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        });
    }

    //save the value of Current index variable
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("books", (ArrayList<? extends Parcelable>) mBookDetails);
        super.onSaveInstanceState(outState);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        //check if instance state is null or not
        if (savedInstanceState != null) {
            //if it is not null then
            //clear the previous adapter
            mAdapter.clear();
            mBookDetails = savedInstanceState.getParcelableArrayList("books");
            mAdapter.addAll(mBookDetails);
            listView.setAdapter(mAdapter);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    private class BookAsyncTask extends AsyncTask<URL, Void, List<BookDetails>> {

        @Override
        protected List<BookDetails> doInBackground(URL... urls) {
            //create URL object
            URL url = QueryUtils.createUrl(BOOK_REQUEST_URL + userInput + "&maxResults=10");

            //perform a HTTP request to the URL and receive the JSON response
            String jsonResponse = "";
            //try to catch HTTP request
            try {
                jsonResponse = QueryUtils.makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error with create a HTTP request", e);
            }

            //Extract the Json response and crete a BookDetails object
            List<BookDetails> bookDetails = QueryUtils.extractFromJson(jsonResponse);
            //return the result to AsyncTask
            return bookDetails;
        }

        @Override
        protected void onPostExecute(List<BookDetails> bookDetails) {
            mProcessBar = findViewById(R.id.process_bar);
            mProcessBar.setVisibility(View.GONE);

            //check if the background is null or not
            if (bookDetails == null) {
                return;
            } else {
                mBookDetails = (ArrayList<BookDetails>) bookDetails;
                updateUi(bookDetails);
            }
        }

        private void updateUi(List<BookDetails> bookDetails) {
            mAdapter.clear();
            mAdapter.addAll(bookDetails);
            mAdapter.notifyDataSetChanged();
        }
    }
}



