package coffee3D.core.resources.types;

import coffee3D.core.io.log.Log;
import coffee3D.core.renderer.RenderUtils;
import coffee3D.core.renderer.Window;
import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.resources.GraphicResource;
import coffee3D.core.types.Color;
import org.joml.Vector4f;
import static org.lwjgl.opengl.GL46.*;

public class Framebuffer extends GraphicResource {

    private final boolean _bEnableColorBuffer;
    private final boolean _bEnableDepthBuffer;
    private final int _framebuffer;
    private final int _colorTexture;
    private final int _depthTexture;
    private int _drawOffsetX, _drawOffsetY;
    private int _fbWidth, _fbHeight;
    private final static float[] borderColor = {1.0f, 1.0f, 1.0f, 1.0f};

    public Framebuffer(String resourceName, int width, int height, boolean colorBuffer, boolean depthBuffer) {
        super(resourceName);
        _fbWidth = width;
        _fbHeight = height;
        _drawOffsetX = 0;
        _drawOffsetY = 0;

        _bEnableColorBuffer = colorBuffer;
        _bEnableDepthBuffer = depthBuffer;

        _framebuffer = glGenFramebuffers();
        if (colorBuffer) _colorTexture = glGenTextures();
        else _colorTexture = -1;
        if (_bEnableDepthBuffer) _depthTexture = glGenTextures();
        else _depthTexture = -1;

        load();
    }

    public int getFrameBuffer() { return _framebuffer; }
    public int getColorTexture() { return _colorTexture; }
    public int getDepthTexture() { return _depthTexture; }

    public int getWidth() { return _fbWidth; }
    public int getHeight() { return _fbHeight; }

    public int getDrawOffsetX() { return _drawOffsetX; }
    public int getDrawOffsetY() { return _drawOffsetY; }

    public void updateDrawOffset(int x, int y) {
        _drawOffsetX = x;
        _drawOffsetY = y;
    }

    public void resizeFramebuffer(int width, int height) {
        if (width != _fbWidth || height != _fbHeight) {
            _fbWidth = width;
            _fbHeight = height;
            load();
        }
    }

    @Override
    public void load() {
        if (_fbWidth <= 0 || _fbHeight <= 0) return;
        glBindFramebuffer(GL_FRAMEBUFFER, _framebuffer);

        // CREATE COLOR RENDER TARGET
        if (_bEnableColorBuffer) {
            glBindTexture(GL_TEXTURE_2D, _colorTexture);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, _fbWidth, _fbHeight, 0, GL_RGBA, GL_FLOAT, 0);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, _colorTexture, 0);
        }

        // CREATE DEPTH RENDER TARGET
        if (_bEnableDepthBuffer) {
            glBindTexture(GL_TEXTURE_2D, _depthTexture);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, _fbWidth, _fbHeight, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
            glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor);
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, _depthTexture, 0);
        }

        // ENSURE BUFFER IS VALID
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) Log.Warning("failed to create frameBuffer");
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        RenderUtils.CheckGLErrors();
    }

    @Override
    public void unload() {
        if (_bEnableColorBuffer) glDeleteTextures(_colorTexture);
        if (_bEnableDepthBuffer) glDeleteTextures(_depthTexture);
        glDeleteFramebuffers(_framebuffer);
    }

    @Override
    public void use(Scene context) { use(true, null); }

    public boolean use(boolean clearBuffer, Color backgroundColor) {
        if (_fbWidth <= 0 || _fbHeight <= 0) return false;
        glBindFramebuffer(GL_FRAMEBUFFER, _framebuffer);
        glViewport(0, 0, _fbWidth, _fbHeight);
        if (clearBuffer) {
            if (backgroundColor == null) glClearColor(0, 0, 0, 1);
            else {
                Vector4f bgColor = backgroundColor.getVector();
                float power = backgroundColor.getPower();
                glClearColor(bgColor.x * power, bgColor.y * power, bgColor.z * power, bgColor.w * power);
            }
            glClear((_bEnableColorBuffer ? GL_COLOR_BUFFER_BIT : 0) | (_bEnableDepthBuffer ? (GL_DEPTH_BUFFER_BIT) : 0));
        }
        RenderUtils.CheckGLErrors();
        return true;
    }

    public static boolean BindBackBuffer(Color clearColor) {
        int width = Window.GetPrimaryWindow().getPixelWidth();
        int height = Window.GetPrimaryWindow().getPixelHeight();
        if (width <= 0 || height <= 0) return false;
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, width, height);
        if (clearColor == null) glClearColor(0,0,0,1);
        else {
            Vector4f bgColor = clearColor.getVector();
            float power = clearColor.getPower();
            glClearColor(bgColor.x * power, bgColor.y * power, bgColor.z * power, bgColor.w * power);
        }
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        return true;
    }

}
