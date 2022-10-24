import boundary.CustomerMenu;
import entity.Menu;
import utils.Constants;

public class App {
  private static App instance = null;
  private Menu currentMenu;

  private App(){

  }

  public static App getInstance() {
    if (instance == null) instance = new App();
    return instance;
  }

  public static void main(String[] args) {
    instance = App.getInstance();

//    Constants.setDebugMode(true);
//    if (args.length == 0) instance.currentMenu = CustomerMenu.getInstance();
//    else if (args[0].equals("--staff")) instance.currentMenu = new StaffMenu();

    int lastArgIdx = args.length - 1;
    if(lastArgIdx >= 0){
      if (args[lastArgIdx].equals("--debug")) Constants.setDebugMode(true);

      if(lastArgIdx == 0){
        instance.currentMenu = CustomerMenu.getInstance();
      }

//      if (args[lastArgIdx - 1].equals("--staff")) instance.currentMenu = new StaffMenu();
    }
    else{
      instance.currentMenu = CustomerMenu.getInstance();
    }

    instance.currentMenu.showMenu();
  }
}
