package coffee3D.core.renderer.scene;

import coffee3D.core.io.log.Log;

import static org.lwjgl.opengl.GL46.*;

public class Framebuffer {

    private int _framebufferId = -1;
    private int _renderBufferObject = -1;
    private int _textureColorBuffer = -1;

    private int _drawOffsetX, _drawOffsetY;
    private int _fbWidth, _fbHeight;

    private void createFramebuffer() {
        if (_framebufferId == -1) _framebufferId = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, _framebufferId);

        // create a color attachment texture
        if (_textureColorBuffer == -1) _textureColorBuffer = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, _textureColorBuffer);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, _fbWidth, _fbHeight, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, _textureColorBuffer, 0);
        // create a renderbuffer object for depth and stencil attachment (we won't be sampling these)
        if (_renderBufferObject == -1) _renderBufferObject = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, _renderBufferObject);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, _fbWidth, _fbHeight); // use a single renderbuffer object for both a depth AND stencil buffer.
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, _renderBufferObject); // now actually attach it
        // now that we actually created the framebuffer and added all attachments we want to check if it is actually complete now
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
        {
            Log.Warning("failed to create frameBuffer");
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public Framebuffer(int width, int height) {
        _fbWidth = width;
        _fbHeight = height;
        _drawOffsetX = 0;
        _drawOffsetY = 0;
        createFramebuffer();
    }

    public void resizeFramebuffer(int width, int height) {
        _fbWidth = width;
        _fbHeight = height;
        createFramebuffer();
    }

    public int getBufferId() {
        return _framebufferId;
    }

    public int getColorBuffer() { return _textureColorBuffer; }

    public void updateDrawPosition(int x, int y) {
        _drawOffsetX = x;
        _drawOffsetY = y;
    }

    public int getDrawOffsetX() { return _drawOffsetX; }
    public int getDrawOffsetY() { return _drawOffsetY; }

    public int getWidth() { return _fbWidth; }
    public int getHeight() { return _fbHeight; }
}
