package iot_project.contiki;

import java.util.Arrays;
import org.eclipse.californium.core.CoapClient;

public class Resource extends CoapClient {
    private String addr;
    private String path;
    // private String methods;
    private Method[] methods;
    private String alias = new String();
    private boolean isObservable = false;

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

    // public boolean hasMethod(String method) {
    // return this.methods.contains(method.toUpperCase());
    // }

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
