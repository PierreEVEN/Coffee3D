package Core.Assets;

import Core.Renderer.Scene.Scene;

import java.io.Serializable;

/**
 * Asset base class
 */
public abstract class Asset implements Serializable {
    private final String _name;
    private final String _filePath;

    protected Asset(String name, String filePath) {
        _filePath = filePath;
        _name = name;
        AssetManager.RegisterAsset(this);
        load();
    }

    /**
     * Load asset into memory
     */
    public abstract void load();

    /**
     * Draw item into given scene
     * @param context draw context
     */
    public abstract void use(Scene context);

    /**
     * get asset name
     * @return asset name
     */
    public String getName() { return _name; }

    /**
     * get asset file path
     * @return relative path
     */
    public String getFilepath() { return _filePath; }

    @Override
    public String toString() { return getName(); }
}
