package com.example.thienvu.bookfinder;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    //Tag for log message
    private static final String LOG_TAG = MainActivity.class.getName();

    //URL query for search google book API
    private static final String BOOK_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    //create loader id = to 1
    private static final int BOOK_LOADER_ID = 1;

    private Button mButton;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = (Button) findViewById(R.id.book_button);
        editText = (EditText) findViewById(R.id.search_book);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get Text from editText
                Log.v("EditText", editText.getText().toString());
                String getEditText = editText.getText().toString();
                if (getEditText == null || getEditText == " ") {
                    Toast.makeText(MainActivity.this, "Please enter name or title of the book", Toast.LENGTH_SHORT);
                } else
                {
                    BookAsyncTask task = new BookAsyncTask();
                    task.execute(getEditText);
                }
            }
        });

    }
    /**
     * Update user interface after getting the request from internet
     *
     * @param bookSearch
     */
    private void updateUi(BookDetails bookSearch) {
        TextView findTextView = (TextView) findViewById(R.id.title_book);
        findTextView.setText(bookSearch.getmTitle());
    }

    private class BookAsyncTask extends AsyncTask<String, Void, BookDetails> {

        @Override
        protected BookDetails doInBackground(String... urls) {
            //check if url null or not
            if (urls.length < 1 || urls[0] == null)
                return null;

            //create url object if it is true
            URL url = QueryUtils.createUrl(BOOK_REQUEST_URL + urls[0]);

            String jsonResponse = "";
            try {
                jsonResponse = QueryUtils.makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "error cannot get json response");
            }

            //Perform the HTTP request for book data and process the responsse
            BookDetails resultBook = QueryUtils.extractFromJson(jsonResponse);
            return resultBook;
        }

        @Override
        protected void onPostExecute(BookDetails bookDetails) {
            //check if the result is null or not
            if (bookDetails == null)
                return;

            //update information to displayed the user
            updateUi(bookDetails);
        }
    }

}
