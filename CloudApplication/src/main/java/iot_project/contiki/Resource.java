package iot_project.contiki;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapObserveRelation;

public class Resource extends CoapClient {
    private String addr;
    private String path;
    private Method[] methods;
    private String alias = new String();
    private boolean isObservable = false;
    private CoapObserveRelation obsRelation;
    private CoapObserverHandler obsHandler;
    private HashMap<String, String> payload_format = new HashMap<>();

    public Resource(String addr, String content) {
        super();

        String[] content_split = content.split(";");

        this.addr = addr;
        String path_str = content_split[0];
        this.path = path_str.substring(path_str.indexOf("<") + 1, path_str.indexOf(">"));

        String methods_str = content_split[2];
        this.methods = Arrays
                .stream(methods_str.substring(0, methods_str.lastIndexOf("\""))
                        .substring(methods_str.indexOf("\"") + 1).split("/", 0))
                .map(rs -> Method.valueOf(rs.toUpperCase())).toArray(size -> new Method[size]);

        /* Extract payload format */
        String payload_str = content_split[3].replace("payload=", "");

        if (!payload_str.isEmpty())
            for (String p : payload_str.split(",")) {
                String key = p.substring(0, p.indexOf(":"));
                String values = p.substring(p.indexOf(":") + 1, p.length());

                this.payload_format.put(key, values);
            }

        // Method from CoapClient
        this.setURI("coap://[" + this.addr + "]" + this.path);

        // Init observing relation
        this.isObservable = content.contains("obs");

        if (!this.isObservable)
            return;

        for (Map.Entry<String, String> e : this.payload_format.entrySet()) {
            if (/*e.getValue().equals("int") ||*/ e.getValue().equals("float")) {
                this.obsHandler = new CoapObserverHandler(this.toFormattedString(), e.getKey());
                this.obsRelation = this.observe(this.obsHandler);
                break;
            }
        }
    }

    public String getAddr() {
        return this.addr;
    }

    public String getPath() {
        return this.path;
    }

    public String getAlias() {
        return this.alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public HashMap<String, String> getPayloadFormat() {
        return this.payload_format;
    }

    public void printHistory() {
        if (this.isObservable)
            this.obsHandler.printHistory();
        else 
            // This code should not be reachable thanks to tab completition
            System.err.println("The resource is not osservable");
    }

    public boolean hasMethod(Method method) {
        for (Method m : this.methods) {
            if (m.equals(method))
                return true;
        }

        return false;
    }

    public boolean isObservable() {
        return this.isObservable;
    }

    public String getFormattedAliasAddr() {
        String result = new String();
        if (!this.alias.isEmpty())
            result = "(" + this.alias + ") ";

        result += "[" + this.addr + "]";

        return result;
    }

    public String toFormattedString() {
        return this.getFormattedAliasAddr() + this.path;
    }
}
