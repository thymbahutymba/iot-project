package iot_project.provider.method;

import org.springframework.stereotype.Component;
import iot_project.contiki.Method;

@Component
public class GetMethod extends GeneralMethod {
    private GetMethod() {
        //super("GET");
        super(Method.GET);
    }
}