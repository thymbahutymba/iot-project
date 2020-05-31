package iot_project;

import iot_project.contiki.*;
import iot_project.provider.*;
import iot_project.provider.method.*;
import java.io.IOException;
import java.util.HashMap;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.TerminalBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class CommandsInterface {
	private LineReader lineReader;

	private CommandsInterface() throws IOException {
		this.lineReader =
				LineReaderBuilder.builder().terminal(TerminalBuilder.builder().build()).build();
	}

	@ShellMethod("Print registered resources")
	public void printResources() {
		AvailableResources.printResources(false);
	}

	@ShellMethod("Set alias to a given resource.")
	public void setAlias(
			@ShellOption(value = {"-r", "--resource"},
					valueProvider = AliasAddr.class) String resource,
			@ShellOption({"-a", "--alias"}) String alias) {

		AvailableResources.getInstance().stream().filter(r -> resource.contains(r.getAddr()))
				.forEach(r -> r.setAlias(alias));
	}

	@ShellMethod("Execute get request to given resource")
	public void get(@ShellOption(value = {"-r", "--resource"},
			valueProvider = GetMethod.class) String resource) throws Exception {

		Resource r = AvailableResources.getResource(resource);
		if (!r.hasMethod(Method.GET)) {
			System.err.println("The resource does not provide GET method.");
			return;
		}

		String resp_str = r.get(MediaTypeRegistry.APPLICATION_JSON).getResponseText();
		JSONObject resp_json = (JSONObject) new JSONParser().parse(resp_str);

		for (Object k : resp_json.keySet()) {
			if (r.getPayloadFormat().containsKey(k)) {
				System.out.println(k + ": " + resp_json.get(k));
			}
		}

		/*
		 * resp_json.keySet().stream().filter(k -> { return r.getPayloadFormat().containsKey(k);
		 * }).forEach(k -> { System.out.println(k + ": " + resp_json.get(k)); });
		 */
	}

	private JSONObject createJsonPayload(Resource r) {
		HashMap<String, Object> json_payload = new HashMap<>();

		r.getPayloadFormat().forEach((k, v) -> {
			if (json_payload.containsKey("mode") && json_payload.get("mode").equals("off")
					/*&& (v.equals("float") || v.equals("int"))*/)
				return;

			String content = lineReader.readLine(k + " [ " + v + " ]: ");

			if (v.contains("|") && !v.substring(0, v.indexOf("|")).equals(content)
					&& !v.substring(v.indexOf("|") + 1).equals(content)) {
				System.err.println("Value " + content + " not allowed.");
				return;
			}

			if (v.equals("float"))
				json_payload.put(k, Float.parseFloat(content));
			/*else if (v.equals("int"))
				json_payload.put(k, Integer.parseInt(content));*/
			else
				json_payload.put(k, content);
		});

		return new JSONObject(json_payload);
	}

	@ShellMethod("Execute post request to given resource")
	public void post(@ShellOption(value = {"-r", "--resource"},
			valueProvider = PostMethod.class) String resource) {

		Resource r = AvailableResources.getResource(resource);
		if (!r.hasMethod(Method.POST)) {
			System.err.println("The resource does not provide PUT method.");
			return;
		}

		JSONObject json_payload = createJsonPayload(r);
		r.post(json_payload.toJSONString(), MediaTypeRegistry.APPLICATION_JSON);
	}

	@ShellMethod("Execute put request to given resource")
	public void put(@ShellOption(value = {"-r", "--resource"},
			valueProvider = PutMethod.class) String resource) {

		Resource r = AvailableResources.getResource(resource);
		if (!r.hasMethod(Method.PUT)) {
			System.err.println("The resource does not provide PUT method.");
			return;
		}

		JSONObject json_payload = createJsonPayload(r);
		r.put(json_payload.toJSONString(), MediaTypeRegistry.APPLICATION_JSON);
	}

	@ShellMethod("Print history of given observable resource")
	public void history(@ShellOption(value = {"-r", "--resource"},
			valueProvider = ObservableResources.class) String resource) {

		AvailableResources.getResource(resource).printHistory();
	}
}
