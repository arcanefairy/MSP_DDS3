import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class MSPCliente {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 18);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            Scanner scanner = new Scanner(System.in);
            System.out.print("Ingresa tu nombre de usuario: ");
            String nombreUsuario = scanner.nextLine();
            out.println("CONNECT " + nombreUsuario);

            while (true) {
                System.out.println("Enter a command (SEND, LIST, DISCONNECT): ");
                String comando = scanner.nextLine();

                if (comando.equals("DISCONNECT")) {
                    out.println("DISCONNECT");
                    break;
                } else if (comando.equals("LIST")) {
                    out.println("LIST");
                    String listaUsuarios = in.readLine();
                    System.out.println("Connected Users: " + listaUsuarios);
                } else if (comando.equals("SEND")) {
                    System.out.print("Enter recipient's username: ");
                    String destinatario = scanner.nextLine();
                    System.out.print("Enter message: ");
                    String mensaje = scanner.nextLine();
                    out.println("SEND " + mensaje + "@" + destinatario);
                }
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}