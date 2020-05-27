package iot_project.provider;

import org.springframework.stereotype.Component;

@Component
public class PutMethod extends GeneralMethod {
    private PutMethod() {
        super("PUT");
    }
}
