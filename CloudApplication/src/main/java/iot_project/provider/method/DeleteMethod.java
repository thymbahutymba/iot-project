package iot_project.provider.method;

import org.springframework.stereotype.Component;
import iot_project.contiki.Method;

@Component
public class DeleteMethod extends GeneralMethod {
    private DeleteMethod() {
        //super("DELETE");
        super(Method.DELETE);
    }
}
