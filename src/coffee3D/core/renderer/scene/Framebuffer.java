package coffee3D.core.renderer.scene;

import coffee3D.core.io.log.Log;
import coffee3D.core.io.settings.EngineSettings;
import coffee3D.core.renderer.RenderUtils;
import coffee3D.core.renderer.Window;
import coffee3D.core.types.Color;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL46.*;

public class Framebuffer {

    private int _framebuffer;
    private int _colorTexture;
    private int _depthStencilTexture;

    private int _drawOffsetX, _drawOffsetY;
    private int _fbWidth, _fbHeight;

    private boolean _bEnableColorBuffer;
    private boolean _bEnableDepthStencilBuffer;

    private void createFramebuffer() {
        if (_fbWidth <= 0 || _fbHeight <= 0) return;
        if (_framebuffer == -1) _framebuffer = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, _framebuffer);

        if (_bEnableColorBuffer) {
            // create a color attachment texture
            if (_colorTexture == -1) _colorTexture = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, _colorTexture);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, _fbWidth, _fbHeight, 0, GL_RGBA, GL_FLOAT, 0);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, _colorTexture, 0);
        }
        if (_bEnableDepthStencilBuffer) {
            if (_depthStencilTexture == -1) _depthStencilTexture = glGenTextures();

            // create depth stencil color texture
            glBindTexture(GL_TEXTURE_2D, _depthStencilTexture);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, _fbWidth, _fbHeight, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
            float borderColor[] = { 1.0f, 1.0f, 1.0f, 1.0f };
            glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor);

            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, _depthStencilTexture, 0);
        }
        // now that we actually created the framebuffer and added all attachments we want to check if it is actually complete now
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) Log.Warning("failed to create frameBuffer");

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        RenderUtils.CheckGLErrors();
    }

    public Framebuffer(int width, int height, boolean colorBuffer, boolean depthStencilBuffer) {
        _fbWidth = width;
        _fbHeight = height;
        _drawOffsetX = 0;
        _drawOffsetY = 0;

        _colorTexture = -1;
        _depthStencilTexture = -1;
        _framebuffer = -1;

        _bEnableColorBuffer = colorBuffer;
        _bEnableDepthStencilBuffer = depthStencilBuffer;

        createFramebuffer();
    }

    public void resizeFramebuffer(int width, int height) {
        if (width != _fbWidth || height != _fbHeight) {
            _fbWidth = width;
            _fbHeight = height;
            createFramebuffer();
        }
    }

    public int getFramebufferId() { return _framebuffer; }
    public int getColorTexture() { return _colorTexture; }
    public int getDepthStencilTexture() { return _depthStencilTexture; }

    public void updateDrawPosition(int x, int y) {
        _drawOffsetX = x;
        _drawOffsetY = y;
    }

    public boolean bindAndReset(Color clearColor) {
        if (_fbWidth <= 0 || _fbHeight <= 0) return false;
        glBindFramebuffer(GL_FRAMEBUFFER, _framebuffer);
        glViewport(0, 0, _fbWidth, _fbHeight);
        if (clearColor == null) glClearColor(0,0,0,0);
        else {
            Vector4f bgColor = clearColor.getVector();
            float power = clearColor.getPower();
            glClearColor(bgColor.x * power, bgColor.y * power, bgColor.z * power, bgColor.w * power);
        }
        glClear((_bEnableColorBuffer ? GL_COLOR_BUFFER_BIT : 0) | (_bEnableDepthStencilBuffer ? (GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT) : 0));
        return true;
    }

    public static boolean BindBackBuffer(Color clearColor) {
        int width = Window.GetPrimaryWindow().getPixelWidth();
        int height = Window.GetPrimaryWindow().getPixelHeight();
        if (width <= 0 || height <= 0) return false;
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, width, height);
        if (clearColor == null) glClearColor(0,0,0,0);
        else {
            Vector4f bgColor = clearColor.getVector();
            float power = clearColor.getPower();
            glClearColor(bgColor.x * power, bgColor.y * power, bgColor.z * power, bgColor.w * power);
        }
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        return true;
    }

    public int getDrawOffsetX() { return _drawOffsetX; }
    public int getDrawOffsetY() { return _drawOffsetY; }

    public int getWidth() { return _fbWidth; }
    public int getHeight() { return _fbHeight; }
}
