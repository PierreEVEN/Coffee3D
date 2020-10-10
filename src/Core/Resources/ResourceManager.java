package Core.Resources;

import Core.IO.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResourceManager {

    private static HashMap<String, GraphicResource> _resources = new HashMap<>();
    public static HashMap<String, GraphicResource> GetResources() { return _resources; }

    public static void RegisterResource(GraphicResource resource) {
        if (_resources.containsKey(resource.getName())) {
            Log.Fail("Resource named " + resource.getName() + " already exist");
            return;
        }
        _resources.put(resource.getName(), resource);
    }

    public static void ClearResources() {
        for (GraphicResource resource : _resources.values()) {
            resource.unload();
        }
        _resources.clear();
    }

    public static GraphicResource GetResource(String resourceName) {
        GraphicResource resource = _resources.get(resourceName);
        if (resource != null) {
            return resource;
        }
        Log.Warning("failed to find resoure '" + resourceName + "'");
        return null;
    }
}
