package coffee3D.core.resources;

import coffee3D.core.renderer.scene.Scene;

/**
 * Handle GPU data
 */
public abstract class GraphicResource {
    private final String _resourceName;

    protected GraphicResource(String resourceName) {
        _resourceName = resourceName;
        ResourceManager.RegisterResource(this);
    }

    /**
     * load resources
     */
    public abstract void load();

    /**
     * unload resources
     */
    public abstract void unload();

    public final void delete() {
        unload();
        ResourceManager.UnRegisterResource(this);
    }

    /**
     * Draw item to desired scene
     * @param context scene context
     */
    public abstract void use(Scene context);

    @Override
    public String toString() { return _resourceName; }
}
