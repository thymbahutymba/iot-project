package iot_project;

import iot_project.contiki.*;
import iot_project.provider.*;
import iot_project.provider.method.*;
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
					valueProvider = AliasAddr.class) String resource,
			@ShellOption({"-a", "--alias"}) String alias) {

		// AvailableResources.getInstance().stream().filter(r -> {
		// if (r.getAlias().isEmpty())
		// return resource.contains(r.getAddr()) /*&& resource.contains(r.getPath()*/);
		// else
		// return resource.contains(r.getAlias()) /*&& resource.contains(r.getPath()*/);
		// }).forEach(r -> r.setAlias(alias));
		AvailableResources.getInstance().stream().filter(r -> resource.contains(r.getAddr()))
				.forEach(r -> r.setAlias(alias));
	}

	@ShellMethod("Make get request to given resource")
	public void get(@ShellOption(value = {"-r", "--resource"},
			valueProvider = GetMethod.class) String resource) {

		Resource r = AvailableResources.getResource(resource);
		// if(!r.hasMethod("GET"))
		if (!r.hasMethod(Method.GET))
			System.out.println("The resource does not provide GET method.");
		else
			System.out.println(r.get().getResponseText());
	}

	@ShellMethod("Make put request to given resource")
	public void put(@ShellOption(value = {"-r", "--resource"},
			valueProvider = PutMethod.class) String resource) {

		Resource r = AvailableResources.getResource(resource);
		// if(!r.hasMethod("PUT"))
		if (!r.hasMethod(Method.PUT))
			System.out.println("The resource does not provide GET method.");
		else
			System.out.println(r.get().getResponseText());
	}
}
