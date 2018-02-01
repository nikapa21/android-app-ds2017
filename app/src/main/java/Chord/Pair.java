package Chord;

/**
 * Created by root on 1/23/18.
 */
public class Pair {

    String origin;
    String destination;

    public Pair(String origin, String destination) {
        this.origin = origin;
        this.destination = destination;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getOrigin() {

        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }
}