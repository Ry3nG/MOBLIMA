package main.entity;

/**
 * The cinema class represents a cinema in a perticular cineplex.
 * @author SS11 Group 1
 * @version 1.0
 * @since 2022/10/11
 * @see Cineplex
 * @see Movie
 * @see Showtime
 * @see Seat
 */
public class Cinema {
    private String cinemaCode;
    private String cinemaClass;
    private Showtime[] showtimes;
    private Seat[][] seats;

    public Cinema(String cinemaCode, String cinemaClass, Showtime[] showTimes, Seat[][] seats) {
        this.cinemaCode = cinemaCode;
        this.cinemaClass = cinemaClass;
        this.showtimes = showTimes;
        this.seats = seats;
    }

    public String getCinemaCode() {
        return cinemaCode;
    }

    public void setCinemaCode(String cinemaCode) {
        this.cinemaCode = cinemaCode;
    }

    public String getCinemaClass() {
        return cinemaClass;
    }

    public void setCinemaClass(String cinemaClass) {
        this.cinemaClass = cinemaClass;
    }

    public Showtime[] getShowtimes() {
        return showtimes;
    }

    public void setShowTimes(Showtime[] showTimes) {
        this.showtimes = showTimes;
    }

    public Seat[][] getSeats() {
        return seats;
    }

    public void setSeats(Seat[][] seats) {
        this.seats = seats;
    }
    
}
