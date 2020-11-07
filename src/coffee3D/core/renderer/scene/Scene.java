package coffee3D.core.renderer.scene;

import coffee3D.core.assets.AssetReference;
import coffee3D.core.assets.types.World;
import coffee3D.core.io.log.Log;
import coffee3D.core.renderer.RenderMode;
import coffee3D.core.renderer.RenderUtils;
import coffee3D.core.renderer.scene.Components.Camera;
import coffee3D.core.types.TypeHelper;
import coffee3D.editor.ui.importers.WorldCreator;
import org.joml.Matrix4f;
import java.io.*;
import java.util.ArrayList;
import static org.lwjgl.opengl.GL11.GL_SELECT;

public class Scene {
    private ArrayList<SceneComponent> _components;

    protected SceneProperty _sceneProperties;
    private final AssetReference<World> _source;


    public Scene() {
        _components = new ArrayList<>();
        _sceneProperties = new SceneProperty();
        _source = new AssetReference<>(World.class);
    }

    public SceneProperty getProperties() { return _sceneProperties; }

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
            _components.remove(rootComponent);
        }
    }

    public static Matrix4f getProjection(float width, float height, Camera camera) {
        if (camera.enablePerspective()) {
            return TypeHelper.getMat4().identity().perspective(
                    (float) Math.toRadians(camera.getFieldOfView()),
                    width / height,
                    camera.getNearClipPlane(),
                    camera.getFarClipPlane()
            );
        }
        else {

            float near_plane = 1f, far_plane = 100;
            float shadowRadius = far_plane / 2;

            Matrix4f lightProjection = TypeHelper.getMat4().ortho(-shadowRadius, shadowRadius, -shadowRadius, shadowRadius, near_plane, far_plane);
            return lightProjection;

        }
    }

    public AssetReference<World> getSource() {
        return _source;
    }

    public void clear() {
        _source.set(null);
        _components.clear();
    }

    public void delete() {
        _source.set(null);
        _sceneProperties = null;
        clear();
    }

    public void load(World source) {
        clear();
        if (source == null || source.getSourcePath() == null || !source.getSourcePath().exists()) return;
        if (_source.get() != null) _source.get().setScene(null);
        _source.set(source);
        _source.get().setScene(this);
        loadFromFile(source.getSourcePath().getPath());
    }

    public void save() {
        if (_source.get() == null) {
            new WorldCreator(this, "save world to...");
        }
        else {
            saveToFile(_source.get().getSourcePath().getPath());
        }
    }

    private void saveToFile(String filePath) {
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(_components);
            oos.writeObject(_sceneProperties);
            Log.Display("successfully saved world");
        } catch (Exception e) {
            Log.Warning("failed to serialise scene : " + e.getMessage());
        }
    }

    private void loadFromFile(String filePath) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<SceneComponent> comps = (ArrayList<SceneComponent>) ois.readObject();
            _sceneProperties = (SceneProperty) ois.readObject();
            _components.clear();
            if (comps != null) {
                for (SceneComponent comp : comps) {
                    comp.attachToScene(this);
                }
            }
            ois.close();
            fis.close();
        } catch (Exception e) {
            Log.Warning("failed to deserialize scene : " + e.getMessage());
        }
    }
}
