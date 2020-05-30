package iot_project;

import iot_project.contiki.*;

import java.util.logging.Logger;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class RegistrationResource extends CoapResource {
    private static RegistrationResource regResource = new RegistrationResource("registration");
    private static final Logger LOGGER = Logger.getLogger(RegistrationResource.class.getName());

    private RegistrationResource(String name) {
        super(name);
    }

    public static RegistrationResource getInstance() {
        return regResource;
    }

    public void handleGET(CoapExchange exchange) {
        exchange.accept();
    
        String addr = exchange.getSourceAddress().toString().substring(1);
        String uri = new String("coap://[" + addr + "]:5683/.well-known/core");

        CoapClient req = new CoapClient(uri);
        String response = req.get().getResponseText().replace("</.well-known/core>;", "");

        for (String resource : response.split("\n")) {
            Resource newRes = new Resource(addr, resource);

            if (!AvailableResources.isPresent(newRes)) {
                AvailableResources.getInstance().add(newRes);
                LOGGER.info("New resource at [" + newRes.getAddr() + "]" + newRes.getPath());
            }
        }
    }
}
