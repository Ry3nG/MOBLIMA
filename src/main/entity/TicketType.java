package main.entity;

public enum TicketType {
    STANDARD, STUDENT, SENIOR;

    //get price method
    public double getPrice() {
        switch (this) {
            case STANDARD:
                return 10;
            case STUDENT:
                return 8;
            case SENIOR:
                return 7;
            default:
                return 0;
        }
    }
}
