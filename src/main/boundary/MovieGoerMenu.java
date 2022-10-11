package main.boundary;
import java.util.Scanner;
/**
 * Movie Goer's Menu
 */
public class MovieGoerMenu extends Menu{

     public void showMenu(){
            System.out.println("Welcome to MOBLIMA!");
            System.out.println("1. Search/List movie");
            System.out.println("2. View movie details – including reviews and ratings");
            System.out.println("3. Check seat availability and selection of seat/s.");
            System.out.println("4. Book and purchase ticket");
            System.out.println("5. View booking history");
            System.out.println("6. List the Top 5 ranking by ticket sales OR by overall reviewers’ ratings");
            System.out.println("7. Return to previous menu");
            System.out.print("Please enter your choice: ");
            int choice = 0;
            Scanner sc = new Scanner(System.in);
            choice = sc.nextInt();
            switch(choice){
                case 1:
                    System.out.println("You have chosen to search/list movie");
                    //TODO place holder for search/list movie
                    break;
                case 2:
                    System.out.println("You have chosen to view movie details");
                    //TODO place holder for view movie details
                    break;
                case 3:
                    System.out.println("You have chosen to check seat availability and selection of seat/s");
                    //TODO place holder for check seat availability and selection of seat/s
                    break;
                case 4:
                    System.out.println("You have chosen to book and purchase ticket");
                    //TODO place holder for book and purchase ticket
                    break;
                case 5:
                    System.out.println("You have chosen to view booking history");
                    //TODO place holder for view booking history
                    break;
                case 6:
                    System.out.println("You have chosen to list the Top 5 ranking by ticket sales OR by overall reviewers’ ratings");
                    //TODO place holder for list the Top 5 ranking by ticket sales OR by overall reviewers’ ratings
                    break;
                case 7:
                    System.out.println("You have chosen to return to previous menu");
                    //TODO place holder for return to previous menu
                    break;
                default:
                    System.out.println("Invalid choice!");
                    break;
            }

     }

}
