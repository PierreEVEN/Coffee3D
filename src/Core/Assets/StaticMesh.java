package Core.Assets;

import Core.Factories.MeshFactory;
import Core.IO.Log;
import Core.Renderer.Scene.Scene;
import Core.Resources.GraphicResource;
import Core.Resources.MaterialResource;
import Core.Resources.MeshResource;

/**
 * NIY
 */
public class StaticMesh extends Asset {
    private transient MeshResource[] _sections;

    public StaticMesh(String name, String filePath) {
        super(name, filePath);
    }

    @Override
    public void load() {
        _sections = MeshFactory.FromFile(getName(), getFilepath());
    }

    @Override
    public void use(Scene context) {
        if (_sections != null) {
            for (MeshResource section : _sections) {
                if (section != null) section.use(context);
            }
        }
        else {
            Log.Warning(toString() + " has empty sections");
        }
    }
}
