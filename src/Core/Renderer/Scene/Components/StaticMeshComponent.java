package Core.Renderer.Scene.Components;

import Core.Assets.Material;
import Core.Renderer.Scene.Scene;
import Core.Assets.StaticMesh;
import Core.Renderer.Scene.SceneComponent;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class StaticMeshComponent extends SceneComponent {

    private final StaticMesh _mesh;

    public StaticMeshComponent(StaticMesh mesh, Vector3f position, Quaternionf rotation, Vector3f scale) {
        super(position, rotation, scale);
        _mesh = mesh;
    }

    @Override
    public void draw(Scene context) {
        for (Material mat : _mesh.getMaterials()) {
            mat.use(context);
        }
        _mesh.setMaterialModel(getWorldTransformationMatrix());
        _mesh.use(context);
    }

    public StaticMesh getStaticMesh() { return _mesh; }
}
