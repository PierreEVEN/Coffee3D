package coffee3D.core.resources.types;

import coffee3D.core.resources.GraphicResource;

/**
 * Texture resource base class
 */
public abstract class TextureResource extends GraphicResource {

    protected int _textureHandle;
    protected boolean _linearTextureFilter = true;

    protected TextureResource(String resourceName, boolean bLinearFilter) {
        super(resourceName);
        _linearTextureFilter = bLinearFilter;
    }

    /**
     * get texture id
     * @return texture id
     */
    public int getTextureHandle() { return _textureHandle; }
}
