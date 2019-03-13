package ca.ualberta.cs.phebert.litx;

public class MapObject {
    private Coordinate location; // Not sure how this is going to be implemented.
                                // But this should be provided by the constructor so the map is
                                // of this location
    private Request request;

    public Coordinate getLocation() {
        return location;
    }

    /*
     * Not sure these getters and setters for Location are necessary if we want to be able to
     * use this setLocation function other than in the constructor we will need to change the entire
     * map so that when we view it we are viewing the correct location
     */
    public void setLocation(Coordinate location) {

    }

    public void viewMap() {

    }
}
