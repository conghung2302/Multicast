package serverlearn;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDate;

public class C3Server {
    private static final int SERVER_PORT = 9876;

    public static void main(String[] args) throws IOException {
        DatagramSocket serverSocket = new DatagramSocket(SERVER_PORT);
        System.out.println("Server is running and waiting for login...");

        byte[] receiveData = new byte[1024];

        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);

            String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("Received: " + message);

            if (message.equals("login")) {
                System.out.println("Client login request received.");
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                // Start new socket and threads to handle this client
                ClientHandler handler = new ClientHandler(clientAddress, clientPort);
                new Thread(handler).start();
            }
        }
    }
}

class ClientHandler implements Runnable {
    private final InetAddress clientAddress;
    private final int clientPort;
    private volatile boolean running = true;
    private volatile boolean paused = false;

    public ClientHandler(InetAddress clientAddress, int clientPort) {
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket()) {
            Thread senderThread = new Thread(new DataSender(socket, clientAddress, clientPort));
            Thread controlThread = new Thread(new ControlReceiver(socket, this));

            senderThread.start();
            controlThread.start();

            senderThread.join(); // Wait until senderThread finishes
            controlThread.join(); // Wait until controlThread finishes

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean isRunning() {
        return running;
    }

    public synchronized void stop() {
        running = false;
    }

    public synchronized boolean isPaused() {
        return paused;
    }

    public synchronized void pause() {
        paused = true;
    }

    public synchronized void resume() {
        paused = false;
    }
}

class DataSender implements Runnable {
    private final DatagramSocket socket;
    private final InetAddress clientAddress;
    private final int clientPort;

    public DataSender(DatagramSocket socket, InetAddress clientAddress, int clientPort) {
        this.socket = socket;
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
    }

    @Override
    public void run() {
        try {
            int counter = 0;
            while (true) {
                synchronized (this) {
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                }

                String message = "Data " + counter;
                byte[] sendData = message.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);

                socket.send(sendPacket);
                System.out.println("Sent: " + LocalDate.now());
                counter++;

                Thread.sleep(2000); // Simulate delay in sending data
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ControlReceiver implements Runnable {
    private final DatagramSocket socket;
    private final ClientHandler clientHandler;

    public ControlReceiver(DatagramSocket socket, ClientHandler clientHandler) {
        this.socket = socket;
        this.clientHandler = clientHandler;
    }

    @Override
    public void run() {
        byte[] receiveData = new byte[1024];

        try {
            while (clientHandler.isRunning()) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);

                String command = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Received control signal: " + command);

                if (command.equals("logout")) {
                    clientHandler.stop();
                    System.out.println("Client logged out.");
                    break;
                } else if (command.equals("pause")) {
                    clientHandler.pause();
                    System.out.println("Client paused data sending.");
                } else if (command.equals("resume")) {
                    clientHandler.resume();
                    System.out.println("Client resumed data sending.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
