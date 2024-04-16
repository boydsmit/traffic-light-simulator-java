package traffic;

import java.io.IOException;
import java.util.*;

public class Main {
  private static QueueThread queueThread;


  public static void main(String[] args){

    System.out.println("Welcome to the traffic management system.");
    int roads = getInput("Input the number of roads: > ");
    int interval = getInput("Input the interval: > ");
    queueThread = new QueueThread(roads, interval);
    getOption();
  }

  private static void printOptions() {
    String[] options = new String[] {"Add road", "Delete road", "Open system", "Quit"};
    System.out.println("Menu:");
    for (int i = 0; i < options.length; i++) {
      int index = i == (options.length - 1) ? 0 : i + 1;
      System.out.println(index + ". " + options[i]);
    }
  }

  public static void clearConsole() {
    try {
      var clearCommand = System.getProperty("os.name").contains("Windows")
              ? new ProcessBuilder("cmd", "/c", "cls")
              : new ProcessBuilder("clear");
      clearCommand.inheritIO().start().waitFor();
    }
    catch (IOException | InterruptedException e) {}
  }

  private static int getInput(String inputDescriptor) {
    Scanner scanner = new Scanner(System.in);
    System.out.print(inputDescriptor);
    try {
      int ans =  scanner.nextInt();
      if (ans > 0 ) {
        return ans;
      }
      throw new InputMismatchException();
    } catch (InputMismatchException exception) {
      return getInput("Incorrect Input! Try again: ");
    }
  }

  private static void waitForInput() {
    Scanner scanner = new Scanner(System.in);
    scanner.nextLine();
    clearConsole();
    queueThread.setState(QueueThread.state.MENU);
    getOption();
  }

  private static void getOption() {
    printOptions();
    Scanner scanner = new Scanner(System.in);
    try {
       int input = scanner.nextInt();
      switch (input) {
        case 1:
          queueThread.addRoad();
          waitForInput();
          break;
        case 2:
          queueThread.deleteRoad();
          waitForInput();
          break;
        case 3:
          queueThread.setState(QueueThread.state.SYSTEM);
          waitForInput();
          break;
        case 0:
          queueThread.kill();
          System.out.println("Bye!");
          break;
        default:
          throw new InputMismatchException();
      }
    } catch (InputMismatchException exception) {

      System.out.println("Incorrect Option");
      waitForInput();
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
  }

}
