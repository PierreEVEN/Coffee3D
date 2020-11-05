package coffee3D.core.renderer.scene;

import coffee3D.core.io.settings.EngineSettings;
import coffee3D.core.maths.MathLibrary;
import coffee3D.core.renderer.RenderMode;
import coffee3D.core.renderer.RenderUtils;
import coffee3D.core.renderer.scene.Components.Camera;
import coffee3D.core.renderer.Window;
import coffee3D.core.resources.factories.MeshFactory;
import coffee3D.core.resources.types.MeshResource;
import coffee3D.core.types.TypeHelper;
import coffee3D.core.types.Vertex;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class RenderScene extends Scene {

    private final Framebuffer _sceneBuffer;
    private final Framebuffer _pickBuffer;
    private final Framebuffer _postProcessBuffer;
    private final Framebuffer _shadowBuffer;
    private final SceneStaticBuffer _sceneUbo;
    private final Camera _camera;
    private final ByteBuffer _pickOutputBuffer;
    private SceneComponent _lastHitComponent = null;
    private final Matrix4f lightSpaceMatrix = new Matrix4f().identity();
    private final FrustumIntersection _frustum = new FrustumIntersection();
    private final LinkedList<SceneComponent> frustumDrawList = new LinkedList<>();


    private boolean enablePicking;
    private boolean enableShadows;
    private boolean enablePostProcess;
    private boolean bFullScreen;
    private boolean freezeFrustum = false;

    MeshResource test;

    public void freezeFrustum(boolean bFreeze) { freezeFrustum = bFreeze; }
    public boolean isFrustumFrozen() { return freezeFrustum; }

    public RenderScene(boolean fullscreen) {
        super();


        enablePicking = EngineSettings.ENABLE_PICKING;
        enableShadows = EngineSettings.ENABLE_SHADOWS;
        enablePostProcess = EngineSettings.ENABLE_POSTPROCESSING;
        bFullScreen = fullscreen;

        // Buffers
        _sceneBuffer = !fullscreen || enablePostProcess ? new Framebuffer(0, 0, true, true) : null;
        _postProcessBuffer = !fullscreen && enablePostProcess ? new Framebuffer(0,0, true, false) : null;
        _shadowBuffer = enableShadows ? new Framebuffer(4096, 4096, false, true) : null;
        _pickBuffer = enablePicking ? new Framebuffer(1, 1, true, true) : null;

        _sceneProperties = new RenderSceneProperties();
        _sceneUbo = new SceneStaticBuffer();
        _sceneUbo.load();
        _camera = new Camera();
        _pickOutputBuffer = BufferUtils.createByteBuffer(3);


        Vertex[] vertices = new Vertex[] {
                new Vertex(new Vector3f(-1, -1, 0), new Vector2f(0, 0)),
                new Vertex(new Vector3f(1, -1, 0), new Vector2f(1, 0)),
                new Vertex(new Vector3f(1, 1, 0), new Vector2f(1, 1)),
                new Vertex(new Vector3f(-1, 1, 0), new Vector2f(0, 1)),
        };
        int[] triangles = new int[] { 0, 1, 2, 0, 2, 3 };
        test = MeshFactory.FromResources("screenQuadMesh" + truc++, vertices, triangles);
    }
    private static int truc = 0;


    public void buildFrustumList() {
        if (freezeFrustum) return;
        frustumDrawList.clear();

        _frustum.set(getProjection(getFbWidth(), getFbHeight(), getCamera()).mul(getCamera().getViewMatrix()));
        ArrayList<SceneComponent> components = getComponents();
        for (int i = 0; i < components.size(); ++i) {
            SceneComponent component = components.get(i);
            component.setComponentIndex(i);
            if (_frustum.testSphere(component.getBound().position, component.getBound().radius)) {
                frustumDrawList.add(component);
            }
        }
    }


    public void drawAllComponents() {
        for (SceneComponent component : getComponents()) component.drawInternal(this);
    }

    public void drawFrustrumComponents() {
        if (RenderUtils.RENDER_MODE == RenderMode.Select) {
            for (SceneComponent component : frustumDrawList) {
                RenderUtils.getPickMaterialDrawList()[0].use(this);
                RenderUtils.getPickMaterialDrawList()[0].getResource().setIntParameter("pickId", component.getComponentIndex() + 1);
                RenderUtils.CheckGLErrors();
                component.drawInternal(this);
            }
        }
        else {
            for (SceneComponent component : frustumDrawList) component.drawInternal(this);
        }
    }


    public void renderScene() {
        if (bFullScreen && _sceneBuffer != null) _sceneBuffer.resizeFramebuffer(Window.GetPrimaryWindow().getPixelWidth(), Window.GetPrimaryWindow().getPixelHeight());
        if (getFbWidth() <= 0 || getFbHeight() <= 0) return;

        // QUERY RENDERED COMPONENTS
        buildFrustumList();

        // CONTEXT INITIALIZATION
        _sceneUbo.use(this, getFbWidth(), getFbHeight(), getCamera());
        _camera.draw(this);

        // SHADOW RENDERING
        if (enableShadows) {
            RenderUtils.RENDER_MODE = RenderMode.Shadow;
            _shadowBuffer.bindAndReset(null);
            glEnable(GL_CULL_FACE);
            //glCullFace(GL_BACK);
            updateLightMatrix();
            drawAllComponents();
        }

        // COLOR RENDERING
        RenderUtils.RENDER_MODE = RenderMode.Color;

        if (bFullScreen && !enablePostProcess) {
            Framebuffer.BindBackBuffer(EngineSettings.TRANSPARENT_FRAMEBUFFER ? null : ((RenderSceneProperties) _sceneProperties)._backgroundColor);
        }
        else {
            _sceneBuffer.bindAndReset(EngineSettings.TRANSPARENT_FRAMEBUFFER ? null : ((RenderSceneProperties) _sceneProperties)._backgroundColor);
        }
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_MULTISAMPLE);
        glEnable(GL_STENCIL_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_FRONT);
        glFrontFace(GL_CW);
        glPolygonMode( GL_FRONT_AND_BACK, Window.GetPrimaryWindow().getDrawMode());
        drawFrustrumComponents();
        glPolygonMode( GL_FRONT_AND_BACK, GL_FILL);

        // POST PROCESS RENDERING
        if (enablePostProcess && (bFullScreen ? Framebuffer.BindBackBuffer(null) : _postProcessBuffer.bindAndReset(null))) {
            RenderUtils.getPostProcessMaterial().use(this);
            RenderUtils.getPostProcessMaterial().getResource().setIntParameter("colorTexture", 0);
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, _sceneBuffer.getColorTexture());
            RenderUtils.getPostProcessMaterial().getResource().setIntParameter("depthTexture", 1);
            glActiveTexture(GL_TEXTURE0 + 1);
            glBindTexture(GL_TEXTURE_2D, _sceneBuffer.getDepthStencilTexture());
            test.use(this);
            RenderUtils.CheckGLErrors();
        }

        // PICK BUFFER RENDERING
        if (enablePicking) {
            RenderUtils.RENDER_MODE = RenderMode.Select;
            _pickBuffer.bindAndReset(null);
            glEnable(GL_DEPTH_TEST);
            glDisable(GL_MULTISAMPLE);
            glEnable(GL_CULL_FACE);
            glCullFace(GL_FRONT);
            glFrontFace(GL_CW);
            drawPickBuffer();
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void updateLightMatrix() {
        float near_plane = 1f, far_plane = 100;
        float shadowRadius = far_plane / 2;

        Matrix4f lightProjection = TypeHelper.getMat4().ortho(-shadowRadius, shadowRadius, -shadowRadius, shadowRadius, near_plane, far_plane);

        Vector3f lightDirection = ((RenderSceneProperties)_sceneProperties).getSunVector();

        Matrix4f lightView = TypeHelper.getMat4().lookAt(
                TypeHelper.getVector3(lightDirection).mul(far_plane / 2),
                TypeHelper.getVector3(0, 0, 0),
                TypeHelper.getVector3(0, 0, 1));

        lightSpaceMatrix.set(lightProjection.mul(lightView));

        RenderUtils.getShadowDrawList()[0].use(this);
        RenderUtils.getShadowDrawList()[0].getResource().setMatrixParameter("lightSpaceMatrix", lightSpaceMatrix);
    }

    public Matrix4f getLightSpaceMatrix() { return lightSpaceMatrix; }

    public boolean isFullscreen() { return bFullScreen; }

    public SceneComponent getLastHitComponent() { return _lastHitComponent; }

    public Camera getCamera() { return _camera; }

    public void drawPickBuffer() {
        Vector3f dir = TypeHelper.getVector3();
        getCursorSceneDirection(dir);

        Vector3f camWorldPosition = getCamera().getWorldPosition();

        _sceneUbo.use(this, _pickBuffer.getWidth(), _pickBuffer.getHeight(), getCamera(),
                TypeHelper.getMat4().identity().lookAt(camWorldPosition, TypeHelper.getVector3(camWorldPosition).add(dir), getCamera().getUpVector()));

        RenderUtils.CheckGLErrors();
        drawFrustrumComponents();
        RenderUtils.CheckGLErrors();

        glReadPixels(0, 0, 1, 1,  GL_RGB, GL_UNSIGNED_BYTE, _pickOutputBuffer);

        int hitCompId = (_pickOutputBuffer.get(0) & 0xff) + ((_pickOutputBuffer.get(1) & 0xff) << 8) + ((_pickOutputBuffer.get(2) & 0xff) << 16);
        if (hitCompId > 0 && getComponents().size() >= hitCompId) {
            _lastHitComponent = getComponents().get(hitCompId - 1);
        }
        else {
            _lastHitComponent = null;
        }

        RenderUtils.RENDER_MODE = RenderMode.Color;
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }


    public Framebuffer getColorFrameBuffer() { return _sceneBuffer; }
    public Framebuffer getPostProcessBuffer() { return _postProcessBuffer; }
    public Framebuffer getShadowBuffer() { return _shadowBuffer; }
    public Framebuffer getPickBuffer() { return _pickBuffer; }

    public int getFbWidth() { return _sceneBuffer == null ? Window.GetPrimaryWindow().getPixelWidth() : _sceneBuffer.getWidth(); }
    public int getFbHeight() { return _sceneBuffer == null ? Window.GetPrimaryWindow().getPixelHeight() : _sceneBuffer.getHeight(); }

    public float getCursorPosX() {
        return (float) (_sceneBuffer == null ? Window.GetPrimaryWindow().getCursorPosX() : Window.GetPrimaryWindow().getCursorPosX() - _sceneBuffer.getDrawOffsetX());
    }

    public float getCursorPosY() {
        return (float) (_sceneBuffer == null ? Window.GetPrimaryWindow().getCursorPosY() : Window.GetPrimaryWindow().getCursorPosY() - _sceneBuffer.getDrawOffsetY());
    }

    public void getCursorSceneDirection(Vector3f result) {
        MathLibrary.PixelToSceneDirection(this, getCursorPosX(), getCursorPosY(), result);
    }

    public void setResolution(int width, int height) {
        if (_sceneBuffer != null) _sceneBuffer.resizeFramebuffer(width, height);
        if (_postProcessBuffer != null) _postProcessBuffer.resizeFramebuffer(width, height);
    }

    public void setPosition(int x, int y) {
        if (_sceneBuffer != null) _sceneBuffer.updateDrawPosition(x, y);
        if (_postProcessBuffer != null) _postProcessBuffer.updateDrawPosition(x, y);
    }

}
