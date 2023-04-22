package es.udc.redes.tutorial.tcp.server;
import java.net.*;
import java.io.*;

/** Thread that processes an echo server connection. */

public class ServerThread extends Thread {

  private Socket socket;

  public ServerThread(Socket s) {
    // Store the socket s
    this.socket = s;
  }

  public void run() {
    try {
      // Set the input channel
      InputStream inputStream = socket.getInputStream();

      // Set the output channel
      OutputStream outputStream = socket.getOutputStream();

      // Receive the message from the client
      BufferedReader sInput = new BufferedReader(new InputStreamReader(inputStream));
      String message = sInput.readLine();

      System.out.println("SERVER: Received "
              + new String(message)
              + " from " + socket.getInetAddress().toString() + ":"
              + socket.getPort());

      // Sent the echo message to the client
      PrintWriter sOutput = new PrintWriter(socket.getOutputStream(), true);
      sOutput.println(message);

      System.out.println("SERVER: Sended "
              + new String(message)
              + " to " + socket.getInetAddress().toString() + ":"
              + socket.getPort());

      // Close the streams
      inputStream.close();
      outputStream.close();

    } catch (SocketTimeoutException e) {
      System.err.println("Nothing received in 300 secs");
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
    } finally {
      // Close the socket
      try {
        socket.close();
      } catch (IOException e) {
        System.err.println("Error: " + e.getMessage());
      }
    }
  }
}
