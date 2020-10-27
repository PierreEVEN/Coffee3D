package Core.Renderer.Scene;

import Core.IO.LogOutput.Log;
import Core.IO.Settings.EngineSettings;
import Core.Maths.MathLibrary;
import Core.Renderer.RenderUtils;
import Core.Renderer.Scene.Components.Camera;
import Core.Renderer.Window;
import Core.Types.Color;
import Core.Types.TypeHelper;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

class RenderSceneProperties extends SceneProperty {
    private static final long serialVersionUID = -7591302437531604737L;
    protected Color _backgroundColor = new Color(0,0,0,1);
}


public class RenderScene extends Scene {

    private final Framebuffer _sceneBuffer;
    private final Framebuffer _pickBuffer;
    private final SceneStaticBuffer _sceneUbo;
    private final transient Camera _camera;
    private final ByteBuffer _pickOutputBuffer;
    private SceneComponent lastHitComponent = null;

    public RenderScene(boolean fullscreen) {
        super();
        _sceneBuffer = fullscreen ? null : new Framebuffer(100, 100);
        _pickBuffer = new Framebuffer(1, 1);
        _sceneProperties = new RenderSceneProperties();
        _sceneUbo = new SceneStaticBuffer();
        _sceneUbo.load();
        _camera = new Camera();
        _pickOutputBuffer = BufferUtils.createByteBuffer(3);
    }

    @Override
    public void renderScene() {
        // Bind and reset scene frame buffer
        if (_sceneBuffer == null) glBindFramebuffer(GL_FRAMEBUFFER, 0);
        else glBindFramebuffer(GL_FRAMEBUFFER, _sceneBuffer.getBufferId());
        Vector4f bgColor = ((RenderSceneProperties)_sceneProperties)._backgroundColor.getVector();
        float power = ((RenderSceneProperties)_sceneProperties)._backgroundColor.getPower();
        if (EngineSettings.TRANSPARENT_FRAMEBUFFER) glClearColor(0,0,0,0);
        else glClearColor(bgColor.x * power, bgColor.y * power, bgColor.z * power, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_MULTISAMPLE);
        glEnable(GL_STENCIL_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_FRONT);
        glFrontFace(GL_CW);
        glViewport(0, 0, getFbWidth(), getFbHeight());

        //Update static buffer
        _sceneUbo.use(getFbWidth(), getFbHeight(), getCamera());

        // render scene content
        super.renderScene();

        // Bind default buffer
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        updatePickBuffer();
    }

    public Camera getCamera() {
        return _camera;
    }

    public void updatePickBuffer() {
        glBindFramebuffer(GL_FRAMEBUFFER, _pickBuffer.getBufferId());
        glClearColor(0,0,0,0);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_MULTISAMPLE);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_FRONT);
        glFrontFace(GL_CW);
        glViewport(0, 0, _pickBuffer.getWidth(), _pickBuffer.getHeight());

        RenderUtils.RENDER_MODE = GL_SELECT;

        Vector3f dir = TypeHelper.getVector3();
        getCursorSceneDirection(dir);

        Vector3f camWorldPosition = getCamera().getWorldPosition();

        _sceneUbo.use(_pickBuffer.getWidth(), _pickBuffer.getHeight(), getCamera(),
                TypeHelper.getMat4().identity().lookAt(camWorldPosition, TypeHelper.getVector3(camWorldPosition).add(dir), getCamera().getUpVector()));

        super.renderScene();

        glReadPixels(0, 0, 1, 1,  GL_RGB, GL_UNSIGNED_BYTE, _pickOutputBuffer);

        int hitCompId = (_pickOutputBuffer.get(0) & 0xff) + ((_pickOutputBuffer.get(1) & 0xff) << 8) + ((_pickOutputBuffer.get(2) & 0xff) << 16);
        if (hitCompId > 0) {
            lastHitComponent = getComponents().get(hitCompId - 1);
        }
        else {
            lastHitComponent = null;
        }

        RenderUtils.RENDER_MODE = GL_RENDER;
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public Framebuffer getFramebuffer() { return _sceneBuffer; }
    public Framebuffer getPickBuffer() { return _pickBuffer; }

    public int getFbWidth() { return _sceneBuffer == null ? Window.GetPrimaryWindow().getPixelWidth() : _sceneBuffer.getWidth(); }
    public int getFbHeight() { return _sceneBuffer == null ? Window.GetPrimaryWindow().getPixelHeight() : _sceneBuffer.getHeight(); }

    public float getCursorPosX() {
        return (float) (_sceneBuffer == null ? getGamemode().getController().getCursorPosX() : getGamemode().getController().getCursorPosX() - _sceneBuffer.getDrawOffsetX());
    }

    public float getCursorPosY() {
        return (float) (_sceneBuffer == null ? getGamemode().getController().getCursorPosY() : getGamemode().getController().getCursorPosY() - _sceneBuffer.getDrawOffsetY());
    }

    public void getCursorSceneDirection(Vector3f result) {
        MathLibrary.PixelToSceneDirection(this, getCursorPosX(), getCursorPosY(), result);
    }
}
