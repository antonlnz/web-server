package es.udc.redes.tutorial.udp.server;

import java.net.*;

/**
 * Implements a UDP echo server.
 */
public class UdpServer {

    public static void main(String argv[]) {
        if (argv.length != 1) {
            System.err.println("Format: es.udc.redes.tutorial.udp.server.UdpServer <port_number>");
            System.exit(-1);
        }
        DatagramSocket datagramSocket = null;
        try {
            // Create a server socket
            int serverPort = Integer.parseInt(argv[0]);
            datagramSocket = new DatagramSocket(serverPort);
            // Set maximum timeout to 300 secs
            datagramSocket.setSoTimeout(300000);
            while (true) {
                // Prepare datagram for reception
                byte array[] = new byte[1024];
                DatagramPacket datagramPacket = new DatagramPacket(array, array.length);
                // Receive the message
                datagramSocket.receive(datagramPacket);
                System.out.println("SERVER: Received "
                        + new String(datagramPacket.getData(), 0, datagramPacket.getLength())
                        + " from " + datagramPacket.getAddress().toString() + ":"
                        + datagramPacket.getPort());
                // Prepare datagram to send response
                DatagramPacket response = new DatagramPacket(array, datagramPacket.getLength(), datagramPacket.getAddress(), datagramPacket.getPort());

                // Send response
                System.out.println("SERVER: Sendded "
                        + new String(response.getData(), 0, response.getLength())
                        + " to " + response.getAddress().toString() + ":"
                        + response.getPort());
                datagramSocket.send(response);
            }

        } catch (SocketTimeoutException e) {
            System.err.println("No requests received in 300 secs ");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            datagramSocket.close();
        }
    }
}
