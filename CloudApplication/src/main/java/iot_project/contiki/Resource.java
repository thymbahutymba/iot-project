package iot_project.contiki;

import java.util.Arrays;
import java.util.Comparator;
import java.lang.Comparable;
import org.eclipse.californium.core.CoapClient;

import java.lang.*;
import java.net.InetAddress;

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
        this.path = content_split[1].substring(content_split[1].indexOf("<") + 1, content_split[1].indexOf(">"));
        this.methods = content_split[3];
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