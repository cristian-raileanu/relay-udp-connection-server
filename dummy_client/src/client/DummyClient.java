package client;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.TimeUnit;

public class DummyClient {
    private static final int BUFFER_SIZE = 8192;
    private final DatagramSocket socket;

    public DummyClient(int clientPort) throws SocketException {
        this.socket = new DatagramSocket(clientPort);
    }

    public void runCommand(String hostname, int serverPort, String targetHostname, int id) {
        try {
            final InetAddress address = InetAddress.getByName(hostname);

            final InetAddress targetAddress = InetAddress.getByName(targetHostname);
            if (id == 1) {
                pingServer(address, serverPort);
            } else if (id == 2) {
                messagePeer(address, serverPort, targetAddress);
            } else if (id == 3) {
                bombardPeer(address, serverPort, 2);
            } else if (id == 4) {
                respondPeer(address, serverPort);
            }
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        } 
    }

    private void pingServer(final InetAddress serverAddress, final int serverPort) throws IOException, SecurityException  {
        sendMessage("PING dummy_msg", serverAddress, serverPort);
        System.out.println("PING dummy_msg sent to " + serverAddress.getHostAddress() + " port " + serverPort);

        String message = receiveMessage();
        System.out.println("Message received from " + serverAddress.getHostAddress() + " port " + serverPort + ": " + message);
    }

    private void messagePeer(final InetAddress serverAddress, final int port, final InetAddress peerAddress) throws IOException {
        System.out.println("send message to peer " + peerAddress.getHostAddress());

        InetAddress myIp = retrieveRealIp(serverAddress, port);
        sendMessage(prefixMessage(peerAddress, myIp, "dummy_message"), serverAddress, port);

        final String response = receiveMessage();
        System.out.println(response);
    }

    private void bombardPeer(final InetAddress address, final int port, final int replicas) throws IOException, InterruptedException {
        InetAddress myIp = retrieveRealIp(address, port);
        for (int i=0; i<replicas; i++) {
            System.out.println("BOMBARD");
            sendMessage(prefixMessage(address, myIp, "BOMBARD"), address, port);

            TimeUnit.SECONDS.sleep(2);
        }
    }

    private void respondPeer(InetAddress serverAddress, int serverPort) throws IOException {
        InetAddress myIp = retrieveRealIp(serverAddress, serverPort);

        while (true) {
            pingServer(serverAddress, serverPort);

            final DatagramPacket response = receiveDataGram();
            String message = datagramMessage(response);
            System.out.println("received message from peer: " + message);

            String sender = getSecondWord(message);
            InetAddress senderAddress = InetAddress.getByName(sender);
            sendMessage(prefixMessage(senderAddress, myIp, "ACK for <" + message + ">"), response.getAddress(),
                    response.getPort());
        }
    }

    private void sendMessage(final String message, final InetAddress address, final int port) throws IOException {
        final byte[] buffer = message.getBytes();
        DatagramPacket request = new DatagramPacket(buffer, buffer.length, address, port);
        socket.send(request);
    }

    private String receiveMessage() throws IOException {
        final DatagramPacket response = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
        socket.receive(response);
        return datagramMessage(response);
    }

    private DatagramPacket receiveDataGram() throws IOException {
        final DatagramPacket response = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
        socket.receive(response);
        return response;
    }

    private InetAddress retrieveRealIp(InetAddress serverAddress, int serverPort) throws IOException {
        sendMessage("IP", serverAddress, serverPort);
        String message = receiveMessage();
        String address = getSecondWord(message);
        return InetAddress.getByName(address);
    }

    private static String datagramMessage(final DatagramPacket response) {
        return new String(response.getData(), 0, response.getLength());
    }

    private static String prefixMessage(InetAddress destination, InetAddress realSenderIp, String message) throws UnknownHostException {
        return destination.getHostAddress() + " " + realSenderIp.getHostAddress() + " " + message;
    }

    private static String getSecondWord(final String message) {
        int i=0;
        while (message.charAt(i) == ' ') {
            i++;
        }
        for (; i < 20; i++) {
            if (message.charAt(i) == ' ') {
                break;
            }
        }
        while (message.charAt(i) == ' ') {
            i++;
        }
        StringBuilder secondWord = new StringBuilder();
        for (; i < message.length(); i++) {
            secondWord.append(message.charAt(i));
        }

        return secondWord.toString();
    }
}
