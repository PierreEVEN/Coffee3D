package Core.Assets.Types;

import Core.Assets.Asset;
import Core.Factories.TextureFactory;
import Core.IO.LogOutput.Log;
import Core.Renderer.Scene.Scene;
import Core.Resources.ResourceManager;
import Core.Resources.Texture2DResource;
import Core.Types.Color;
import imgui.ImGui;

import java.io.File;

public class Texture2D extends Asset {

    private static final long serialVersionUID = -868665333590764448L;
    private transient Texture2DResource _texture;
    private static final Color textureColor = new Color(.9f, .5f, .5f, 1);
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
        _texture = TextureFactory.T2dFromFile(getName(), getSourcePath());
    }

    @Override
    public void reload() {
        ResourceManager.UnRegisterResource(_texture);
        Texture2DResource newTexture = null;
        try {
            newTexture = TextureFactory.T2dFromFile(getName(), getSourcePath());
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
    protected void drawThumbnailImage() {
        if (_texture != null) {
            if (ImGui.imageButton(_texture.getTextureHandle(), 64, 64, 0, 1, 1, 0)) {
                if (_assetEditFunction != null) _assetEditFunction.applyAsset(this);
            }
        }
        else {
            super.drawThumbnailImage();
        }
    }

    @Override
    public void drawDetailedContent() {
        super.drawDetailedContent();
        ImGui.image(getTextureID(), 400, 400, 0, 1, 1, 0);
    }
}
