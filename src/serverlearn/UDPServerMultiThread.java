package serverlearn;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UDPServerMultiThread {

    private static ArrayList<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(12345);
            System.out.println("running...");

            while (true) {
                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength()).trim();
                String clientAddress = packet.getAddress().getHostAddress();

                int clientPort = packet.getPort();

                if (message.equalsIgnoreCase("login")) {
                    ClientHandler clientHandler = new ClientHandler(clientAddress, clientPort, socket);
                    clients.add(clientHandler);
                    new Thread(clientHandler).start();
                    System.out.println("Client logged in: " + clientAddress + ":" + clientPort);
                } else if (message.equalsIgnoreCase("logout")) {
                    removeClient(clientAddress, clientPort);
                    System.out.println("Client  logout: " + clientAddress + ":" + clientPort);
                } else if (message.equalsIgnoreCase("pause")) {
                    pauseClient(clientAddress, clientPort);
                    System.out.println("Client paused: " + clientAddress + ":" + clientPort);
                } else if (message.equalsIgnoreCase("resume")) {
                    resumeClient(clientAddress, clientPort);
                    System.out.println("Client resumed: " + clientAddress + ":" + clientPort);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }

    private static void removeClient(String clientAddress, int clientPort) {
//        for (ClientHandler client: clients) {
//            if (client.clientAddress.equals(clientAddress)) {
//                clients.remove(client);
//            }
//        }

            clients.removeIf(client -> client.getClientAddress().equals(clientAddress) && client.getClientPort() == clientPort);

    }

    private static void pauseClient(String clientAddress, int clientPort) {
        for (ClientHandler client : clients) {
            if (client.getClientAddress().equals(clientAddress) && client.getClientPort() == clientPort) {
                client.pauseThread();
            }
        }
    }

    private static void resumeClient(String clientAddress, int clientPort) {
        for (ClientHandler client : clients) {
            if (client.getClientAddress().equals(clientAddress) && client.getClientPort() == clientPort) {
                client.resumeThread();
            }
        }
    }
}

class ClientHandler implements Runnable {

    public String clientAddress;
    public int clientPort;
    public DatagramSocket socket;
    public boolean paused;

    public ClientHandler(String clientAddress, int clientPort, DatagramSocket socket) {
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
        this.socket = socket;
        this.paused = false;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public int getClientPort() {
        return clientPort;
    }

    public synchronized void pauseThread() {
        this.paused = true;
    }

    public synchronized void resumeThread() {
        this.paused = false;
        notify();
    }

    @Override
    public void run() {
        try {
            while (true) {
                synchronized (this) {
                    System.out.println("Resume");
                    while (paused) {
                        wait();
                    }
                }

                String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                byte[] buffer = currentTime.getBytes();

                InetAddress address = InetAddress.getByName(clientAddress);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, clientPort);
                socket.send(packet);

                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
