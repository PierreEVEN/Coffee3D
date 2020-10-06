package Core.Resources;

import Core.IO.Log;

import java.util.ArrayList;

public class ResourceManager {

    private static ArrayList<GraphicResource> _resources = new ArrayList<>();
    public static ArrayList<GraphicResource> GetResources() { return _resources; }

    public static void RegisterResource(GraphicResource resource) {
        _resources.add(resource);
    }

    public static void ClearResources() {
        for (GraphicResource resource : _resources) {
            resource.unload();
        }
        _resources.clear();
    }



}
