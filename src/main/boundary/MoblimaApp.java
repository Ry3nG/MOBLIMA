package main.boundary;
import java.util.Scanner;
public class MoblimaApp extends Menu {
    // Console-based Movie Booking and LIsting Management Application (MOBLIMA)
    public static void main(String[] args) {
        MoblimaApp moblimaApp = new MoblimaApp();
        moblimaApp.showMenu();
    }

    public void showMenu() {
        System.out.println("Welcome to MOBLIMA!");
        System.out.println("1. I'm a Movie-goer");
        System.out.println("2. I'm a Staff");
        System.out.println("3. Exit");
        System.out.print("Please enter your choice: ");
        int choice = 0;
        Scanner sc = new Scanner(System.in);
        choice = sc.nextInt();
        switch (choice) {
            case 1:
                System.out.println("You have chosen to login as a movie-goer");
                //TODO place holder for login as a movie-goer
                break;
            case 2:
                System.out.println("You have chosen to login as a staff");
                //TODO place holder for login as a staff
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