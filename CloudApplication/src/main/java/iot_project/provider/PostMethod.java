package iot_project.provider;

import org.springframework.stereotype.Component;

@Component
public class PostMethod extends GeneralMethod {
    private PostMethod() {
        super("POST");
    }
}