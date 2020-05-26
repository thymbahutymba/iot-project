package iot_project.contiki;

import org.eclipse.californium.core.CoapClient;

public class Resource extends CoapClient {
    private String addr;
    private String path;
    private String methods;
    private String alias = new String();
    private boolean isObservable = false;

    public Resource(String addr, String content) {
        super();

        String[] content_split = content.split(";");

        this.addr = addr;
        String path_str = content_split[0];
        this.path = path_str.substring(path_str.indexOf("<") + 1, path_str.indexOf(">"));
        this.methods = content_split[2];
        this.isObservable = content.contains("obs");

        // Method from CoapClient
        this.setURI("coap://[" + this.addr + "]" + this.path);
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

    public boolean hasMethod(String method) {
        return this.methods.contains(method.toUpperCase());
    }

    public boolean isObservable() {
        return this.isObservable;
    }

    public String asFormattedString() {
        return "[" + ((this.alias.isEmpty()) ? this.addr : this.alias) + "]" + this.path;
    }
}