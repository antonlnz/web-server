package es.udc.redes.tutorial.tcp.server;

import es.udc.redes.tutorial.copy.Copy;

import java.net.*;
import java.io.*;

/**
 * MonoThread TCP echo server.
 */
public class MonoThreadTcpServer {

    public static void main(String argv[]) throws IOException {
        if (argv.length != 1) {
            System.err.println("Format: es.udc.redes.tutorial.tcp.server.MonoThreadTcpServer <port>");
            System.exit(-1);
        }
        ServerSocket serverSocket = new ServerSocket();
        try {
            // Create a server socket
            int serverPort = Integer.parseInt(argv[0]);
            serverSocket = new ServerSocket(serverPort);
            // Set a timeout of 300 secs
            serverSocket.setSoTimeout(300000);
            while (true) {
                // Wait for connections
                Socket socketToComunicateWithClient = serverSocket.accept();

                // Set the input channel
                InputStream inputStream = socketToComunicateWithClient.getInputStream();

                // Set the output channel
                OutputStream outputStream = socketToComunicateWithClient.getOutputStream();

                // Receive the client message
                BufferedReader sInput = new BufferedReader(new InputStreamReader(inputStream));
                String message = sInput.readLine();

                System.out.println("SERVER: Received "
                        + new String(message)
                        + " from " + socketToComunicateWithClient.getInetAddress().toString() + ":"
                        + socketToComunicateWithClient.getPort());

                // Send response to the client
                PrintWriter sOutput = new PrintWriter(socketToComunicateWithClient.getOutputStream(), true);

                System.out.println("SERVER: Sended "
                        + new String(message)
                        + " to " + socketToComunicateWithClient.getInetAddress().toString() + ":"
                        + socketToComunicateWithClient.getPort());
                sOutput.println(message);

                // Close the streams
                inputStream.close();
                outputStream.close();
                socketToComunicateWithClient.close();

            }

        } catch (SocketTimeoutException e) {
            System.err.println("Nothing received in 300 secs ");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
	        serverSocket.close();
        }
    }
}
