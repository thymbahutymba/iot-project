package iot_project;

import iot_project.contiki.*;

import java.util.ArrayList;

import org.jline.reader.LineReader;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class CommandsInterface {
    @ShellMethod("Print registered resources")
    public void printResources() {
        AvailableResources.printResources(false);
    }

    @ShellMethod("Set alias to a given resource.")
    public void setAlias(@ShellOption(value = { "-r", "--resource" }, defaultValue = "") String resource,
            @ShellOption({ "-a", "--alias" }) String alias) {

        if (resource.isEmpty()) {
            AvailableResources.printResources(false);
            Integer index = new LineReader().readLine("Index to rename: ");

            if (index >= AvailableResources.getInstance().size())
                System.out.println("Index out of range!!!");
            else
                AvailableResources.getInstance().get(index).setAlias(alias);
        } else
            AvailableResources.getInstance().iterator().forEachRemaining(r -> {
                if (resource.contains(r.getAddr()) && resource.contains(r.getPath()))
                    r.setAlias(alias);
                else
                    System.out.println("Can not set alias, resource not found.");
            });
    }

}