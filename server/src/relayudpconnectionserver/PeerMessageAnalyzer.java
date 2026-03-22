package relayudpconnectionserver;

import common.Logger;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

/**
 * PeerMessageAnalyzer
 *
 * @author criss.tmd@gmail.com
 */
public class PeerMessageAnalyzer extends Thread {
    private final DatagramPacket packet;
    private final SocketRepository socketRepository;

    public PeerMessageAnalyzer(final ApplicationContext applicationContext, final DatagramPacket packet) {
        this.socketRepository = applicationContext.getSocketRepository();
        this.packet = packet;
    }

    @Override
    public void run() {
        processMessage(packet);
    }

    private void processMessage(final DatagramPacket packet) {
        Logger.info("[PeerMessageAnalyzer] processMessage");
        if (packet == null) {
            return;
        }
        final InetAddress clientAddress = packet.getAddress();
        final int clientPort = packet.getPort();
        final String message = new String(packet.getData(), StandardCharsets.UTF_8);

        final String firstWord = getFirstWord(message);
        if (firstWord.isEmpty()) {
            Logger.error("[PeerMessageAnalyzer] Error: some issues reading first message");
            return;
        }

        try {
            if (firstWord.equals("PING")) {
                forwardPacketToClient(packet, clientAddress, clientPort);
            } else if (firstWord.equals("IP")) {
                String senderAddress = clientAddress.getHostAddress();
                packet.setData(("IP " + senderAddress).getBytes(StandardCharsets.UTF_8));
                forwardPacketToClient(packet, clientAddress, clientPort);
            }else {
                InetAddress destination = InetAddress.getByName(firstWord);
                forwardPacketToClient(packet, destination, clientPort);
            }
        } catch (UnknownHostException e) {
            Logger.error("[PeerMessageAnalyzer] Error: " + e.getMessage());
        }
    }

    private void forwardPacketToClient(final DatagramPacket packet, InetAddress address, int port) {
        Logger.info("[PeerMessageAnalyzer] forwardPacketToClient " + new String(packet.getData(), StandardCharsets.UTF_8));
        packet.setAddress(address);
        packet.setPort(port);
        socketRepository.sendPacket(packet);
    }

    private static String getFirstWord(final String message) {
        StringBuilder firstWord = new StringBuilder();
        for (int i = 0; i < Math.min(message.length(), 20); i++) {
            if (message.charAt(i) == ' ') {
                break;
            }
            firstWord.append(message.charAt(i));
        }
        return firstWord.toString();
    }
}
