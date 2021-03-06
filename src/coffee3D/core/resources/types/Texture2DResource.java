package coffee3D.core.resources.types;

import coffee3D.core.io.log.Log;
import coffee3D.core.renderer.scene.Scene;

import static org.lwjgl.opengl.GL30.*;

/**
 * Simple texture 2D
 */
public class Texture2DResource extends TextureResource {

    private final int _width, _height;
    private int[] _data;

    public Texture2DResource(String resourceName, int[] data, int width, int height, boolean bLinearFilter) {
        super(resourceName, bLinearFilter);
        _data = data;
        _width = width;
        _height = height;

        _textureHandle = glGenTextures();
    }

    @Override
    public void load() {
        glBindTexture(GL_TEXTURE_2D, _textureHandle);
        // Texture wrapping mode
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        // Texture filtering mode
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, _linearTextureFilter ? GL_LINEAR_MIPMAP_LINEAR : GL_NEAREST_MIPMAP_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, _linearTextureFilter ? GL_LINEAR : GL_NEAREST);

        glTexParameteri(GL_TEXTURE_2D, GL_GENERATE_MIPMAP, GL_TRUE);

        glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, new float[] {1,0,0,0});
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, _width, _height, 0, GL_RGBA, GL_UNSIGNED_BYTE, _data);
        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, 0);

        _data = null;
    }

    @Override
    public void unload() {
        glDeleteTextures(_textureHandle);
    }

    /**
     * Texture width
     * @return width
     */
    public int getWidth() { return _width; }

    /**
     * Texture height
     * @return height
     */
    public int getHeight() { return _height; }

    /**
     * Texture2D is usually linked to a desired material
     * never call 'use' on a texture2D
     * @param context scene context
     */
    @Override
    public void use(Scene context) {
        Log.Error("a Texture2D must be linked to material resource");
    }
}
