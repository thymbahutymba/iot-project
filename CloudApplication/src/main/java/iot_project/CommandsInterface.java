package iot_project;

import java.util.ArrayList;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class CommandsInterface {
    private ArrayList<Resource> resource_array;

    public void addResource(Resource r) {
        resource_array.add(r);
    }

    @ShellMethod("Print message to display")
    public void echo() {
        System.out.println("echo");
    }

    @ShellMethod("Print resource path")
    public void print() {
        AvailableResources.getInstance().printResources();
    }
}