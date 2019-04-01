/*
 * Classname: BookClient.java
 * Version: 1.0
 * Date: 2019-03-11
 * Copyright notice: https://www.youtube.com/watch?v=xoTKpstv9f0 (video on text recognition with Camera using Google Vision)
 * https://codelabs.developers.google.com/codelabs/barcodes/#4 (guide on Barcode Reading)
 * https://guides.codepath.com/android/Book-Search-Tutorial (guide on searching for books using ISBN)


 */

package ca.ualberta.cs.phebert.litx;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * This is our BookClient class this is used by scan to find search results from our ISBN
 * @author rcdavids
 * @version 1.0
 */

public class BookClient {
    private static final String API_BASE_URL = "http://openlibrary.org/ISBN/";
    private static final String API_END_URL = ".json";

    private AsyncHttpClient client;

    /**
     * Constructor for a BookClient object. Sets it client to be a new AsyncHttpClient
     */
    public BookClient() {
        this.client = new AsyncHttpClient();
    }

    /**
     * gets and ISBN and uses it to create the URL of the book in the openlibrary
     * @param ourISBN the isbn of the book
     */
    private String getApiUrl(String ourISBN) {
        //return API_BASE_URL + ourISBN +API_END_URL;
        return API_BASE_URL + ourISBN +API_END_URL;
    }

    /**
     * Method for accessing the result of a search with our URL
     * @param anISBN isbn of book
     */

    public void getBooks(final String anISBN, JsonHttpResponseHandler handler) {
        String url = getApiUrl(anISBN);
        client.get(url, handler);
    }
}
