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
