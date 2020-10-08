package Core.Resources;

import Core.Renderer.Scene.Scene;

public abstract class GraphicResource {
    private String _resourceName;

    protected GraphicResource(String resourceName) {
        _resourceName = resourceName;
        ResourceManager.RegisterResource(this);
    }

    public String getName() { return _resourceName; }

    public abstract void load();
    public abstract void unload();
    public abstract void use(Scene context);
}
