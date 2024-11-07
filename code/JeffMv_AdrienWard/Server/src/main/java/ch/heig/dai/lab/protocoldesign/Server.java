package ch.heig.dai.lab.protocoldesign;

// supported protocol versions : 1 -> 2

public class Server {
    final int SERVER_PORT = 2277;

    public static void main(String[] args) {
        // Create a new server and run it
        Server server = new Server();
        server.run();
    }

    private void run() {
    }

    // TODO : handling communication
    // - send supported protocol version range
}