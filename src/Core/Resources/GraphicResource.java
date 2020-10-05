package Core.Resources;

import Core.Renderer.Scene.Scene;

public abstract class GraphicResource {

    protected GraphicResource() {
        ResourceManager.RegisterResource(this);
    }

    public abstract void Load();
    public abstract void Unload();
    public abstract void use(Scene context);
}
