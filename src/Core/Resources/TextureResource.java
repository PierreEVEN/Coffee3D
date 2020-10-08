package Core.Resources;

import java.nio.IntBuffer;

public abstract class TextureResource extends GraphicResource {

    protected int _textureHandle;

    protected TextureResource(String resourceName) {
        super(resourceName);
    }

    public int getTextureHandle() { return _textureHandle; }
}
