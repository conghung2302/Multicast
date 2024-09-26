package Multicast.Receiver;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Observable;
import java.util.Observer;

public class MulticastManager extends Observable{

    public Thread receiver;
    public String multicastAddress = "230.0.0.1";
    public int port = 12345;
    MulticastSocket socket;
    InetAddress group;

    public MulticastManager(Observer obs) {
        this.addObserver(obs);
        try {
            socket = new MulticastSocket(port);
            group = InetAddress.getByName(multicastAddress);
            socket.joinGroup(group);
        } catch (Exception e) {
        }

        StartThreadReceiver();

    }

    @Override
    public void notifyObservers(Object arg) {
        setChanged();
        super.notifyObservers(arg); 
    }
    
    
    
    public void SendData(String text) {
                // Dữ liệu muốn gửi
//        String message = "Hello Multicast Group!";
        byte[] buffer = text.getBytes();

        // Tạo DatagramPacket chứa dữ liệu và thông tin địa chỉ/cổng
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);

        // Gửi gói tin tới nhóm multicast
        try {
            socket.send(packet);
            System.out.println("Message sent to multicast group: " + text);
            
        } catch (Exception e) {
        }
        
    }

    public void StartThreadReceiver() {
        byte[] buffer = new byte[1024];
        receiver = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    System.out.println("Waiting for multicast message...");
                    socket.receive(packet);
                    String receivedMessage = new String(packet.getData(), 0, packet.getLength());
                    notifyObservers(receivedMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                }
            }
        });
        receiver.start();
    }

    public static void main(String[] args) throws Exception {
        
//        new MulticastManager();
        // Địa chỉ multicast và cổng
        String multicastAddress = "230.0.0.1";  // Địa chỉ IP multicast
        int port = 1234;  // Cổng để nhận dữ liệu

        // Tạo MulticastSocket để nhận dữ liệu từ nhóm multicast
        MulticastSocket socket = new MulticastSocket(port);
        InetAddress group = InetAddress.getByName(multicastAddress);

        // Tham gia vào nhóm multicast
        socket.joinGroup(group);

        // Buffer để nhận dữ liệu
        byte[] buffer = new byte[1024];

        // Chờ và nhận gói tin từ nhóm multicast
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        System.out.println("Waiting for multicast message...");
        socket.receive(packet);

        // Hiển thị nội dung của gói tin nhận được
        String receivedMessage = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Received message from multicast group: " + receivedMessage);

        // Rời khỏi nhóm multicast và đóng socket
        socket.leaveGroup(group);
        socket.close();
    }
}
