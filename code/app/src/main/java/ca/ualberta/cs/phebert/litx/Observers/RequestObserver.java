package ca.ualberta.cs.phebert.litx.Observers;

import ca.ualberta.cs.phebert.litx.Request;

public interface RequestObserver {
    void onUpdate(Request request);
}
