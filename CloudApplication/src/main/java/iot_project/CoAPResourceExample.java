package iot_project;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class CoAPResourceExample extends CoapResource {
    public CoAPResourceExample(String name) {
        super(name);
        //setObservable(true);
    }

    /*public void handleGET(CoapExchange exchange) {

        Response response = new Response(ResponseCode.CONTENT);

        if (exchange.getRequestOptions().getAccept() == MediaTypeRegistry.APPLICATION_XML) {
            response.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_XML);
            response.setPayload("<value>10</value>");
        } else if (exchange.getRequestOptions().getAccept() == MediaTypeRegistry.APPLICATION_JSON) {
            response.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
            response.setPayload("{\"value\":\"10\"}");
        } else {
            response.setPayload("Hello");
        }

        exchange.respond(response);
    }*/

    public void handlePOST(CoapExchange exchange) {

        byte[] request = exchange.getRequestPayload();
        String s = new String(request);
        System.out.println(s);
        System.out.println("new");

        //exchange.accept();

        //Integer value = Integer.parseInt(s);
        //Double resp = Math.sqrt(value);

        /*s = Double.toString(1000);

        Response response = new Response(ResponseCode.CONTENT);

        response.setPayload(s);

        exchange.respond(response);*/
        exchange.respond(ResponseCode.CREATED);
    }
}
