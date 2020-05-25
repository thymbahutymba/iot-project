package iot_project;

import java.net.InetAddress;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class RegistrationResource extends CoapResource {
    // private public Constructor
    // public getInstance

    public RegistrationResource(String name) {
        super(name);
        // setObservable(true);
    }

    public void handleGET(CoapExchange exchange) {
        exchange.accept();
        // exchange.reject();
        InetAddress addr = exchange.getSourceAddress();

        String uri = new String("coap://[" + addr.toString().substring(1) + "]:5683/.well-known/core");
        CoapClient req = new CoapClient(uri);

        String res = req.get().getResponseText();

        AvailableResources.getInstance().add(new Resource(addr.toString().substring(1), res));

        //Resource r = new Resource(addr.toString().substring(1), res);
        //System.out.println(r.getAddr());
        //System.out.println(r.hasMethod("post"));
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
