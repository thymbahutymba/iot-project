package iot_project;

import java.util.ArrayList;

public class AvailableResources extends ArrayList<Resource> {
    private static AvailableResources array_resources = new AvailableResources();

    private AvailableResources() {}

    public static AvailableResources getInstance() {
        return array_resources;
    }

    public static void printResources() {
        array_resources.iterator().forEachRemaining(r -> {
            System.out.println(r.getPath());
        });
    }
}