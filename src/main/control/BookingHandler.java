package main.control;

import java.util.ArrayList;

import main.entity.Booking;
import main.entity.Ticket;
import main.entity.TicketType;
import tmdb.datasource.Datasource;

/**
 * Control class for customer booking, receive booking information from UI
 * 
 * @author Gong Zerui, SS11 Group 1
 * @version 1.0
 * @see Booking
 * @see Ticket
 * @see Showtime
 * @see Seat
 * @since 2022/10/20
 */
public class BookingHandler {
    // global booking object, and totalprice
    private ArrayList <Booking> bookings = new ArrayList <Booking>();
    private double totalPrice;

    // singleton design pattern
    private static BookingHandler instance = null;

    private BookingHandler() {
    }

    public static BookingHandler getInstance() {
        if (instance == null) {
            instance = new BookingHandler();
        }
        return instance;
    }

    // startbooking method
    public void startBooking(String showId, String name, String mobile, String email) {
        Booking booking = new Booking(showId, name, mobile, email);
        bookings.add(booking);
    }

    /**
     * addTicket method
     * 
     * @param String seatId
     * @param String ticketType
     * @return true if add successfully, false if not
     */
    public boolean addTicket(String seatId, TicketType ticketType) {
        // check if seat is available
        //if (ShowHandler.getInstance().checkSeatAvailability(seatId)) { // !ask teamate
        if(bookings.size() != 0){    
            // add ticket to booking
            Ticket ticket = new Ticket(ticketType, seatId);
            bookings.get(bookings.size() - 1).addTicket(ticket);
            // update total price
            totalPrice += ticketType.getPrice();
            // set seat to unavailable
            //ShowHandler.getInstance().setSeatAvailability(seatId, false); // !ask teamate
            return true;
        } else {
            return false;
        }
    }

    /**
     * completeBooking method: store booking to database
     * 
     * @return true if complete successfully, false if not
     */
    public boolean completeBooking() {
        // check if booking is empty
        if (bookings.size() == 0) {
            return false;
        }
        // generate transaction id
        // The TID is of the format XXXYYYYMMDDhhmm
        // (Y : year, M : month, D : day, h : hour, m : minutes, XXX : cinema code)
        String transactionId = "TID"
                + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        bookings.get(bookings.size() - 1).setTransactionId(transactionId);
        // store current booking to booking.csv
        return Datasource.serializeData(this.bookings, "bookings.csv");
    }

    /**
     * view booking method: check the user's booking history from bookings.csv
     * @param String email
     * @param String mobile
     * @return List<Booking> if view successfully, null if not
     */
    //! todo

    public static void main(String[] args) {
        //test code
        BookingHandler bh = BookingHandler.getInstance();
        bh.startBooking("testshowID", "Ryan", "89427828", "nomlasssssss@outlook.com");
        bh.addTicket("testSeatID1", TicketType.STANDARD);
        bh.addTicket("testSeatID2", TicketType.STUDENT);
        bh.addTicket("testSeatID3", TicketType.SENIOR);
        bh.completeBooking();

        bh.startBooking("testshowID2", "Ryan2", "89427828", "nomlasssssss@outlook.com");
        bh.addTicket("testSeatID1-2", TicketType.STANDARD);
        bh.addTicket("testSeatID2-2", TicketType.STUDENT);
        bh.addTicket("testSeatID3-2", TicketType.SENIOR);
        bh.completeBooking();
    }
}
