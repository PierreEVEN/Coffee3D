package Core.Resources;

import Core.IO.Log;
import Core.Renderer.Scene.Scene;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class Texture2DResource extends GraphicResource {

    private IntBuffer _textureHandle;
    private int _width, _height;
    private ByteBuffer _data;

    public Texture2DResource(ByteBuffer data, int width, int height) {
        super();
        _data = data;
        _width = width;
        _height = height;
        Load();
    }

    @Override
    public void Load() {
        glGenTextures(_textureHandle);
        glBindTexture(GL_TEXTURE_2D, _textureHandle.get());
        // Texture wrapping mode
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        // Texture filtering mode
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, _width, _height, 0, GL_RGB, GL_UNSIGNED_BYTE, _data);
        glGenerateMipmap(GL_TEXTURE_2D);
    }

    @Override
    public void Unload() {
        glDeleteTextures(_textureHandle);
    }

    public int getTextureHandle() {
        return _textureHandle.get();
    }

    public int getWidth() { return _width; }

    public int getHeight() { return _height; }

    @Override
    public void use(Scene context) {
        Log.Error("Textures must be linked to material resource");
    }
}
