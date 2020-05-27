package iot_project.provider;

import org.springframework.stereotype.Component;
import iot_project.contiki.Method;

@Component
public class PostMethod extends GeneralMethod {
    private PostMethod() {
        //super("POST");
        super(Method.POST);
    }
}