package main.entity;
/**
 * Ticket class
 * Type: enum
 * seatId: seat id
 * 
 * @author Gong Zerui, SS11 Group 1
 * @version 1.0
 * @see Seat
 * @since 2022/10/20
 */

public class Ticket {
    TicketType type;
    String seatId;

    public Ticket(TicketType type, String seatId) {
        this.type = type;
        this.seatId = seatId;
    }

    //getType method
    public TicketType getType() {
        return this.type;
    }

    //getPrice method
    public double getPrice() {
        return this.type.getPrice();
    }
}
