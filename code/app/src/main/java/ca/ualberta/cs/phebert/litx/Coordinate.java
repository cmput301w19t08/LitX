package ca.ualberta.cs.phebert.litx;

public class Coordinate {
    private double x;
    private double y;

    /*
     * Assumes format of google map location
     */
    public Coordinate (double x, double y) {
        this.x = x;
        this.y = y;
    }
}
