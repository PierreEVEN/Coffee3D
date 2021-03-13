package coffee3D.core.animation;

import coffee3D.core.assets.Asset;
import coffee3D.core.assets.AssetManager;
import coffee3D.core.io.log.Log;
import coffee3D.core.renderer.scene.Components.StaticMeshComponent;
import coffee3D.core.renderer.scene.IScene;
import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.renderer.scene.SceneComponent;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SkeletalMesh extends Asset implements IScene {

    private static final long serialVersionUID = -8225927565901039565L;

    private final HashMap<String, StaticMeshComponent> _bones = new HashMap<>();
    private final List<SceneComponent> _components = new ArrayList<>();

    public SkeletalMesh(String assetName, File assetPath) {
        super(assetName, null, assetPath);
    }

    public void addBone(String boneName, String parent) {
        if (_bones.containsKey(boneName)) return;
        if (!(parent == null || parent == "") && !_bones.containsKey(parent)) return;

        StaticMeshComponent comp = new StaticMeshComponent(null, new Vector3f(0), new Quaternionf().identity(), new Vector3f(1));
        comp.setComponentName(boneName);

        if (parent != null && parent != "") {
            comp.attachToComponent(_bones.get(parent));
        }
        else {
            comp.attachToScene(this);
        }

        _bones.put(boneName, comp);
    }

    public void removeBone(String boneName) {
        if (!_bones.containsKey(boneName)) return;
        _bones.get(boneName).detach();
        _bones.remove(_bones);
    }



    @Override
    public void attachComponent(SceneComponent rootComponent) {
        if (rootComponent != null && !_components.contains(rootComponent)) {
            _components.add(rootComponent);
        }
    }

    @Override
    public void detachComponent(SceneComponent rootComponent) {
        if (rootComponent != null && _components.contains(rootComponent)) {
            _components.remove(rootComponent);
        }
    }

    @Override
    public List<SceneComponent> getComponents() {
        return _components;
    }

    @Override
    public Matrix4f getSceneTransform() {
        return null;
    }

    public void updateNames() {
        for (SceneComponent comp : getComponents()) {
            for (SceneComponent hashValue : _bones.values()) {
                if (comp == hashValue && !comp.getComponentName().equals(hashValue.getComponentName())) {
                    //hashValue.
                }
            }
        }
    }

    @Override
    public void load() {}

    @Override
    public void reload() {}

    @Override
    public void use(Scene context) {}
}
