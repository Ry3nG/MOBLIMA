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

    //constructor
    public Ticket(TicketType type, String seatId) {
        this.type = type;
        this.seatId = seatId;
    }

    //getters and setters
    public TicketType getType() {
        return type;
    }

    public void setType(TicketType type) {
        this.type = type;
    }

    public String getSeatId() {
        return seatId;
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }
}
