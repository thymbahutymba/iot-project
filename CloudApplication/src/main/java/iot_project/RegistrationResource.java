package iot_project;

import iot_project.contiki.*;

import java.net.InetAddress;
import java.util.logging.Logger;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
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
        // exchange.accept();
        exchange.reject();

        String addr = exchange.getSourceAddress().toString().substring(1);
        String uri = new String("coap://[" + addr + "]:5683/.well-known/core");

        CoapClient req = new CoapClient(uri);

        for (String response : req.get().getResponseText().split("\n")) {
            Resource newRes = new Resource(addr, response);

            if (!AvailableResources.isPresent(newRes)) {
                AvailableResources.getInstance().add(newRes);
                LOGGER.info("New resource at [" + newRes.getAddr() + "]" + newRes.getPath());
            }
        }
    }

    public void handlePOST(CoapExchange exchange) {

        byte[] request = exchange.getRequestPayload();
        String s = new String(request);
        System.out.println(s);
        System.out.println("new");

        // exchange.accept();

        // Integer value = Integer.parseInt(s);
        // Double resp = Math.sqrt(value);

        /*
         * s = Double.toString(1000);
         * 
         * Response response = new Response(ResponseCode.CONTENT);
         * 
         * response.setPayload(s);
         * 
         * exchange.respond(response);
         */
        exchange.respond(ResponseCode.CREATED);
    }

}
