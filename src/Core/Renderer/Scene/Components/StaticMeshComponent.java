package Core.Renderer.Scene.Components;

import Core.Assets.AssetReference;
import Core.Assets.Material;
import Core.Renderer.Scene.Scene;
import Core.Assets.StaticMesh;
import Core.Renderer.Scene.SceneComponent;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class StaticMeshComponent extends SceneComponent {

    private AssetReference<StaticMesh> _mesh;

    public StaticMeshComponent(StaticMesh mesh, Vector3f position, Quaternionf rotation, Vector3f scale) {
        super(position, rotation, scale);
        _mesh = new AssetReference(StaticMesh.class, mesh);
    }

    @Override
    public void draw(Scene context) {
        if (_mesh.get() == null) return;
        for (Material mat : _mesh.get().getMaterials()) {
            mat.use(context);
        }
        _mesh.get().setMaterialModel(getWorldTransformationMatrix());
        _mesh.get().use(context);
    }

    public StaticMesh getStaticMesh() { return _mesh.get(); }
}
