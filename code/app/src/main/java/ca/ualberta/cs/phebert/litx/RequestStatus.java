package ca.ualberta.cs.phebert.litx;

import android.util.Log;

import java.util.Hashtable;

//static import Request.TAG;

public enum RequestStatus {
    Pending("pending") {
        @Override
        public RequestStatus accept(Book toModify) {
            return Accepted;
        }

        @Override
        public RequestStatus refuse(Book toModify) {
            return Refused;
        }
    },
    Accepted("accepted") {
        @Override
        public RequestStatus resolve(Book toModify) {
            return Resolved;
        }
    },
    Resolved("resolved"),
    Refused("refused");

    private static final String TAG = "LitX.Request";

    static Hashtable<String, RequestStatus> table = new Hashtable<>();
    String name;

    RequestStatus(String s) {
        name = s;
    }

    String getName() {
        return name;
    }

    static RequestStatus get(String s) {
        return table.get(s);
    }

    private String getArticle() {
        switch(name.charAt(0)) {
            case 'a':
            case 'e':
            case 'i':
            case 'o':
            case 'u':
                return "an";
            default:
                return "a";
        }
    }

    public RequestStatus accept(Book b) {
        Log.w(TAG, "cannot accept {A} {NAME} request"
                .replace("{NAME}", getName())
                .replace("{A}",getArticle()));
        return this;
    }

    public RequestStatus refuse(Book b) {
        Log.w(TAG, "cannot refuse {A} {NAME} request"
                .replace("{NAME}", getName())
                .replace("{A}", getArticle()));
        return this;
    }

    public RequestStatus resolve(Book b) {
        Log.w(TAG, "cannot resolve a {NAME} request".replace("{NAME}", getName()));
        return this;
    }
}
