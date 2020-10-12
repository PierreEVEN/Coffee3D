package Core.Resources;

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
