package ca.ualberta.cs.phebert.litx.Observers;

import ca.ualberta.cs.phebert.litx.User;

public interface UserObserver {
    void onUserUpdated(User user);
}
