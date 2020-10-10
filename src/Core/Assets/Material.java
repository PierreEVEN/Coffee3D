package Core.Assets;

import Core.Factories.MaterialFactory;
import Core.Renderer.Scene.Scene;
import Core.Resources.MaterialResource;
import Core.Resources.ResourceManager;
import Core.Resources.TextureResource;

public class Material extends Asset {

    private transient MaterialResource _mat;
    private String[] _textureNames;

    public Material(String name, String filePath) {
        super(name, filePath);
        _textureNames = null;
    }

    public Material(String name, String filePath, String[] textureNames) {
        super(name, filePath);
        _textureNames = textureNames;
    }

    public MaterialResource getShader() { return _mat; }

    @Override
    public void load() {
        String fileCleanName = getFilepath().replaceFirst("[.][^.]+$", "");
        _mat = MaterialFactory.FromFiles(getName(), fileCleanName + ".vert", fileCleanName + ".frag");
    }

    @Override
    public void use(Scene context) {
        _mat.use(context);
    }
}
