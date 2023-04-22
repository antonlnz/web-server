package es.udc.redes.webserver;

import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {
    
    public static void main(String[] args) {
        // Set the port number to listen on
        int serverPort = Integer.parseInt(args[0]);
        // Create a new ServerSocket
        ServerSocket serverSocket = null;

        try {
            // Initialize ServerSocket
            serverSocket = new ServerSocket(serverPort);

            // Print a message indicating the server is running
            System.out.println("Server is listening on port " + serverPort);

            // Loop indefinitely to handle incoming connections
            while (true) {
                // Wait for a client connection
                Socket clientSocket = serverSocket.accept();

                // Create a new ServerThread to handle the connection
                ServerThread thread = new ServerThread(clientSocket);

                // Start the thread
                thread.start();
            }

        } catch (Exception e) {
            // Print an error message if something goes wrong
            System.err.println("Error: " + e.getMessage());
        }
    }
    
}
