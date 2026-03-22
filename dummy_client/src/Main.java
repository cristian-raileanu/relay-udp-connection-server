import client.DummyClient;

import java.net.SocketException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("Syntax: DummyClient <host> <port> <targetHost> <id>");
            return;
        }
        final String hostname = args[0];
        final int clientPort = Integer.parseInt(args[1]);
        final int serverPort = Integer.parseInt(args[2]);
        final String targetHostname = args[3];
        final int id = Integer.parseInt(args[4]);

        try {
            new DummyClient(clientPort).runCommand(hostname, serverPort, targetHostname, id);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }
}