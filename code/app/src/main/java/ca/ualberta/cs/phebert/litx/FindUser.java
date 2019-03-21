package ca.ualberta.cs.phebert.litx;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static ca.ualberta.cs.phebert.litx.User.USER_COLLECTION;

public class FindUser {

    private ArrayList<User> userlist;
    private ArrayList<User> results;

    public FindUser(String type, String keyword) {
        userlist = new ArrayList<>();
        // use lazy instantiation.
        FirebaseFirestore.getInstance()
                .collection(USER_COLLECTION)
                .get()
                .addOnSuccessListener(snapshot -> {
                    for(DocumentSnapshot document: snapshot.getDocuments()) {
                        userlist.add(new User(
                                document.getString("userName"),
                                document.getString("email"),
                                document.getString("phoneNumber")
                        ));
                    }
                });
        if(type.contentEquals("username") || type.contentEquals("")) {
            findUserByName(keyword);
        } else if (type.contentEquals("email")) {
            findUserByEmail(keyword);
        } else if (type.contentEquals("phone")) {
            findUserByPhone(keyword);
        }
    }

    public ArrayList<User> getResults(){ return results; }

    protected void findUserByName (String keyword) {
        results = new ArrayList<>();
        for(User user:userlist) {
            if(user.getUserName().startsWith(keyword)) {
                 results.add(user);
            }
        }
    }

    protected void findUserByEmail (String keyword) {
        for(User user:userlist) {
            if(user.getEmail().startsWith(keyword)) {
                results.add(user);
            }
        }
    }

    protected void findUserByPhone (String keyword) {
        for(User user:userlist) {
            if(user.getPhoneNumber().startsWith(keyword)) {
                results.add(user);
            }
        }
    }
}
