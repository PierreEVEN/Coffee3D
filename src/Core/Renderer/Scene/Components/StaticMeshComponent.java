package Core.Renderer.Scene.Components;

import Core.Assets.AssetReference;
import Core.Assets.Material;
import Core.IO.LogOutput.Log;
import Core.Renderer.DebugRendering.DebugRenderer;
import Core.Renderer.RenderUtils;
import Core.Renderer.Scene.Scene;
import Core.Assets.StaticMesh;
import Core.Renderer.Scene.SceneComponent;
import Core.Types.Color;
import Core.Types.SphereBound;
import Core.Types.TypeHelper;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.GL_SELECT;

public class StaticMeshComponent extends SceneComponent {

    private transient static final long serialVersionUID = 4648435994317397619L;
    protected AssetReference<StaticMesh> _mesh;
    private transient SphereBound _componentBound;

    public StaticMeshComponent(StaticMesh mesh, Vector3f position, Quaternionf rotation, Vector3f scale) {
        super(position, rotation, scale);
        _mesh = new AssetReference(StaticMesh.class, mesh);
    }

    @Override
    public void draw(Scene context) {
        super.draw(context);
        if (_mesh.get() == null) return;
        _mesh.get().setMaterialModel(getWorldTransformationMatrix());
        _mesh.get().use(context);
    }

    public StaticMesh getStaticMesh() { return _mesh.get(); }

    @Override
    public SphereBound getBound() {
        if (_mesh.get() == null) return super.getBound();
        if (_componentBound == null) _componentBound = new SphereBound();
        _componentBound.radius = _mesh.get().getBound().radius;
        _componentBound.position = TypeHelper.getVector3(_mesh.get().getBound().position);
        getWorldTransformationMatrix().transformPosition(_componentBound.position);
        Vector3f scale = TypeHelper.getVector3();
        getWorldTransformationMatrix().getScale(scale);
        _componentBound.radius *= Math.max(scale.x, Math.max(scale.y, scale.z));
        return _componentBound;
    }
}
