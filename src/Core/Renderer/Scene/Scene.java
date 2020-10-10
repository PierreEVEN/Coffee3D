package Core.Renderer.Scene;

import Core.IO.Log;
import Core.Renderer.Scene.Components.Camera;
import Core.Renderer.Scene.Components.SceneComponent;
import Core.Renderer.Scene.Gamemode.DefaultGamemode;
import Core.Renderer.Scene.Gamemode.IGamemodeBase;
import Core.Renderer.Window;
import Core.Resources.Factories.MaterialFactory;
import Core.Resources.Factories.TextureFactory;
import Core.Resources.MaterialResource;
import Core.Resources.MeshResource;
import Core.Resources.Texture2DResource;
import Core.Types.Vertex;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.io.*;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class Scene {
    private Camera _camera;
    private SceneStaticBuffer _sceneUbo;
    private IGamemodeBase _gamemode;

    private ArrayList<SceneComponent> _components;



    MaterialResource test;
    Texture2DResource testText;
    MeshResource testMesh;
    public Scene() {
        //Initialize properties
        _components = new ArrayList<>();

        //Create camera
        _camera = new Camera(new Vector3f(0, 0, 0), new Quaternionf(0, 0, 0, 1));
        _components.add(_camera);

        //Create scene UBO
        _sceneUbo = new SceneStaticBuffer();

        _gamemode = new DefaultGamemode(this);

        testText = TextureFactory.T2dFromFile("test", "resources/textures/avazimmos.png");
        test = MaterialFactory.FromFiles("test2", "resources/shaders/shader.vert", "resources/shaders/shader.frag", new Texture2DResource[] {testText});

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

        testMesh = new MeshResource(vertices, indices);
        testMesh.load();

    }

    public void close() {

    }

    public void renderScene() {
        _sceneUbo.use(this);

        test.use(this);

        Matrix4f model = new Matrix4f().identity(); // make sure to initialize matrix to identity matrix first

        // translate
        model = model.translate(new Vector3f(0,0,0));

        // rotate
        model = model.rotate(10, new Vector3f(1.0f, 0.3f, 0.5f));

        _camera.setPosition(new Vector3f(0, 0, 0));
        _camera.setFieldOfView(45);

        _camera.setPosition(new Vector3f(2, 0, -1));

        test.setMatrixParameter("model", model);
        test.setMatrixParameter("view", _camera.getViewMatrix());
        test.setMatrixParameter("projection", getProjection());

        test.setFloatParameter("test", 4.f);

        testMesh.use(this);

        for (SceneComponent component : _components) {
            component.draw(this);
        }
    }

    public Camera getCamera() { return _camera; }

    public Matrix4f getProjection() {
        if (_camera.enablePerspective()) {
            return new Matrix4f().perspective(
                    (float) Math.toRadians(_camera.getFieldOfView()),
                    Window.GetPrimaryWindow().getPixelWidth() / (float) Window.GetPrimaryWindow().getPixelHeight(),
                    _camera.getNearClipPlane(),
                    _camera.getFarClipPlane()
            );
        }
        else {
            return new Matrix4f().identity();
        }
    }
}
