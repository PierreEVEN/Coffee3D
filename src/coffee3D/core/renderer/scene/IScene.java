package coffee3D.core.renderer.scene;

import org.joml.Matrix4f;

import java.util.List;

public interface IScene {
    void attachComponent(SceneComponent rootComponent);
    void detachComponent(SceneComponent rootComponent);
    List<SceneComponent> getComponents();
    Matrix4f getSceneTransform();
}
