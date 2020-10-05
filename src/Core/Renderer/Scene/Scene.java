package Core.Renderer.Scene;

import Core.IO.Log;
import Core.Renderer.Scene.Components.Camera;
import Core.Renderer.Scene.Components.SceneComponent;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;

public class Scene {
    private Camera _camera;
    private ArrayList<SceneComponent> _components;


    public Scene() {
        //Initialize properties
        _components = new ArrayList<>();

        //Create camera
        _camera = new Camera(new Vector3f(0, 0, 0), new Quaternionf(0, 0, 0, 1));
        _components.add(_camera);
    }

    public void close() {

    }

    public void RenderScene() {
        for (SceneComponent component : _components) {
            component.draw(this);
        }
    }
}
