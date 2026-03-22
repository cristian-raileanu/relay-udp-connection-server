import common.Logger;
import relayudpconnectionserver.RelayServer;

public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            Logger.error("Syntax: Server <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);

        if (args.length > 1) {
            Logger.setPriority(args[1]);
            Logger.info("Logger priority set to " + args[1]);
        }

        try {
            final Thread server = new RelayServer(port);
            server.start();
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
    }
}