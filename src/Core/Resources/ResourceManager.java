package Core.Resources;

import Core.IO.LogOutput.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Handle resources
 */
public class ResourceManager {

    private static final HashMap<String, GraphicResource> _resources = new HashMap<>();

    public static Collection<GraphicResource> GetResources() { return _resources.values(); }

    public static void RegisterResource(GraphicResource resource) {
        if (_resources.containsKey(resource.toString())) {
            Log.Fail("Resource named " + resource.toString() + " already exist");
            return;
        }
        _resources.put(resource.toString(), resource);
    }

    public static void UnRegisterResource(GraphicResource resource) {
        if (resource != null) _resources.remove(resource.toString(), resource);
    }

    /**
     * Free all resources
     */
    public static void ClearResources() {
        for (GraphicResource resource : _resources.values()) {
            resource.unload();
        }
        _resources.clear();
    }

    /**
     * Find resource by name
     * @param resourceName resource name
     * @return  found resource
     */
    public static <T> T FindResource(String resourceName) {
        GraphicResource resource = _resources.get(resourceName);
        if (resource != null) {
            return ((T) resource);
        }
        Log.Warning("failed to find resource '" + resourceName + "'");
        return null;
    }
}
