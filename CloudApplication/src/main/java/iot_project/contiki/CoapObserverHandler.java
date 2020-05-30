package iot_project.contiki;

import java.util.ArrayList;
import java.util.stream.Collectors;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

class CoapObserverHandler implements CoapHandler {
    private String resource_str;
    private ArrayList<String> history = new ArrayList<String>();
    private final int max_size;
    private final String type;

    public CoapObserverHandler(String resource_str, String type) {
        this(resource_str, type, 10);
    }

    public CoapObserverHandler(String resource_str, String type, int max_size) {
        super();
        this.resource_str = resource_str;
        this.max_size = max_size;
        this.type = type;
    }

    public void printHistory() {
        System.out.println(this.history.stream().collect(Collectors.joining(" ")));
    }

    @Override
    public void onLoad(CoapResponse response) {
        if (!response.getOptions().isContentFormat(MediaTypeRegistry.APPLICATION_JSON))
            return;

        String content_str = response.getResponseText();
        try {
            JSONObject content = (JSONObject) new JSONParser().parse(content_str);

            if (this.history.size() == this.max_size)
                this.history.remove(0);

            this.history.add(content.get(this.type).toString());
        } catch (ParseException e) {
            System.err.println(e);
        }
    }

    @Override
    public void onError() {
        System.err.println("Error during observing " + this.resource_str);
    }

}
