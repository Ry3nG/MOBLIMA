package main.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Booking class
 * 
 * @author Gong Zerui, SS11 Group 1
 * @version 1.0
 * @see Showtime
 * @see Ticket
 * @since 2022/10/11
 */
public class Booking {
    private String showId;
    private ArrayList <Ticket> tickets;
    private String transactionId;
    private String name;
    private String mobile;
    private String email;

    //constructor
    public Booking(String showId, String name, String mobile, String email) {
        this.showId = showId;
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.tickets = new ArrayList <Ticket>();
    }

    //getters and setters

    public String getShowId() {
        return showId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
    }

    public List<Ticket> getTickets() {
        return this.tickets;
    }

    public void addTicket(Ticket ticket) {
        this.tickets.add(ticket);
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }  

}
