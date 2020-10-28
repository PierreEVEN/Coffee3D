package Core.Assets.Types;

import Core.Assets.Asset;
import Core.Assets.AssetReference;
import Core.Renderer.Scene.Scene;
import Core.Resources.MaterialResource;
import Core.Types.Color;

import java.io.File;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public abstract class MaterialInterface extends Asset {
    private static final long serialVersionUID = -167314150263644016L;

    protected AssetReference<Texture2D>[] _textures;
    protected Color _materialColor;
    protected float _uvScale;


    protected MaterialInterface(String name, File sourcePath, File assetPath, AssetReference<Texture2D>[] textures) {
        super(name, sourcePath, assetPath);
        _materialColor = new Color(1f, 1f, 1f, 1f);
        _uvScale = 1f;
        _textures = textures;
    }

    @Override
    public void use(Scene context) {
        getResource().use(context);
        bindColor(null);
        bindTextures(null);
    }

    public abstract MaterialResource getResource();

    public void bindColor(Color colorOverride) {
        getResource().setColorParameter("color", colorOverride == null ? _materialColor : colorOverride);
    }

    public void bindTextures(AssetReference<Texture2D>[] textureOverride) {
        if (_textures == null) return;
        for (int i = 0; i < _textures.length; ++i) {

            Texture2D usedTexture = _textures[i].get();
            if (usedTexture == null && textureOverride != null) usedTexture = textureOverride[i].get();

            getResource().setIntParameter("texture" + i, i);
            glActiveTexture(GL_TEXTURE0 + i);
            glBindTexture(GL_TEXTURE_2D, usedTexture.getTextureID());
        }
    }

    public AssetReference<Texture2D>[] getTextures() { return _textures; }
    public AssetReference<Texture2D>[] cloneTextures() {
        AssetReference<Texture2D>[] textures = new AssetReference[_textures.length];
        for (int i = 0; i < _textures.length; ++i) {
            textures[i] = new AssetReference<>(Texture2D.class, _textures[i].get().getName());
        }
        return textures;
    }
    public Color getColor() { return _materialColor; }
    public void setColor(Color inColor) { _materialColor = inColor; }
    public float getUvScale() { return _uvScale; }
}
