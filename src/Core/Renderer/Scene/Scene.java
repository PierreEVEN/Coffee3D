package Core.Renderer.Scene;

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

import java.util.ArrayList;
import java.util.Random;

import static org.lwjgl.opengl.GL46.*;

public class Scene {
    private Camera _camera;
    private IGamemodeBase _gamemode;

    private ArrayList<SceneComponent> _components;



    Material testMat;
    Texture2D testTexture;
    Material matChelou;


    public Scene() {
        //Initialize properties
        _components = new ArrayList<>();

        //Create camera
        _camera = new Camera(new Vector3f(0, 0, 0), new Quaternionf(0, 0, 0, 1));
        _components.add(_camera);

        //Update gamemode
        _gamemode = new DefaultGamemode(this);

        testTexture = new Texture2D("testText", "resources/textures/defaultGrid.png");
        testMat = new Material("testMat", "resources/shaders/shader");
        matChelou = new Material("matChelou", "resources/shaders/test");

        StaticMesh cube = new StaticMesh("cube", "resources/models/cube.fbx", new String[] { "matChelou" });
        StaticMesh testSm = new StaticMesh("test", "resources/models/test.fbx", new String[] { "testMat" });


        SceneComponent root = new SceneComponent(new Vector3f(0,0,0), new Quaternionf().identity(), new Vector3f(1,1,1));

        root.attachToScene(this);

        Random rnd = new Random();
        for (int i = 0; i < 300; ++i) {
            float range = 200;

            StaticMeshComponent parent = new StaticMeshComponent(
                    testSm,
                    new Vector3f(rnd.nextFloat() * range - range / 2 , rnd.nextFloat() * range - range / 2, rnd.nextFloat() * range - range / 2),
                    new Quaternionf().fromAxisAngleDeg(new Vector3f(0, 0, 1), 10),
                    new Vector3f(1, 1, 1)
            );
            parent.attachToComponent(root);

            new StaticMeshComponent(
                    cube,
                    new Vector3f(0, 0, 2),
                    new Quaternionf().fromAxisAngleDeg(new Vector3f(1, 0, 0), 25),
                    new Vector3f(1.5f, 0.8f, 1)
            ).attachToComponent(parent);

            StaticMeshComponent subChild = new StaticMeshComponent(
                    cube,
                    new Vector3f(0, 4, 1),
                    new Quaternionf().fromAxisAngleDeg(new Vector3f(1, 2, 0).normalize(), 8789),
                    new Vector3f(1.5f, 0.8f, 1.4f)
            );
            subChild.attachToComponent(parent);

            new StaticMeshComponent(
                    cube,
                    new Vector3f(3, 2, 1),
                    new Quaternionf().fromAxisAngleDeg(new Vector3f(1, 2, 0).normalize(), 489),
                    new Vector3f(1.1f, 0.8f, 0.02f)
            ).attachToComponent(subChild);
        }
    }

    public void renderScene() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_FRONT);
        glFrontFace(GL_CW);

        // Tick gameMode
        _gamemode.update(this);

        testMat.use(this);
        testMat.getShader().setMatrixParameter("view", _camera.getViewMatrix());
        testMat.getShader().setMatrixParameter("projection", getProjection());
        matChelou.use(this);
        matChelou.getShader().setMatrixParameter("view", _camera.getViewMatrix());
        matChelou.getShader().setMatrixParameter("projection", getProjection());


        for (SceneComponent component : _components) {
            if (!(component instanceof Camera)) component.getLocalPosition().x = (float)Math.sin(GLFW.glfwGetTime()) * 4;
            if (!(component instanceof Camera)) component.getLocalPosition().y = (float)Math.sin(GLFW.glfwGetTime() * 2) * 3;
            if (!(component instanceof Camera)) component.getLocalPosition().z = (float)Math.sin(GLFW.glfwGetTime() * 0.5f) * 2;

            if (!(component instanceof Camera)) component.getLocalScale().x = ((float)Math.sin(GLFW.glfwGetTime() * 4.2f) + 2) / 3;

            component.drawInternal(this);
        }
    }

    /**
     * Make component root on this scene.
     * (don't call this method yourself, use component.attachToScene(sceneParam));
     * @param rootComponent
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
     * @param rootComponent
     */
    protected void detachComponent(SceneComponent rootComponent) {
        if (rootComponent != null && _components.contains(rootComponent)) {
            _components.add(rootComponent);
        }
    }

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
