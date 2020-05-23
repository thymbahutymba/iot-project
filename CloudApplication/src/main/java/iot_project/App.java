package iot_project;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import java.net.InetSocketAddress;


public class App extends CoapServer {
    private static final int COAP_PORT = 5800;

    public static void main(String[] args) {
        App server = new App();
        server.add(new CoAPResourceExample("registration"));
        server.addEndpoint(new CoapEndpoint(new InetSocketAddress("[fd00::302:304:506:708]", COAP_PORT)));

        System.out.println("ciao");
        server.start();
    }
}
