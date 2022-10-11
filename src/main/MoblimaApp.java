package main;
import java.util.Scanner;
public class MoblimaApp{
    // Console-based Movie Booking and LIsting Management Application (MOBLIMA)
    public static void main(String[] args) {
        showMenu();
    }

    public static void showMenu() {
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
                System.out.println("You are a Movie-goer");
                //place holder for movie-goer menu

                break;
            case 2:
                System.out.println("You are a Staff");
                //place holder for staff menu
                
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