package relayudpconnectionserver;

import common.Logger;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.DatagramPacket;
import java.io.IOException;

public class SocketRepositoryImpl implements SocketRepository {
    protected DatagramSocket socket;
    final int port;

    public SocketRepositoryImpl(final int port) throws SocketException {
        this.port = port;
        this.socket = new DatagramSocket(port);
    }

    @Override
    public void sendPacket(final DatagramPacket packet) {
        try {
            socket.send(packet);
        } catch (IOException e) {
            Logger.error(e.getMessage());
        }
    }

    @Override
    public void receive(DatagramPacket packet) {
        try {
            socket.receive(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
