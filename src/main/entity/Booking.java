package main.entity;

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
    private List<Ticket> tickets;
    private String transactionId;
    private String name;
    private String mobile;
    private String email;

    public Booking(String showId, String name, String mobile, String email) {
        this.showId = showId;
        this.name = name;
        this.mobile = mobile;
        this.email = email;
    }

    //add ticket to booking
    public void addTicket(String seatId, String type) {
        Ticket ticket = new Ticket(TicketType.valueOf(type), seatId);
        this.tickets.add(ticket);
    }

    //calculate total price
    public double calculateTotalPrice() {
        double totalPrice = 0;
        for (Ticket ticket : tickets) {
            totalPrice += ticket.getType().getPrice();
        }
        return totalPrice;
    }
    //generate transaction id
    public void generateTransactionId() {
        //TODO
        //The TID is of the format XXXYYYYMMDDhhmm (Y : year, M : month, D : day, h : hour, m : minutes, XXX : cinema code in letters).
    }
    

}
