package iot_project.provider;

import org.springframework.stereotype.Component;
import iot_project.contiki.Method;

@Component
public class PutMethod extends GeneralMethod {
    private PutMethod() {
        //super("PUT");
        super(Method.PUT);
    }
}
