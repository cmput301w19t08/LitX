/*
 * NOT FOR PART 4 THIS WILL BE PART OF PART 5
 * Classname: AuthorClient.java
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

public class AuthorClient {
    private static final String API_BASE_URL = "http://openlibrary.org/authors/";
    private static final String API_END_URL = ".json";

    private AsyncHttpClient client;
    //private String booksISBN;

    public AuthorClient() {
        this.client = new AsyncHttpClient();
        //this.booksISBN = ISBN;
    }

    private String getApiUrl(String ourAuthor) {
        //return API_BASE_URL + ourISBN +API_END_URL;
        return API_BASE_URL + ourAuthor +API_END_URL;
    }

    // Method for accessing the search API
    public void getAuthor(final String anAuthor, JsonHttpResponseHandler handler) {
        String url = getApiUrl(anAuthor);
        client.get(url, handler);
    }
}
