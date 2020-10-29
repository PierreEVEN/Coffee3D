package coffee3D.core.resources.types;

import coffee3D.core.resources.GraphicResource;

/**
 * Texture resource base class
 */
public abstract class TextureResource extends GraphicResource {

    protected int _textureHandle;

    protected TextureResource(String resourceName) {
        super(resourceName);
    }

    /**
     * get texture id
     * @return texture id
     */
    public int getTextureHandle() { return _textureHandle; }
}
