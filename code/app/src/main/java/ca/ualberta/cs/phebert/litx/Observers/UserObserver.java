package ca.ualberta.cs.phebert.litx.Observers;

import ca.ualberta.cs.phebert.litx.Models.User;

public interface UserObserver {
    void onUserUpdated(User user);
}
