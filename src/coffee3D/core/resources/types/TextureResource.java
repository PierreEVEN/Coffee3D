package coffee3D.core.resources.types;

import coffee3D.core.io.log.Log;
import coffee3D.core.resources.GraphicResource;

/**
 * Texture resource base class
 */
public abstract class TextureResource extends GraphicResource {

    protected int _textureHandle;
    protected final boolean _linearTextureFilter;

    protected TextureResource(String resourceName, boolean bLinearFilter) {
        super(resourceName);
        _linearTextureFilter = bLinearFilter;
    }

    /**
     * get texture id
     * @return texture id
     */
    public int getTextureHandle() { return _textureHandle; }

    public static void resizeTextureData(int[] from, int[] to, int fromX, int fromY, int toX, int toY) {
        for (int x = 0; x < toX; ++x) {
            for (int y = 0; y < toY; ++y) {
                int pxFrom = (int) (x * (fromX / (double) toX));
                int pyFrom = (int) (y * (fromY / (double) toY));
                to[(x + y * toX)] = from[(pxFrom + pyFrom * fromX)];
            }
        }
    }
}
