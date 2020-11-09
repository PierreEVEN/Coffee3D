package coffee3D.core.assets.types;

import coffee3D.core.assets.Asset;
import coffee3D.core.resources.factories.TextureFactory;
import coffee3D.core.io.log.Log;
import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.resources.ResourceManager;
import coffee3D.core.resources.types.Texture2DResource;
import coffee3D.core.resources.types.TextureResource;
import coffee3D.core.types.Color;
import imgui.ImGui;
import org.joml.Vector2i;

import java.io.File;

public class Texture2D extends Asset {

    private static final long serialVersionUID = -868665333590764448L;
    private transient Texture2DResource _texture;
    protected boolean _linearFilter = true;
    private static final Color textureColor = new Color(255/255f, 51/255f, 51/255f, 1);
    private static final String[] meshExtensions = new String[] {"png"};

    public Texture2D(String name, File filePath, File assetPath) {
        super(name, filePath, assetPath);
    }

    @Override
    public String[] getAssetExtensions() {
        return meshExtensions;
    }

    @Override
    public Color getAssetColor() {
        return textureColor;
    }

    public int getTextureID() {
        return _texture != null ? _texture.getTextureHandle() : -1;
    }

    @Override
    public void load() {
        _texture = TextureFactory.T2dFromFile(getName(), getSourcePath(), _linearFilter);
    }

    @Override
    public void reload() {
        ResourceManager.UnRegisterResource(_texture);
        Texture2DResource newTexture = null;
        try {
            newTexture = TextureFactory.T2dFromFile(getName(), getSourcePath(), _linearFilter);
        }
        catch (Exception e) {
            Log.Warning("failed to load or compile shaders : " + e.getMessage());
        }

        if (newTexture != null) {
            _texture = newTexture;
        }
        else {
            ResourceManager.RegisterResource(newTexture);
        }
    }

    @Override
    public void use(Scene context) {
        _texture.use(context);
    }

    @Override
    public void drawDetailedContent() {
        super.drawDetailedContent();
        if (_texture != null) {
            ImGui.text("resolution : " + _texture.getWidth() + "x" + _texture.getHeight());
            ImGui.image(getTextureID(), _texture.getWidth(), _texture.getHeight(), 0, 1, 1, 0);
        }
    }

    @Override
    public int getThumbnailSourceTexture(Vector2i textureSize) {
        textureSize.set(_texture.getWidth(), _texture.getHeight());
        return getTextureID();
    }
}
