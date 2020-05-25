package iot_project.contiki;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModel;

public class AvailableResources extends ArrayList<Resource> {
    private static AvailableResources array_resources = new AvailableResources();

    private AvailableResources() {
    }

    public static AvailableResources getInstance() {
        return array_resources;
    }

    public static void printResources(boolean with_index) {
        List<Integer> index = IntStream.range(0, array_resources.size()).boxed().collect(Collectors.toList());

        Object[][] data = new Object[array_resources.size()][2];

        index.forEach(i -> {
            data[i][0] = Integer.toString(i);
            data[i][1] = array_resources.get(i).asFormattedString();
        });

        TableModel tab = new ArrayTableModel(data);
        TableBuilder builder = new TableBuilder(tab);
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
}