package main;
import main.boundary.*;
import java.util.Scanner;
public class MoblimaApp extends Menu {
    // Console-based Movie Booking and LIsting Management Application (MOBLIMA)
    public static void main(String[] args) {
        MoblimaApp app = new MoblimaApp();
        app.showMenu();
    }

    public void showMenu() {
        System.out.println("Welcome to MOBLIMA!");
        System.out.println("1. I'm a Movie-goer");
        System.out.println("2. I'm a Staff");
        System.out.println("3. Exit");
        System.out.print("Please enter your choice: ");
        int choice = 0;
        try (Scanner sc = new Scanner(System.in)) {
            choice = sc.nextInt();
        }
        switch (choice) {
            case 1:
                System.out.println("You have chosen to login as a movie-goer");
                //switch to display movie-goer menu
                MovieGoerMenu movieGoerMenu = new MovieGoerMenu();
                movieGoerMenu.showMenu();
                break;
            case 2:
                System.out.println("You have chosen to login as a staff");
                //switch to display staff menu
                StaffMenu staffMenu = new StaffMenu();
                staffMenu.showMenu();
                break;
            case 3:
                System.out.println("Thank you for using MOBLIMA!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice!");
                break;
        }

    }
}