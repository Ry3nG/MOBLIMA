package main.entity;
import java.util.ArrayList;

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

    @Override
    public String toString() {
        return "Cineplex{" + "location=" + location + ", cinemas=" + cinemas + '}';
    }

    public static void main(String[] args) {

    }
}
