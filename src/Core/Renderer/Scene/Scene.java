package Core.Renderer.Scene;

import Core.Assets.AssetManager;
import Core.Assets.Material;
import Core.IO.LogOutput.Log;
import Core.Renderer.Scene.Components.Camera;
import Core.Renderer.Scene.Gamemode.DefaultGamemode;
import Core.Renderer.Scene.Gamemode.IGamemodeBase;
import Core.Renderer.Window;
import org.joml.Matrix4f;

import java.io.*;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL30.glBindBufferRange;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL31C.glUniformBlockBinding;

public class Scene {
    private final transient Camera _camera;
    private final transient IGamemodeBase _gameMode;
    private ArrayList<SceneComponent> _components;
    private SceneStaticBuffer _sceneUbo;

    protected SceneProperty _sceneProperties;


    public Scene() {
        _components = new ArrayList<>();
        _gameMode = new DefaultGamemode(this);
        _camera = new Camera();
        _sceneUbo = new SceneStaticBuffer();
        _sceneUbo.load();
        _sceneProperties = new SceneProperty();
    }

    public void renderScene() {
        // Tick gameMode
        _gameMode.update(this);
        //Update static buffer
        _sceneUbo.use(this);

        // Draw attached components
        for (SceneComponent component : _components) {
            component.drawInternal(this);
        }
    }

    public ArrayList<SceneComponent> getComponents() {
        return _components;
    }

    /**
     * Make component root on this scene.
     * (don't call this method yourself, use component.attachToScene(sceneParam));
     *
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
     *
     * @param rootComponent detached component
     */
    protected void detachComponent(SceneComponent rootComponent) {
        if (rootComponent != null && _components.contains(rootComponent)) {
            _components.add(rootComponent);
        }
    }

    public Camera getCamera() {
        return _camera;
    }

    public Matrix4f getProjection() {
        return new Matrix4f().perspective(
                (float) Math.toRadians(_camera.getFieldOfView()),
                Window.GetPrimaryWindow().getPixelWidth() / (float) Window.GetPrimaryWindow().getPixelHeight(),
                _camera.getNearClipPlane(),
                _camera.getFarClipPlane()
        );
    }


    public void saveToFile(String filePath) {
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(_components);
        } catch (Exception e) {
            Log.Warning("failed to serialise scene : " + e.getMessage());
        }
    }

    public void loadFromFile(String filePath) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<SceneComponent> comps = (ArrayList<SceneComponent>) ois.readObject();
            if (comps != null) _components = comps;
            ois.close();
            fis.close();
        } catch (Exception e) {
            Log.Warning("failed to deserialize scene : " + e.getMessage());
        }
    }
}
