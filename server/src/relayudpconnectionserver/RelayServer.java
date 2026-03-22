package relayudpconnectionserver;

import common.Logger;

import java.net.SocketException;
import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;

public class RelayServer extends Thread implements ApplicationContext {
    private final static int BUFFER_LEN = 8192;

    private final SocketRepository socketRepository;

    public RelayServer(final int port) throws SocketException {
        socketRepository = new SocketRepositoryImpl(port);
    }

    @Override
    public void run() {
        while (true) {
            try {
                final byte[] buffer = new byte[BUFFER_LEN];
                final DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socketRepository.receive(request);
                Logger.info("\n[RelayServer] message received! \"" + new String(request.getData(), StandardCharsets.UTF_8)
                        + " from " + request.getAddress().getHostAddress() + "\"");
                final PeerMessageAnalyzer session = new PeerMessageAnalyzer(this, request);
                session.start();
            } catch (Exception e) {
                Logger.error(e.getMessage());
            }
        }
    }

    @Override
    public SocketRepository getSocketRepository() {
        return socketRepository;
    }
}