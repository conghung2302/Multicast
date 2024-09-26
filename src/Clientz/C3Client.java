package Clientz;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class C3Client {
    private static final int SERVER_PORT = 9876;
    private static final String SERVER_ADDRESS = "localhost";

    public static void main(String[] args) throws IOException {
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);

        byte[] sendData;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Sending login signal to server...");
        sendData = "login".getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
        clientSocket.send(sendPacket);

        while (true) {
            System.out.println("Enter command (logout, pause, resume):");
            String command = scanner.nextLine();

            if (command.equals("logout")) {
                sendData = "logout".getBytes();
                sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
                clientSocket.send(sendPacket);
                break;
            } else if (command.equals("pause")) {
                sendData = "pause".getBytes();
            } else if (command.equals("resume")) {
                sendData = "resume".getBytes();
            } else {
                System.out.println("Invalid command, try again.");
                continue;
            }

            sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
            clientSocket.send(sendPacket);
        }

        clientSocket.close();
        scanner.close();
    }
}
