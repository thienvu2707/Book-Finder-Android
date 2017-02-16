package com.example.thienvu.bookfinder;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by thienvu on 2/15/17.
 */

public final class QueryUtils {

    //Tag for the log message
    public static final String LOG_TAG = QueryUtils.class.getName();

    /**
     * Query to get information from internet then return BookDetail
     * @param requestUrl
     * @return
     */
    public static BookDetails fetchBookData (String requestUrl)
    {
        //create URL object
        URL url = createUrl(requestUrl);

        //perform a HTTP request to url and receive a JSON response
        String jsonResponse = null;
        //try to make a HTTP request
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e)
        {
            Log.e(LOG_TAG, "Error cannot make http request", e);
        }

        //Extract Json response return title of the book and create title book object
        BookDetails title = extractFromJson(jsonResponse);
        return title;
    }

    /**
     * Create to return the url from the give String url
     *
     * @param requestUrl
     * @return
     */
    public static URL createUrl(String requestUrl) {
        URL url = null;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error creating Url", e);
        }
        return url;
    }

    /**
     * This is how we make HTTP request from URL and we return the response
     * @param url
     * @return
     * @throws IOException
     */
    public static String makeHttpRequest(URL url) throws IOException {
        //create variable to get json
        String jsonResponse = "";

        //check if url is null or not
        if (url == null)
            //if null immediately return json response
            return jsonResponse;

        //create an empty url connection for the first time
        HttpURLConnection urlConnection = null;
        //Create a stream inputStream afer creating url
        InputStream inputStream = null;

        //if it is not null then we try create a url connection
        //then we try to connect to server
        try {
            //1st we open a connection to server
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(10000);
            //use method GET to download from url
            urlConnection.setRequestMethod("GET");

            //this to establish the connection
            urlConnection.connect();

            //check if the response code is OK or not
            if (urlConnection.getResponseCode() == 200) {
                //if ok then get the input stream
                inputStream = urlConnection.getInputStream();
                //we need json to read from the input stream
                jsonResponse = readFromStream(inputStream);
            } else
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem cannot retrieve JSON response from server", e);
        } finally {
            //check if urlConnection is null or not
            if (urlConnection != null)
                //if it's not null after done connecting then
                //disconnect it
                urlConnection.disconnect();

            //check if the inputStream is null or not
            if (inputStream != null)
                inputStream.close();
        }
        return jsonResponse;
    }

    /**
     * Convert input stream from server into String which contain JSON response
     * we need to read from
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static String readFromStream(InputStream inputStream) throws IOException
    {
        //create a StringBuilder to read from stream
        StringBuilder output = new StringBuilder();
        //check if the input stream is null or not
        if (inputStream != null)
        {
            //if input stream is not null
            //1st we create a stream reader to read from character utf-8
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            //2nd we create a buffer reader for optimize reading String
            BufferedReader reader = new BufferedReader(inputStreamReader);
            //now we read from url line by line
            String line = reader.readLine();
            while (line !=null)
            {
                //add more line to read
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static BookDetails extractFromJson (String bookJson)
    {
        //if the json is empty then i must return immediately
        if (TextUtils.isEmpty(bookJson))
            return null;

        //trying to parse JSON
        try {
            //1st create new root of Json
            JSONObject root = new JSONObject(bookJson);

            //then get JSON array in the root
            JSONArray items = root.getJSONArray("items");

            //check if there an result for what you search
            if (items.length() > 0)
            {
                //extract out the first title searching for book
                JSONObject firstItem = items.getJSONObject(0);
                JSONObject volumeInfo = firstItem.getJSONObject("volumeInfo");

                //Extract out the title of the book
                String title = volumeInfo.getString("title");

                //final we create new object for Book
                return new BookDetails(title);
            }
        } catch (JSONException e)
        {
            Log.e(LOG_TAG, "Problem parsing the Json result", e);
        }
        return null;
    }

}
