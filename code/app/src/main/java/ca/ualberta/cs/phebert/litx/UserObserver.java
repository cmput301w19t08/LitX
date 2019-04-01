package ca.ualberta.cs.phebert.litx;

/**
 * Observes a {@link User user} and acts as soon as one is modified
 */
public interface UserObserver {
    /**
     * runs whenever an observed user is modified
     * @param user the user which was modified.
     */
    void onUserUpdated(User user);
}
