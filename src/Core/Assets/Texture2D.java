package Core.Assets;

import Core.Factories.TextureFactory;
import Core.Renderer.Scene.Scene;
import Core.Resources.Texture2DResource;
import imgui.ImGui;

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

    @Override
    protected void drawThumbnailImage() {
        if (_texture != null) {
            if (ImGui.imageButton(_texture.getTextureHandle(), 64, 64, 0, 1, 1, 0)) {
                // On clicked
            }
        }
        else {
            super.drawThumbnailImage();
        }
    }
}
