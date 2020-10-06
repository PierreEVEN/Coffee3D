package Core.Renderer.Scene.Components;

import Core.Renderer.Scene.Scene;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class SceneComponent {
    private Vector3f _position;
    private Quaternionf _rotation;

    protected SceneComponent(Vector3f position, Quaternionf rotation) {
        _position = position;
        _rotation = rotation;
    }

    public Vector3f getPosition() {
        return _position;
    }

    public abstract void draw(Scene context);

}
