package Multicast.Sender;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPReceiver {
    public static void main(String[] args) {
        try {
            // Tạo DatagramSocket để nhận dữ liệu từ cổng 9876
            DatagramSocket socket = new DatagramSocket(9876);

            // Buffer để chứa dữ liệu nhận được
            byte[] buffer = new byte[1024];

            // Tạo DatagramPacket để nhận dữ liệu
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            System.out.println("Receiver is waiting for data...");

            // Nhận gói tin
            socket.receive(packet);

            // Chuyển đổi gói tin nhận được thành chuỗi
            String receivedMessage = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Received message: " + receivedMessage);

            // Đóng socket sau khi nhận dữ liệu
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
