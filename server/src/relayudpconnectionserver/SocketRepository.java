package relayudpconnectionserver;

import java.net.DatagramPacket;

public interface SocketRepository {
    void sendPacket(final DatagramPacket packet);

    void receive(DatagramPacket packet);
}