package Core.Renderer.Scene.Components;

import Core.Renderer.Scene.Scene;
import Core.Assets.StaticMesh;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class StaticMeshComponent extends SceneComponent {

    private final StaticMesh _mesh;

    public StaticMeshComponent(StaticMesh mesh, Vector3f position, Quaternionf rotation) {
        super(position, rotation);
        _mesh = mesh;
    }

    @Override
    public void draw(Scene context) {
        _mesh.draw(context);
    }
}
