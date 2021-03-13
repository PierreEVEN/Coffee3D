package coffee3D.core.assets.types;

import coffee3D.core.assets.Asset;
import coffee3D.core.assets.AssetReference;
import coffee3D.core.renderer.RenderUtils;
import coffee3D.core.renderer.scene.RenderScene;
import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.resources.types.MaterialResource;
import coffee3D.core.types.Color;
import coffee3D.core.renderer.scene.ThumbnailScene;
import org.joml.Vector2i;

import java.io.File;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

/**
 * usage :
 * 1) Set defaults (color + textures)
 * 2) call use(context);
 * 3) set additional values
 */
public abstract class MaterialInterface extends Asset {
    private static final long serialVersionUID = -167314150263644016L;

    protected AssetReference<Texture2D>[] _textures;
    protected Color _materialColor;
    protected float _uvScale;


    protected MaterialInterface(String name, File sourcePath, File assetPath, AssetReference<Texture2D>[] textures) {
        super(name, sourcePath, assetPath);
        _materialColor = new Color(1, 1, 1, 1);
        _uvScale = 1f;
        _textures = textures;
    }

    @Override
    public void use(Scene context) {
        useInternal(context);
        bindColor(null);
        bindTextures(null);
    }

    public final void useInternal(Scene context) {
        getResource().use(context);
        bindShadowMaps(context);
    }


    public abstract MaterialResource getResource();

    public void bindColor(Color colorOverride) {
        getResource().setColorParameter("color", colorOverride == null ? _materialColor : colorOverride);
    }

    private void bindShadowMaps(Scene context) {
        if (((RenderScene)context).getShadowBuffer() == null) return;
        getResource().setIntParameter("shadowMap", 0);
        RenderUtils.ActivateTexture(0);
        glBindTexture(GL_TEXTURE_2D, ((RenderScene)context).getShadowBuffer().getDepthTexture());
    }

    public void bindTextures(AssetReference<Texture2D>[] textureOverride) {
        if (_textures == null) return;
        for (int i = 0; i < _textures.length; ++i) {

            Texture2D usedTexture = _textures[i].get();
            if (usedTexture == null && textureOverride != null) usedTexture = textureOverride[i].get();
            if (usedTexture != null)
            {
                getResource().setIntParameter("texture" + i, i + 1);
                RenderUtils.ActivateTexture(i + 1);
                glBindTexture(GL_TEXTURE_2D, usedTexture.getTextureID());
            }
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

    @Override
    public int getThumbnailSourceBuffer(Vector2i textureSize) {
        textureSize.set(ThumbnailScene.Get().getFbWidth(), ThumbnailScene.Get().getFbHeight());
        ThumbnailScene.Get().use(this, null);
        return ThumbnailScene.Get().getPostProcessBuffer().getFrameBuffer();
    }
}
