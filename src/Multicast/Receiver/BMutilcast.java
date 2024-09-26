package Multicast.Receiver;


import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class BMutilcast {
    public static void main(String[] args) {
        try {
            // Địa chỉ multicast và cổng
            String multicastAddress = "230.0.0.1"; // Địa chỉ IP multicast
            int port = 1234;  // Cổng UDP

            // Tạo MulticastSocket để lắng nghe trên cổng 1234
            MulticastSocket socket = new MulticastSocket(port);

            // Tham gia vào nhóm multicast
            InetAddress group = InetAddress.getByName(multicastAddress);
            socket.joinGroup(group);  // Tham gia vào nhóm multicast

            // Buffer để nhận dữ liệu
            byte[] buffer = new byte[1024];

            // Tạo DatagramPacket để nhận dữ liệu
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            System.out.println("Waiting for multicast message...");

            // Nhận gói tin multicast
            socket.receive(packet);

            // Chuyển dữ liệu thành chuỗi và hiển thị
            String receivedMessage = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Received message from multicast group: " + receivedMessage);

            // Rời khỏi nhóm multicast và đóng socket
            socket.leaveGroup(group);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
