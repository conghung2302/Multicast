package Core;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

/**
 *
 * @author hungkiller
 */
public class ServerManager extends Observable {
    public String command = "login";
    public ServerManager(Observer obs) {
        this.addObserver(obs);
        StartThead();
    }

    @Override
    public void notifyObservers(Object obj) {
        setChanged();
        super.notifyObservers(obj);
    }

    private void StartThead() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatagramSocket socket = null;
                try {
                    socket = new DatagramSocket();
                    InetAddress serverAddress = InetAddress.getByName("localhost");
                    int serverPort = 12345;

                    Scanner scanner = new Scanner(System.in);
                    System.out.println("Enter command (login, logout, pause, resume):");

                    while (true) {

                        byte[] buffer = command.getBytes();
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, serverPort);
                        socket.send(packet);
                                
                        if (command.equalsIgnoreCase("logout")) {
                            break;
                        }
                        // Chuẩn bị nhận phản hồi từ server
                        byte[] receiveBuffer = new byte[256];
                        DatagramPacket response = new DatagramPacket(receiveBuffer, receiveBuffer.length);

                        // Nhận gói tin phản hồi từ server
                        socket.receive(response);

                        // Hiển thị dữ liệu nhận được từ server
                        String receivedData = new String(response.getData(), 0, response.getLength());
//                        lbTime.setText(receivedData);
                        notifyObservers(receivedData);

//                        System.out.println("Received from server: " + receivedData);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (socket != null && !socket.isClosed()) {
                        socket.close();
                    }
                }
            }
        }).start();
    }

}
