package Multicast.Sender;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastSender {
    public static void main(String[] args) throws Exception {
        // Địa chỉ multicast và cổng
        String multicastAddress = "230.0.01";  // Địa chỉ IP multicast
        int port = 1234;  // Cổng để gửi dữ liệu

        // Tạo MulticastSocket để gửi dữ liệu
        MulticastSocket socket = new MulticastSocket();
        InetAddress group = InetAddress.getByName(multicastAddress);

        // Dữ liệu muốn gửi
        String message = "Hello Multicast Group!";
        byte[] buffer = message.getBytes();

        // Tạo DatagramPacket chứa dữ liệu và thông tin địa chỉ/cổng
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);

        // Gửi gói tin tới nhóm multicast
        socket.send(packet);
        System.out.println("Message sent to multicast group: " + message);

        // Đóng socket
        socket.close();
    }
}
