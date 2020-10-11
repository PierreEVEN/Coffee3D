package Core.Assets;

import Core.Renderer.Scene.Scene;

import java.io.Serializable;

public abstract class Asset implements Serializable {
    private final String _name;
    private final String _filePath;

    protected Asset(String name, String filePath) {
        _filePath = filePath;
        _name = name;
        AssetManager.RegisterAsset(this);
        load();
    }

    public abstract void load();
    public abstract void use(Scene context);

    public String getName() { return _name; }
    public String getFilepath() { return _filePath; }

    @Override
    public String toString() {
        return _name;
    }
}
