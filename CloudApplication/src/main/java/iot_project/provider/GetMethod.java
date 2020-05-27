package iot_project.provider;

import org.springframework.stereotype.Component;

@Component
public class GetMethod extends GeneralMethod {
    private GetMethod() {
        super("GET");
    }
}