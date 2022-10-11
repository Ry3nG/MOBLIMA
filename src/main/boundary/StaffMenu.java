package main.boundary;
import java.util.Scanner;
/**
 * Staff's Menu
 */
public class StaffMenu extends Menu {
    public void showMenu(){
        System.out.println("Welcome to MOBLIMA!()");
        System.out.println("1. Login");
        System.out.println("2. Create/Update/Remove movie listing");
        System.out.println("3. Create/Update/Remove cinema showtimes and the movies to be shown");
        System.out.println("4. Configure system settings");
        System.out.println("5. Return to previous menu");
        System.out.print("Please enter your choice: ");
        int choice = 0;
        Scanner sc = new Scanner(System.in);
        choice = sc.nextInt();
        switch(choice){
            case 1:
                System.out.println("You have chosen to login");
                //TODO place holder for login
                break;
            case 2:
                System.out.println("You have chosen to create/update/remove movie listing");
                //TODO place holder for create/update/remove movie listing
                break;
            case 3:
                System.out.println("You have chosen to create/update/remove cinema showtimes and the movies to be shown");
                //TODO place holder for create/update/remove cinema showtimes and the movies to be shown
                break;
            case 4:
                System.out.println("You have chosen to configure system settings");
                //TODO place holder for configure system settings
                break;
            case 5:
                System.out.println("You have chosen to return to previous menu");
                //TODO place holder for return to previous menu
                break;
            default:
                System.out.println("Invalid choice!");
                break;
        }
    }
}
