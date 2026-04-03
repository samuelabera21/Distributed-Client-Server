import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    static final String HOST = "localhost";
    static final int PORT = 5000;

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.println();
                System.out.println("1. Register student");
                System.out.println("2. Filter students");
                System.out.println("3. Exit");
                System.out.print("Choose option: ");

                int choice;
                try {
                    choice = Integer.parseInt(sc.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Enter 1, 2, or 3.");
                    continue;
                }

                if (choice == 3) {
                    System.out.println("Goodbye");
                    break;
                }

                String request;
                if (choice == 1) {
                    System.out.print("Enter id: ");
                    String id = sc.nextLine().trim();

                    System.out.print("Enter name: ");
                    String name = sc.nextLine().trim();

                    System.out.print("Enter email: ");
                    String email = sc.nextLine().trim();

                    System.out.print("Enter gender (Male/Female): ");
                    String gender = sc.nextLine().trim();

                    System.out.print("Enter age: ");
                    String age = sc.nextLine().trim();

                    request = "REGISTER," + id + "," + name + "," + email + "," + gender + "," + age;
                } else if (choice == 2) {
                    System.out.println("Filter fields: id, age, gender");
                    System.out.print("Enter field: ");
                    String field = sc.nextLine().trim();

                    System.out.print("Enter value: ");
                    String value = sc.nextLine().trim();

                    request = "FILTER," + field + "," + value;
                } else {
                    System.out.println("Invalid choice");
                    continue;
                }

                sendRequest(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendRequest(String request) {
        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Connected to server " + HOST + ":" + PORT);
            output.println(request);

            String response;
            while ((response = input.readLine()) != null) {
                if ("END".equals(response)) {
                    break;
                }
                System.out.println(response);
            }
        } catch (Exception e) {
            System.out.println("Error communicating with server: " + e.getMessage());
        }
    }
}
