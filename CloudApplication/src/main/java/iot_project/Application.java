package iot_project;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.json.simple.JSONObject;
import java.util.HashMap;
import org.eclipse.californium.core.CaliforniumLogger;
import org.eclipse.californium.core.CoapServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.shell.jline.PromptProvider;

@SpringBootApplication
@ComponentScan(basePackages = {"iot_project"})
public class Application extends CoapServer {
    public static void main(String[] args) {
        CaliforniumLogger.disableLogging();

        Application server = new Application();
        server.add(RegistrationResource.getInstance());
        server.start();

        SpringApplication.run(Application.class, args);

        server.destroy();
    }

    @Bean
    public PromptProvider myPromptProvider() {
        return () -> new AttributedString("-> ",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
    }
}
