package coffee3D.core.renderer.scene;

import coffee3D.core.assets.AssetManager;
import coffee3D.core.assets.types.Material;
import coffee3D.core.assets.types.StaticMesh;
import coffee3D.core.io.settings.EngineSettings;
import coffee3D.core.maths.MathLibrary;
import coffee3D.core.renderer.RenderMode;
import coffee3D.core.renderer.RenderUtils;
import coffee3D.core.renderer.scene.Components.Camera;
import coffee3D.core.renderer.Window;
import coffee3D.core.renderer.scene.Components.StaticMeshComponent;
import coffee3D.core.resources.factories.MeshFactory;
import coffee3D.core.resources.types.Framebuffer;
import coffee3D.core.resources.types.MeshResource;
import coffee3D.core.resources.types.SceneUniformBuffer;
import coffee3D.core.types.TypeHelper;
import coffee3D.core.types.Vertex;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class RenderScene extends Scene {

    // BUFFERS
    private final Framebuffer _colorBuffer;
    private final Framebuffer _pickBuffer;
    private final Framebuffer _postProcessBuffer;
    private final Framebuffer _shadowBuffer;
    private final Framebuffer _stencilBuffer;
    private static SceneUniformBuffer _sceneUbo;

    private final Camera _camera;
    private final Matrix4f _shadowMatrix = new Matrix4f();
    private final DrawList _drawList = new DrawList();
    private final HitResult _lastHit = new HitResult();
    private static MeshResource _viewportQuadMesh;
    public final RenderSceneSettings _sceneSettings;
    private boolean freezeFrustum = false;
    private final StaticMeshComponent skyBoxMesh;
    private float _shadowRange;

    public void freezeFrustum(boolean bFreeze) { freezeFrustum = bFreeze; }
    public boolean isFrustumFrozen() { return freezeFrustum; }

    public RenderScene(RenderSceneSettings settings) {
        super();
        _sceneSettings = settings;
        _sceneProperties = new RenderSceneProperties();
        _shadowRange = 200;

        // Buffers
        _colorBuffer = _sceneSettings.hasColorBuffer() ? new Framebuffer("colorBuffer_" + TypeHelper.MakeGlobalUid(), 0, 0, true, true) : null;
        _postProcessBuffer = _sceneSettings.hasPostProcessBuffer() ? new Framebuffer("postProcessBuffer_" + TypeHelper.MakeGlobalUid(), 0,0, true, false) : null;
        _shadowBuffer = _sceneSettings.hasShadowBuffer() ? new Framebuffer("shadowBuffer_" + TypeHelper.MakeGlobalUid(), EngineSettings.Get().shadowResolution, EngineSettings.Get().shadowResolution * 2, false, true) : null;
        _pickBuffer = _sceneSettings.hasPickBuffer() ? new Framebuffer("pickBuffer_" + TypeHelper.MakeGlobalUid(), 1, 1, true, true) : null;
        _stencilBuffer = _sceneSettings.hasStencilBuffer() ? new Framebuffer("stencilBuffer_" + TypeHelper.MakeGlobalUid(), 0, 0, true, true) : null;
        if (_sceneUbo == null) {
            _sceneUbo = new SceneUniformBuffer("SceneBuffer_" + TypeHelper.MakeGlobalUid());
            _sceneUbo.load();
        }
        _camera = new Camera();

        if (_viewportQuadMesh == null) {
            Vertex[] vertices = new Vertex[]{
                    new Vertex(new Vector3f(-1, -1, 0), new Vector2f(0, 0)),
                    new Vertex(new Vector3f(1, -1, 0), new Vector2f(1, 0)),
                    new Vertex(new Vector3f(1, 1, 0), new Vector2f(1, 1)),
                    new Vertex(new Vector3f(-1, 1, 0), new Vector2f(0, 1)),
            };
            int[] triangles = new int[]{0, 1, 2, 0, 2, 3};
            _viewportQuadMesh = MeshFactory.FromResources("screenQuadMesh", vertices, triangles);
        }

        StaticMesh sphereMesh = AssetManager.FindAsset("default_sphere");
        if (sphereMesh != null) skyBoxMesh = new StaticMeshComponent(sphereMesh, new Vector3f().zero(), new Quaternionf().identity(), new Vector3f(-10));
        else skyBoxMesh = null;
    }

    public RenderSceneSettings getSettings() {
        return _sceneSettings;
    }

    public void drawAllComponents() {
        for (SceneComponent component : getComponents()) component.drawInternal(this);
    }

    public SceneUniformBuffer getSceneUbo() { return _sceneUbo; }

    public boolean renderScene() {

        double deltaTime = Window.GetPrimaryWindow().getDeltaTime();
        for (SceneComponent component : getComponents()) {
            component.tickInternal(this, deltaTime);
        }

        if (_sceneSettings.hasFullScreenColorBuffer()) resizeBuffers(Window.GetPrimaryWindow().getPixelWidth(), Window.GetPrimaryWindow().getPixelHeight());
        if (getFbWidth() <= 0 || getFbHeight() <= 0) return false;

        // QUERY RENDERED COMPONENTS
        if (!isFrustumFrozen()) _drawList.build(getComponents(), getCamera().getViewMatrix(), getProjection(getFbWidth(), getFbHeight(), getCamera()));

        // CONTEXT INITIALIZATION
        _sceneUbo.use(this, getFbWidth(), getFbHeight(), getCamera());
        _camera.drawInternal(this);

        // SHADOW RENDERING
        if (_sceneSettings.enableShadows()) {
            RenderUtils.RENDER_MODE = RenderMode.Shadow;
            _shadowBuffer.use(true, null);
            glEnable(GL_CULL_FACE);
            updateLightMatrix();
            drawAllComponents();
        }

        // DRAW STENCIL BUFFER
        if (_sceneSettings.enableStencil()) {
            RenderUtils.RENDER_MODE = RenderMode.Stencil;
            _stencilBuffer.resizeFramebuffer(getFbWidth(), getFbHeight());
            _stencilBuffer.use(true,null);
            glEnable(GL_DEPTH_TEST);
            glDisable(GL_CULL_FACE);
            glCullFace(GL_FRONT);
            glFrontFace(GL_CW);
            _drawList.preRender(this);
            glClear(GL_DEPTH_BUFFER_BIT);
            _drawList.render(this);
            glClear(GL_DEPTH_BUFFER_BIT);
            _drawList.postRender(this);
        }
        else if (_sceneSettings.hasStencilBuffer()) _stencilBuffer.use(this);

        // COLOR RENDERING
        RenderUtils.RENDER_MODE = RenderMode.Color;
        if (_sceneSettings.enablePostProcess()) _colorBuffer.use(true, EngineSettings.Get().transparentFramebuffer ? null : ((RenderSceneProperties) _sceneProperties)._backgroundColor);
        else Framebuffer.BindBackBuffer(EngineSettings.Get().transparentFramebuffer ? null : ((RenderSceneProperties) _sceneProperties)._backgroundColor);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST);
        glEnable(GL_MULTISAMPLE);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_FRONT);
        glFrontFace(GL_CW);
        glPolygonMode(GL_FRONT_AND_BACK, Window.GetPrimaryWindow().getDrawMode());
        preDraw();
        glClear(GL_DEPTH_BUFFER_BIT);
        _drawList.render(this);
        glClear(GL_DEPTH_BUFFER_BIT);
        postDraw();
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        // POST PROCESS RENDERING
        if (_sceneSettings.enablePostProcess()) {
            if (_sceneSettings.hasPostProcessBuffer()) _postProcessBuffer.use(true,null);
            else Framebuffer.BindBackBuffer(null);

            glDisable(GL_CULL_FACE);
            RenderUtils.getPostProcessMaterial().use(this);
            RenderUtils.getPostProcessMaterial().getResource().setIntParameter("colorTexture", 0);
            RenderUtils.ActivateTexture(0);
            glBindTexture(GL_TEXTURE_2D, _colorBuffer.getColorTexture());
            RenderUtils.getPostProcessMaterial().getResource().setIntParameter("depthTexture", 1);
            RenderUtils.ActivateTexture(1);
            glBindTexture(GL_TEXTURE_2D, _colorBuffer.getDepthTexture());
            if (_sceneSettings.hasStencilBuffer()) {
                RenderUtils.getPostProcessMaterial().getResource().setIntParameter("stencilTexture", 2);
                RenderUtils.ActivateTexture(2);
                glBindTexture(GL_TEXTURE_2D, _stencilBuffer.getColorTexture());
            }
            _viewportQuadMesh.use(this);
        }

        // PICK BUFFER RENDERING
        if (_sceneSettings.isPickCursorEveryFrames()) {
            RenderUtils.RENDER_MODE = RenderMode.Select;
            _pickBuffer.use(true, null);
            glEnable(GL_DEPTH_TEST);
            glDisable(GL_MULTISAMPLE);
            glEnable(GL_CULL_FACE);
            glCullFace(GL_FRONT);
            glFrontFace(GL_CW);
            _lastHit.update(this, _pickBuffer);
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        return true;
    }

    public void preDraw() {
        if (getSkyboxMaterial() != null && skyBoxMesh != null) {
            skyBoxMesh.setMaterial(getSkyboxMaterial(), 0);
            skyBoxMesh.drawInternal(this);
        }
        _drawList.preRender(this);
    }

    public void postDraw() {
        _drawList.postRender(this);
    }

    public void resizeBuffers(int sizeX, int sizeY) {
        if (_colorBuffer != null) _colorBuffer.resizeFramebuffer(sizeX, sizeY);
        if (_stencilBuffer != null) _stencilBuffer.resizeFramebuffer(sizeX, sizeY);
        if (_postProcessBuffer != null) _postProcessBuffer.resizeFramebuffer(sizeX, sizeY);
    }


    public float getShadowRange() {
        return _shadowRange;
    }

    public void setShadowRange(float range) {
        _shadowRange = range;
    }

    public int getShadowResolution() {
        return _shadowBuffer.getHeight();
    }

    public void setShadowResolution(int resolution) {
        EngineSettings.Get().shadowResolution = resolution;
        _shadowBuffer.resizeFramebuffer(resolution, resolution * 2);
    }

    public void updateLightMatrix() {
        float near_plane = 20;
        float shadowRadius = _shadowRange / 2;

        Matrix4f lightProjection = TypeHelper.getMat4().ortho(-shadowRadius * .5f, shadowRadius * .5f, -shadowRadius, shadowRadius, near_plane, _shadowRange);

        Vector3f lightDirection = ((RenderSceneProperties)_sceneProperties).getSunVector();

        Matrix4f lightView = TypeHelper.getMat4().lookAt(
                TypeHelper.getVector3(lightDirection).mul(_shadowRange / 2).add(getCamera().getWorldPosition()),
                TypeHelper.getVector3(0, 0, 0).add(getCamera().getWorldPosition()),
                TypeHelper.getVector3(0, 0, 1));

        _shadowMatrix.set(lightProjection.mul(lightView));

        RenderUtils.getShadowDrawList()[0].use(this);
        RenderUtils.getShadowDrawList()[0].getResource().setMatrixParameter("lightSpaceMatrix", _shadowMatrix);
    }

    public Matrix4f getShadowMatrix() { return _shadowMatrix; }

    public HitResult getHitResult() { return _lastHit; }

    public Camera getCamera() { return _camera; }

    public Framebuffer getColorFrameBuffer() { return _colorBuffer; }
    public Framebuffer getPostProcessBuffer() { return _postProcessBuffer; }
    public Framebuffer getShadowBuffer() { return _shadowBuffer; }
    public Framebuffer getPickBuffer() { return _pickBuffer; }
    public Framebuffer getStencilBuffer() { return _stencilBuffer; }


    public int getFbWidth() { return _colorBuffer == null ? Window.GetPrimaryWindow().getPixelWidth() : _colorBuffer.getWidth(); }
    public int getFbHeight() { return _colorBuffer == null ? Window.GetPrimaryWindow().getPixelHeight() : _colorBuffer.getHeight(); }

    public float getCursorPosX() {
        return (float) (_colorBuffer == null ? Window.GetPrimaryWindow().getCursorPosX() : Window.GetPrimaryWindow().getCursorPosX() - _colorBuffer.getDrawOffsetX());
    }

    public float getCursorPosY() {
        return (float) (_colorBuffer == null ? Window.GetPrimaryWindow().getCursorPosY() : Window.GetPrimaryWindow().getCursorPosY() - _colorBuffer.getDrawOffsetY());
    }

    public void getCursorSceneDirection(Vector3f result) {
        MathLibrary.PixelToSceneDirection(this, getCursorPosX(), getCursorPosY(), result);
    }

    public Material getSkyboxMaterial() { return AssetManager.FindAsset("skyboxMaterial"); }

    public void setResolution(int width, int height) {
        if (_colorBuffer != null) _colorBuffer.resizeFramebuffer(width, height);
        if (_postProcessBuffer != null) _postProcessBuffer.resizeFramebuffer(width, height);
    }

    public void setPosition(int x, int y) {
        if (_colorBuffer != null) _colorBuffer.updateDrawOffset(x, y);
        if (_postProcessBuffer != null) _postProcessBuffer.updateDrawOffset(x, y);
    }

    @Override
    public void delete() {
        super.delete();
        if (_colorBuffer != null) _colorBuffer.delete();
        if (_pickBuffer != null) _pickBuffer.delete();
        if (_postProcessBuffer != null) _postProcessBuffer.delete();
        if (_shadowBuffer != null) _shadowBuffer.delete();
        if (_stencilBuffer != null) _stencilBuffer.delete();
    }
}
