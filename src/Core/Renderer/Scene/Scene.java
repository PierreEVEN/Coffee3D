package Core.Renderer.Scene;

import Core.Assets.AssetManager;
import Core.Assets.Material;
import Core.Assets.StaticMesh;
import Core.Assets.Texture2D;
import Core.Renderer.Scene.Components.Camera;
import Core.Renderer.Scene.Components.StaticMeshComponent;
import Core.Renderer.Scene.Gamemode.DefaultGamemode;
import Core.Renderer.Scene.Gamemode.IGamemodeBase;
import Core.Renderer.Window;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import static org.lwjgl.opengl.GL46.*;

public class Scene implements Serializable {
    private final transient Camera _camera;
    private final transient IGamemodeBase _gameMode;
    private final ArrayList<SceneComponent> _components;


    public Scene() {
        _components = new ArrayList<>();
        _gameMode = new DefaultGamemode(this);
        _camera = new Camera(this);
    }

    public void renderScene() {
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

        // Draw attached components
        for (SceneComponent component : _components) {
            component.drawInternal(this);
        }
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
