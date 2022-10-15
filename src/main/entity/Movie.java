package main.entity;
import java.util.ArrayList;
/**
 * The movie class
 * @author SS11 Group 1
 * @version 1.0
 * @since 2022/10/11
 * @see Cinema
 * @see Cineplex
 * @see Showtime
 * @see Seat
 */
public class Movie {
    private String title;
    private String synopsis;
    private String director;
    private ArrayList<String> cast;
    private String showStatus;
    private String type;
    private String rating;
    private int duration;
    private ArrayList<Review> reviews;
    private ArrayList<Showtime> showtimes;

    public Movie(String title, String synopsis, String director, ArrayList<String> cast, String showStatus, String type, String rating, int duration, ArrayList<Review> reviews, ArrayList<Showtime> showtimes) {
        this.title = title;
        this.synopsis = synopsis;
        this.director = director;
        this.cast = cast;
        this.showStatus = showStatus;
        this.type = type;
        this.rating = rating;
        this.duration = duration;
        this.reviews = reviews;
        this.showtimes = showtimes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public ArrayList<String> getCast() {
        return cast;
    }

    public void setCast(ArrayList<String> cast) {
        this.cast = cast;
    }

    public String getShowStatus() {
        return showStatus;
    }

    public void setShowStatus(String showStatus) {
        this.showStatus = showStatus;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public ArrayList<Showtime> getShowtimes() {
        return showtimes;
    }

    public void setShowtimes(ArrayList<Showtime> showtimes) {
        this.showtimes = showtimes;
    }
    
}
