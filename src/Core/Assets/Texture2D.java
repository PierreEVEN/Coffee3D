package Core.Assets;

import Core.Factories.TextureFactory;
import Core.IO.Log;
import Core.Renderer.Scene.Scene;
import Core.Resources.Texture2DResource;

public class Texture2D extends Asset {

    private transient Texture2DResource _texture;

    public Texture2D(String name, String filePath) {
        super(name, filePath);
    }

    public int getTextureID() {
        return _texture != null ? _texture.getTextureHandle() : -1;
    }

    @Override
    public void load() {
        _texture = TextureFactory.T2dFromFile(getName(), getFilepath());
    }

    @Override
    public void use(Scene context) {
        _texture.use(context);
    }
}
