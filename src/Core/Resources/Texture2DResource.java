package Core.Resources;

import Core.IO.Log;
import Core.Renderer.Scene.Scene;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;

public class Texture2DResource extends TextureResource {

    private int _width, _height;
    private int[] _data;

    public Texture2DResource(String resourceName, int[] data, int width, int height) {
        super(resourceName);
        _data = data;
        _width = width;
        _height = height;
    }

    @Override
    public void load() {
        _textureHandle = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, _textureHandle);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, _width, _height, 0, GL_RGBA, GL_UNSIGNED_BYTE, _data);
        glGenerateMipmap(GL_TEXTURE_2D);
        // Texture wrapping mode
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        // Texture filtering mode
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, new float[] {1,0,0,0});
    }

    @Override
    public void unload() {
        glDeleteTextures(_textureHandle);
    }

    public int getTextureHandle() {
        return _textureHandle;
    }

    public int getWidth() { return _width; }

    public int getHeight() { return _height; }

    @Override
    public void use(Scene context) {
        Log.Error("Textures must be linked to material resource");
    }
}
