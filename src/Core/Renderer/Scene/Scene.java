package Core.Renderer.Scene;

import Core.Assets.AssetManager;
import Core.Assets.Material;
import Core.Assets.StaticMesh;
import Core.Assets.Texture2D;
import Core.IO.Log;
import Core.Renderer.Scene.Components.Camera;
import Core.Renderer.Scene.Components.StaticMeshComponent;
import Core.Renderer.Scene.Gamemode.DefaultGamemode;
import Core.Renderer.Scene.Gamemode.IGamemodeBase;
import Core.Renderer.Window;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import static org.lwjgl.opengl.GL46.*;

public class Scene implements Serializable {
    private final transient Camera _camera;
    private final transient IGamemodeBase _gameMode;
    private final Framebuffer _sceneBuffer;
    private final ArrayList<SceneComponent> _components;


    public Scene() {
        //Initialize properties
        _components = new ArrayList<>();
        _camera = new Camera(new Vector3f(0, 0, 0), new Quaternionf(0, 0, 0, 1));
        _camera.attachToScene(this);
        _gameMode = new DefaultGamemode(this);
        _sceneBuffer = new Framebuffer(Window.GetPrimaryWindow().getPixelWidth(), Window.GetPrimaryWindow().getPixelHeight());

        // Create sample assets
        new Texture2D("gridTexture", "resources/textures/defaultGrid.png");
        new Texture2D("grass", "resources/textures/grassSeamless.png");
        new Material("testMat", "resources/shaders/shader", new String[] {"gridTexture"});
        new Material("matWeird", "resources/shaders/shader", new String[] {"grass"});
        new StaticMesh("test", "resources/models/test.fbx", new String[] { "testMat" });
        new StaticMesh("cube", "resources/models/cube.fbx", new String[] { "matWeird" });

        // Create basic hierarchy
        SceneComponent root = new SceneComponent(new Vector3f(0,0,0), new Quaternionf().identity(), new Vector3f(1,1,1));
        root.attachToScene(this);
        Random rnd = new Random();
        for (int i = 0; i < 300; ++i) {
            float range = 200;
            StaticMeshComponent parent = new StaticMeshComponent(
                    AssetManager.GetAsset("test"),
                    new Vector3f(rnd.nextFloat() * range - range / 2 , rnd.nextFloat() * range - range / 2, rnd.nextFloat() * range - range / 2),
                    new Quaternionf().fromAxisAngleDeg(new Vector3f(0, 0, 1), 10),
                    new Vector3f(1, 1, 1)
            );
            parent.attachToComponent(root);

            new StaticMeshComponent(
                    AssetManager.GetAsset("cube"),
                    new Vector3f(0, 0, 2),
                    new Quaternionf().fromAxisAngleDeg(new Vector3f(1, 0, 0), 25),
                    new Vector3f(1.5f, 0.8f, 1)
            ).attachToComponent(parent);

            StaticMeshComponent subChild = new StaticMeshComponent(
                    AssetManager.GetAsset("cube"),
                    new Vector3f(0, 4, 1),
                    new Quaternionf().fromAxisAngleDeg(new Vector3f(1, 2, 0).normalize(), 8789),
                    new Vector3f(1.5f, 0.8f, 1.4f)
            );
            subChild.attachToComponent(parent);

            new StaticMeshComponent(
                    AssetManager.GetAsset("cube"),
                    new Vector3f(3, 2, 1),
                    new Quaternionf().fromAxisAngleDeg(new Vector3f(1, 2, 0).normalize(), 489),
                    new Vector3f(1.1f, 0.8f, 0.02f)
            ).attachToComponent(subChild);
        }
    }

    public void renderScene() {

        glBindBuffer(GL_FRAMEBUFFER, _sceneBuffer.getBufferId());
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Tick gameMode
        _gameMode.update(this);

        Material foundMat = AssetManager.GetAsset("testMat");
        if (foundMat != null) {
            foundMat.use(this);
            foundMat.getShader().setMatrixParameter("view", _camera.getViewMatrix());
            foundMat.getShader().setMatrixParameter("projection", getProjection());
        }
        foundMat = AssetManager.GetAsset("matWeird");
        if (foundMat != null) {
            foundMat.use(this);
            foundMat.getShader().setMatrixParameter("view", _camera.getViewMatrix());
            foundMat.getShader().setMatrixParameter("projection", getProjection());
        }

        for (SceneComponent component : _components) {
            if (!(component instanceof Camera)) component.getLocalPosition().x = (float)Math.sin(GLFW.glfwGetTime()) * 4;
            if (!(component instanceof Camera)) component.getLocalPosition().y = (float)Math.sin(GLFW.glfwGetTime() * 2) * 3;
            if (!(component instanceof Camera)) component.getLocalPosition().z = (float)Math.sin(GLFW.glfwGetTime() * 0.5f) * 2;

            if (!(component instanceof Camera)) component.getLocalScale().x = ((float)Math.sin(GLFW.glfwGetTime() * 0.5f) + 2) / 3;

            component.drawInternal(this);
        }

        glBindBuffer(GL_FRAMEBUFFER, 0);
    }

    /**
     * Make component root on this scene.
     * (don't call this method yourself, use component.attachToScene(sceneParam));
     * @param rootComponent attached component
     */
    protected void attachComponent(SceneComponent rootComponent) {
        if (rootComponent != null && !_components.contains(rootComponent)) {
            rootComponent.detach();
            _components.add(rootComponent);
        }
    }

    /**
     * Unregister component from this scene.
     * (don't call this method yourself, use component.detach());
     * @param rootComponent detached component
     */
    protected void detachComponent(SceneComponent rootComponent) {
        if (rootComponent != null && _components.contains(rootComponent)) {
            _components.add(rootComponent);
        }
    }

    public Framebuffer getFramebuffer() { return _sceneBuffer; }

    public Camera getCamera() { return _camera; }

    public Matrix4f getProjection() {
            return new Matrix4f().perspective(
                    (float) Math.toRadians(_camera.getFieldOfView()),
                    Window.GetPrimaryWindow().getPixelWidth() / (float) Window.GetPrimaryWindow().getPixelHeight(),
                    _camera.getNearClipPlane(),
                    _camera.getFarClipPlane()
            );
    }
}
