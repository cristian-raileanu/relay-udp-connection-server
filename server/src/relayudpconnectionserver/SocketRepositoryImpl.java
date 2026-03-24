package relayudpconnectionserver;

import common.Logger;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.DatagramPacket;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SocketRepositoryImpl implements SocketRepository {
    protected DatagramSocket socket;
    final int port;
    final ConcurrentMap<InetAddress, Integer> clientIpToPortMap;

    public SocketRepositoryImpl(final int port) throws SocketException {
        this.port = port;
        this.socket = new DatagramSocket(port);
        this.clientIpToPortMap = new ConcurrentHashMap<>();
    }

    @Override
    public void sendPacket(final DatagramPacket packet) {
        final Integer port = clientIpToPortMap.get(packet.getAddress());
        if (port == null) {
            return;
        }
        packet.setPort(port);
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
            clientIpToPortMap.putIfAbsent(packet.getAddress(), packet.getPort());
        } catch (IOException e) {
            Logger.error(e.getMessage());
        }
    }

    @Override
    public void sendMessage(final String message, final InetAddress address) {
        final Integer port = clientIpToPortMap.get(address);
        if (port == null) {
            return;
        }
        final byte[] buffer = message.getBytes();
        try {
            DatagramPacket request = new DatagramPacket(buffer, buffer.length, address, port);
            socket.send(request);
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
    }
}
