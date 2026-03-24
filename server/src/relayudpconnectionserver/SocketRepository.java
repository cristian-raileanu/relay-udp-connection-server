package relayudpconnectionserver;

import java.net.DatagramPacket;
import java.net.InetAddress;

public interface SocketRepository {
    void sendPacket(final DatagramPacket packet);

    void receive(DatagramPacket packet);

    void sendMessage(String message, InetAddress address, int port);
}