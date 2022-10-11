package main.entity;
import java.util.ArrayList;

/**
 * Represents a cineplex in MOBLIMA.
 * @author SS11 Group 1
 * @version 1.0
 * @since 2022/10/11
 * @see Cinema
 * @see Movie
 * @see Showtime
 * @see Seat
    */

public class Cineplex {
    //each cineplex has a location and a list of cinemas
    private String location;
    private ArrayList<Cinema> cinemas;

    public Cineplex(String location, ArrayList<Cinema> cinemas) {
        this.location = location;
        this.cinemas = cinemas;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<Cinema> getCinemas() {
        return cinemas;
    }

    public void setCinemas(ArrayList<Cinema> cinemas) {
        this.cinemas = cinemas;
    }
}
