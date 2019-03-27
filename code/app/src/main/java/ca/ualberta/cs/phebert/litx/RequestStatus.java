package ca.ualberta.cs.phebert.litx;

import android.util.Log;

import java.util.Hashtable;

//static import Request.TAG;

public enum RequestStatus {
    Pending {
        @Override
        public boolean deletable() {
            return false;
        }

        @Override
        public RequestStatus accept(Book toModify, Request r) {
            toModify.setAcceptedRequest(r);
            if(toModify.getAcceptedRequest().equals(r)) {
                return Accepted;
            }
            Log.d(TAG,"failed to accept request, " +
                    "the given book already has an accepted request");
            return this;
        }

        @Override
        public RequestStatus refuse(Book toModify, Request r) {
            // TODO: remove from toModify's requests
            return Refused;
        }
    },
    Accepted {
        @Override
        public boolean deletable() {
            return false;
        }

        @Override
        public RequestStatus resolve(Book toModify, Request request) {
            if(toModify.getAcceptedRequest().equals(request)) {
                toModify.setAcceptedRequest(null);
                // TODO remove from toModify's requests
                return Resolved;
            }
            Log.e(TAG,"request was marked as accepted yet was not the accepted request.");
            return this;
        }
    },
    Resolved,
    Refused;

    private static final String TAG = "LitX.Request";

    private String getArticle() {
        switch(toString().charAt(0)) {
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

    public boolean deletable() { return true; }

    public RequestStatus accept(Book b, Request r) {
        Log.w(TAG, "cannot accept {A} {NAME} request"
                .replace("{NAME}", toString())
                .replace("{A}",getArticle()));
        return this;
    }

    public RequestStatus refuse(Book b, Request r) {
        Log.w(TAG, "cannot refuse {A} {NAME} request"
                .replace("{NAME}", toString())
                .replace("{A}", getArticle()));
        return this;
    }

    public RequestStatus resolve(Book b, Request request) {
        Log.w(TAG, "cannot resolve a {NAME} request".replace("{NAME}", toString()));
        return this;
    }
}
