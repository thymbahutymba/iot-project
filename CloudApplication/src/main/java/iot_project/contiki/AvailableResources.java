package iot_project.contiki;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.core.MethodParameter;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.standard.ValueProvider;
import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModel;
import org.springframework.stereotype.Component;

@Component
public class AvailableResources extends ArrayList<Resource> implements ValueProvider {
    /*
     * Since AvailableResources is a singleton, instantiating array_resource as AvailableResouces is
     * pointless due to the fact that each operation onto the array is prefixed by the getInstance
     * which either returns AvailableResource which has to implement ArrayList or return the
     * ArrayList itself should be the same.
     * 
     * ` public class AvailableResources extends ArrayList<Resource> implements ValueProvider
     * private static AvailableResources array_resources = new AvailableResources(); `
     */
    private static AvailableResources array_resources = new AvailableResources();

    private AvailableResources() {
    }

    public static AvailableResources getInstance() {
        return array_resources;
    }

    public static void printResources(boolean with_index) {
        if (array_resources.isEmpty()) {
            System.out.println("No resources available.");
            return;
        }

        TableModel table;

        if (with_index) {
            List<Integer> index =
                    IntStream.range(0, array_resources.size()).boxed().collect(Collectors.toList());

            Object[][] data = new Object[array_resources.size()][2];

            index.forEach(i -> {
                data[i][0] = Integer.toString(i);
                data[i][1] = array_resources.get(i).asFormattedString();
            });

            table = new ArrayTableModel(data);
        } else {
            Object[][] data = new Object[][] {array_resources.stream()
                    .map(r -> r.asFormattedString()).collect(Collectors.toList()).toArray()};

            table = new ArrayTableModel(data).transpose();
        }

        TableBuilder builder = new TableBuilder(table);
        builder.addFullBorder(BorderStyle.oldschool);
        System.out.println(builder.build().render(80));
    }

    public static boolean isPresent(Resource res) {
        for (Resource r : array_resources) {
            if (res.getAddr().equals(r.getAddr()) && res.getPath().equals(r.getPath()))
                return true;
        }

        return false;
    }

    public static Resource getResource(String res) {
        return array_resources.stream().filter(r -> r.asFormattedString().equals(res)).findAny()
                .get();
    }

    /* Same implementation of the ValueProviderSupport abstract class */
    @Override
    public boolean supports(MethodParameter parameter, CompletionContext completionContext) {
        ShellOption annotation = parameter.getParameterAnnotation(ShellOption.class);
        if (annotation == null) {
            return false;
        }
        return annotation.valueProvider().isAssignableFrom(this.getClass());
    }

    @Override
    public List<CompletionProposal> complete(MethodParameter methodParameter,
            CompletionContext completionContext, String[] strings) {

        List<CompletionProposal> result = new ArrayList<CompletionProposal>();
        String userInput = completionContext.currentWordUpToCursor();

        array_resources.stream().filter(t -> t.asFormattedString().contains(userInput))
                .forEach(t -> result.add(new CompletionProposal(t.asFormattedString())));

        return result;
    }
}
