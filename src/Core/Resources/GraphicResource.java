package Core.Resources;

import Core.Renderer.Scene.Scene;

public abstract class GraphicResource {

    protected GraphicResource() {
        ResourceManager.RegisterResource(this);
    }

    public abstract void load();
    public abstract void unload();
    public abstract void use(Scene context);
}
