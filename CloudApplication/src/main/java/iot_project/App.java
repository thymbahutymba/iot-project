package iot_project;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.net.InetSocketAddress;

@SpringBootApplication
@ComponentScan(basePackages = { "iot_project" })
public class App extends CoapServer {
    public static void main(String[] args) {
        App server = new App();
        server.add(new RegistrationResource("registration"));
        server.start();
        SpringApplication.run(App.class, args);
        server.destroy();
    }

    // @Bean
    // public AttributedString myPromptProvider() {
    //    return new AttributedString("->",
    //        AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
    //}

    // @Bean
    // public PromptProvider myPromptProvider() {
    // return () -> new AttributedString("->",
    // AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
    // }

    // public static void main(String[] args) {
    // App server = new App();
    // server.add(new CoAPResourceExample("registration"));
    // server.start();
    // }
}
