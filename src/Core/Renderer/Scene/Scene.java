package Core.Renderer.Scene;

import Core.IO.LogOutput.Log;
import Core.Renderer.RenderUtils;
import Core.Renderer.Scene.Components.Camera;
import Core.Renderer.Scene.Gamemode.DefaultGamemode;
import Core.Renderer.Scene.Gamemode.IGamemodeBase;
import Core.Types.Color;
import org.joml.Matrix4f;

import java.io.*;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.GL_SELECT;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;

public class Scene {
    private final transient IGamemodeBase _gameMode;
    private ArrayList<SceneComponent> _components;

    protected SceneProperty _sceneProperties;


    public Scene() {
        _components = new ArrayList<>();
        _gameMode = new DefaultGamemode((RenderScene) this);
        _sceneProperties = new SceneProperty();
    }

    public IGamemodeBase getGamemode() { return _gameMode; }

    private static int colorCount = 0;

    public void renderScene() {

        _gameMode.update(this);


        colorCount = 0;
        // Draw attached components
        for (int i = 0; i < _components.size(); ++i) {

            if (RenderUtils.RENDER_MODE == GL_SELECT) {
                colorCount++;
                RenderUtils.getPickMaterial().use(this);
                RenderUtils.getPickMaterial().getShader().setIntParameter("pickId", i + 1);
                RenderUtils.CheckGLErrors();
            }

            _components.get(i).drawInternal(this);
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

    public static Matrix4f getProjection(float width, float height, Camera camera) {
        return new Matrix4f().perspective(
                (float) Math.toRadians(camera.getFieldOfView()),
                width / height,
                camera.getNearClipPlane(),
                camera.getFarClipPlane()
        );
    }


    public void saveToFile(String filePath) {
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(_components);
            oos.writeObject(_sceneProperties);
        } catch (Exception e) {
            Log.Warning("failed to serialise scene : " + e.getMessage());
        }
    }

    public void loadFromFile(String filePath) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<SceneComponent> comps = (ArrayList<SceneComponent>) ois.readObject();
            _sceneProperties = (SceneProperty) ois.readObject();
            if (comps != null) _components = comps;
            ois.close();
            fis.close();
        } catch (Exception e) {
            Log.Warning("failed to deserialize scene : " + e.getMessage());
        }
    }
}
