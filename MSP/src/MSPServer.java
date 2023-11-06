import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MSPServer {
    private static final int PUERTO = 18;
    private static Map<String, PrintWriter> clientesConectados = new HashMap<>();

    public static void main(String[] args) {
        try {
            ServerSocket servidorSocket = new ServerSocket(PUERTO);
            System.out.println("MSP Server is running on port " + PUERTO);

            while (true) {
                Socket clienteSocket = servidorSocket.accept();
                new Thread(new ManejadorCliente(clienteSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ManejadorCliente implements Runnable {
        private Socket clienteSocket;
        private PrintWriter out;
        private BufferedReader in;
        private String nombreUsuario;

        public ManejadorCliente(Socket socket) {
            clienteSocket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(clienteSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));

                String lineaEntrada;
                while ((lineaEntrada = in.readLine()) != null) {
                    if (lineaEntrada.startsWith("CONNECT")) {
                        // Analizar nombre de usuario desde el comando CONNECT
                        nombreUsuario = lineaEntrada.substring("CONNECT ".length());
                        clientesConectados.put(nombreUsuario, out);
                        System.out.println(nombreUsuario + " se ha conectado.");
                    } else if (lineaEntrada.startsWith("DISCONNECT")) {
                        // Desconectar al cliente y eliminarlo de clientesConectados
                        clientesConectados.remove(nombreUsuario);
                        System.out.println(nombreUsuario + " se ha desconectado.");
                        break;
                    } else if (lineaEntrada.equals("LIST")) {
                        // Enviar la lista de usuarios conectados
                        List<String> usuariosConectados = new ArrayList<>(clientesConectados.keySet());
                        out.println("Connected Users: " + String.join(", ", usuariosConectados));
                    } else if (lineaEntrada.startsWith("SEND")) {
                        // Analizar el mensaje y el destinatario desde el comando SEND
                        String[] partes = lineaEntrada.split("@");
                        if (partes.length == 2) {
                            String destinatario = partes[1];
                            String mensaje = partes[0].substring("SEND ".length());
                            PrintWriter escritorDestinatario = clientesConectados.get(destinatario);
                            if (escritorDestinatario != null) {
                                escritorDestinatario.println(nombreUsuario + ": " + mensaje);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (nombreUsuario != null) {
                    clientesConectados.remove(nombreUsuario);
                }
                try {
                    clienteSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}