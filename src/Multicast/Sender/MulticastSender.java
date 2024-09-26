package Multicast.Sender;


import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastSender {
    public static void main(String[] args) {
        try {
            // Địa chỉ multicast và cổng
            String multicastAddress = "224.0.0.2"; // Địa chỉ IP multicast
            int port = 1234;  // Cổng để gửi dữ liệu

            // Tạo MulticastSocket để gửi dữ liệu
            MulticastSocket socket = new MulticastSocket();

            // Tạo địa chỉ nhóm multicast
            InetAddress group = InetAddress.getByName(multicastAddress);

            // Chuỗi thông báo cần gửi
            String message = "Hello from Machine A!";
            byte[] buffer = message.getBytes();

            // Tạo DatagramPacket chứa dữ liệu và thông tin địa chỉ/cổng
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);

            // Gửi gói tin tới nhóm multicast
            socket.send(packet);
            System.out.println("Message sent to multicast group: " + message);

            // Đóng socket sau khi gửi
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
