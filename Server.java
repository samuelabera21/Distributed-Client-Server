import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import utils.DBConnection;

public class Server {
    static final int PORT = 5000;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT + "...");

            while (true) {
                try (Socket socket = serverSocket.accept();
                     BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter output = new PrintWriter(socket.getOutputStream(), true)) {

                    System.out.println("Client connected: " + socket.getInetAddress());
                    String request = input.readLine();

                    if (request == null || request.trim().isEmpty()) {
                        output.println("Error: Empty request");
                        output.println("END");
                        continue;
                    }

                    if (request.startsWith("REGISTER,")) {
                        registerStudent(request, output);
                    } else if (request.startsWith("FILTER,")) {
                        filterStudents(request, output);
                    } else {
                        output.println("Error: Unknown request type");
                    }

                    output.println("END");
                } catch (Exception clientError) {
                    System.out.println("Client handling error: " + clientError.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void registerStudent(String request, PrintWriter output) {
        String[] data = request.split(",", -1);
        if (data.length != 6) {
            output.println("Error: REGISTER format must be REGISTER,id,name,email,gender,age");
            return;
        }

        try {
            int id = Integer.parseInt(data[1].trim());
            String name = data[2].trim();
            String email = data[3].trim();
            String gender = data[4].trim();
            int age = Integer.parseInt(data[5].trim());

                try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "INSERT INTO students (id, name, email, gender, age) VALUES (?, ?, ?, ?, ?)")) {

                ps.setInt(1, id);
                ps.setString(2, name);
                ps.setString(3, email);
                ps.setString(4, gender);
                ps.setInt(5, age);

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    output.println("Student Registered");
                } else {
                    output.println("Error: Insert failed");
                }
            }
        } catch (NumberFormatException nfe) {
            output.println("Error: id and age must be numbers");
        } catch (Exception e) {
            output.println("Error: " + e.getMessage());
        }
    }

    static void filterStudents(String request, PrintWriter output) {
        String[] parts = request.split(",", 3);
        if (parts.length != 3) {
            output.println("Error: FILTER format must be FILTER,field,value");
            output.println("Use field: id OR age OR gender");
            return;
        }

        String field = parts[1].trim().toLowerCase();
        String value = parts[2].trim();

        String sql;
        switch (field) {
            case "id":
                sql = "SELECT id, name, email, gender, age FROM students WHERE id = ?";
                break;
            case "age":
                sql = "SELECT id, name, email, gender, age FROM students WHERE age = ?";
                break;
            case "gender":
                sql = "SELECT id, name, email, gender, age FROM students WHERE gender = ?";
                break;
            default:
                output.println("Error: Unsupported field. Use id, age, or gender");
                return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if ("id".equals(field) || "age".equals(field)) {
                ps.setInt(1, Integer.parseInt(value));
            } else {
                ps.setString(1, value);
            }

            try (ResultSet rs = ps.executeQuery()) {
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    output.println(
                            rs.getInt("id") + " | " +
                            rs.getString("name") + " | " +
                            rs.getString("email") + " | " +
                            rs.getString("gender") + " | " +
                            rs.getInt("age")
                    );
                }
                if (!found) {
                    output.println("No students found");
                }
            }
        } catch (NumberFormatException nfe) {
            output.println("Error: id/age filter value must be a number");
        } catch (Exception e) {
            output.println("Error: " + e.getMessage());
        }
    }
}
