package iot_project;

import iot_project.contiki.*;
import iot_project.provider.GetMethod;
import iot_project.provider.PutMethod;
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
	public void setAlias(
			@ShellOption(value = {"-r", "--resource"},
					valueProvider = AvailableResources.class) String resource,
			@ShellOption({"-a", "--alias"}) String alias) {

		AvailableResources.getInstance().stream().filter(r -> {
			if (r.getAlias().isEmpty())
				return resource.contains(r.getAddr()) && resource.contains(r.getPath());
			else
				return resource.contains(r.getAlias()) && resource.contains(r.getPath());
		}).forEach(r -> r.setAlias(alias));
	}

	@ShellMethod("Make get request to given resource")
	public void get(@ShellOption(value = {"-r", "--resource"},
			valueProvider = GetMethod.class) String resource) {
		
		Resource r = AvailableResources.getResource(resource);
		if(!r.hasMethod("GET"))
			System.out.println("The resource does not provide GET method.");
		else 
			System.out.println(r.get().getResponseText());
	}

	@ShellMethod("Make put request to given resource")
	public void put(@ShellOption(value = {"-r", "--resource"},
			valueProvider = PutMethod.class) String resource) {
		
		Resource r = AvailableResources.getResource(resource);
		if(!r.hasMethod("PUT"))
			System.out.println("The resource does not provide GET method.");
		else 
			System.out.println(r.get().getResponseText());
	}
}
