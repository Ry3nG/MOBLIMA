package main.control;

import main.entity.Booking;

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
    //global booking object, and totalprice
    private Booking currentBooking;
    private double totalPrice;

    //singleton design pattern
    private static BookingHandler instance = null;
    private BookingHandler() {}
    public static BookingHandler getInstance() {
        if (instance == null) {
            instance = new BookingHandler();
        }
        return instance;
    }

    /**
     * Startbooking, takes in the customer's name, mobile, email, and the showID requested, create a booking class.
     * 
     * @param name customer's name
     * @param mobile customer's mobile
     * @param email customer's email
     * @param showId showID requested
     * @return booking class
     */

    public Booking startBooking(String name, String mobile, String email, String showId) {

        Booking booking = new Booking(showId, name, mobile, email);
        this.currentBooking = booking;
        return booking;
    }

    /**
     * Add ticket to booking, takes in the seatID and ticket type, add the ticket to the booking class.
     * 
     * @param seatId seatID requested
     * @param type ticket type
     * @return booking class
     */
    public Booking addTicket(String seatId, String type) {
        this.currentBooking.addTicket(seatId, type);
        return this.currentBooking;
    }

    /**
     * Calculate total price
     * @param booking booking class
     * @return total price
     */
    public double calculateTotalPrice(Booking booking) {
        this.totalPrice = booking.calculateTotalPrice();
        return totalPrice;
    }

    /**
     * Make payment, takes in the payment method, and the total price, and make payment.
     * 
     * @param paymentMethod payment method
     * @param totalPrice total price
     * @return true: payment will always be successful in this project
     */
    public boolean makePayment(String paymentMethod, double totalPrice) {
        //payment will always be successful
        return true;
    }

    /**
     * View booking history, takes in the customer's email and phone number, and view the booking history.
     * 
     * @param email customer's email
     * @param mobile customer's mobile
     * @return booking history
     */
    


}
