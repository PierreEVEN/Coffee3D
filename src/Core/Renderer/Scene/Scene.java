package Core.Renderer.Scene;

import Core.IO.Log;
import Core.Renderer.Scene.Components.Camera;
import Core.Renderer.Scene.Components.SceneComponent;
import Core.Resources.Factories.MaterialFactory;
import Core.Resources.Factories.TextureFactory;
import Core.Resources.MaterialResource;
import Core.Resources.MeshResource;
import Core.Resources.Texture2DResource;
import Core.Types.Vertex;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.*;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class Scene {
    private Camera _camera;
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

        testText = TextureFactory.T2dFromFile("resources/textures/avazimmos.png");
        test = MaterialFactory.FromFiles("resources/shaders/shader.vert", "resources/shaders/shader.frag", new Texture2DResource[] {testText});

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

    public void RenderScene() {
        test.use(this);
        testMesh.use(this);

        for (SceneComponent component : _components) {
            component.draw(this);
        }
    }
}
