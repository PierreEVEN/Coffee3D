package Core.Renderer.Scene;

import Core.Assets.Material;
import Core.Assets.StaticMesh;
import Core.Assets.Texture2D;
import Core.Renderer.Scene.Components.Camera;
import Core.Renderer.Scene.Components.SceneComponent;
import Core.Renderer.Scene.Gamemode.DefaultGamemode;
import Core.Renderer.Scene.Gamemode.IGamemodeBase;
import Core.Renderer.Window;
import Core.Resources.MeshResource;
import Core.Types.Vertex;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL46.*;

public class Scene {
    private Camera _camera;
    private SceneStaticBuffer _sceneUbo;
    private IGamemodeBase _gamemode;

    private ArrayList<SceneComponent> _components;



    Material testMat;
    Texture2D testTexture;
    MeshResource testMesh;
    StaticMesh testSm;


    public Scene() {
        //Initialize properties
        _components = new ArrayList<>();

        //Create camera
        _camera = new Camera(new Vector3f(0, 0, 0), new Quaternionf(0, 0, 0, 1));
        _components.add(_camera);

        //Create scene UBO
        _sceneUbo = new SceneStaticBuffer();

        //Update gamemode
        _gamemode = new DefaultGamemode(this);

        glEnable(GL_DEPTH_TEST);

        //testText = TextureFactory.T2dFromFile("test", "resources/textures/avazimmos.png");
        testTexture = new Texture2D("testText", "resources/textures/defaultGrid.png");
        testMat = new Material("testMat", "resources/shaders/shader");
       //         MaterialFactory.FromFiles("test2", "resources/shaders/shader.vert", "resources/shaders/shader.frag", new Texture2DResource[] {testTexture});



        Vertex[] vertices = {
                new Vertex(new Vector3f(.5f, .5f, .0f), new Vector2f(1.0f, 1.0f)),
                new Vertex(new Vector3f(.5f, -.5f, .0f), new Vector2f(1.0f, 0.0f)),
                new Vertex(new Vector3f(-.5f, -.5f, .0f), new Vector2f(0.0f, 0.0f)),
                new Vertex(new Vector3f(-.5f, .5f, .0f), new Vector2f(0.0f, 1.0f)),
        };

        int[] indices = {
                0, 1, 2,
                0, 2, 3
        };

        testMesh = new MeshResource("test3", vertices, indices);
        testSm = new StaticMesh("cube", "resources/models/test.fbx");
        testMesh.load();
    }

    public void close() {

    }

    public void renderScene() {
        _sceneUbo.use(this);

        testMat.use(this);

        _gamemode.update(this);

        Matrix4f model = new Matrix4f().identity(); // make sure to initialize matrix to identity matrix first

        // translate
        model = model.translate(new Vector3f(0,0,0));

        // rotate
        //model = model.rotate(10, new Vector3f(1.0f, 0.3f, 0.5f));

        //_camera.setRotation(new Quaternionf().identity());

        testMat.getShader().setMatrixParameter("model", model);
        testMat.getShader().setMatrixParameter("view", _camera.getViewMatrix());
        testMat.getShader().setMatrixParameter("projection", getProjection());

        testMat.getShader().setFloatParameter("test", 4.f);

        testMesh.use(this);
        testSm.use(this);

        for (SceneComponent component : _components) {
            component.draw(this);
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
