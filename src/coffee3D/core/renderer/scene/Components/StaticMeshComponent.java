package coffee3D.core.renderer.scene.Components;

import coffee3D.core.assets.AssetReference;
import coffee3D.core.renderer.RenderUtils;
import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.assets.types.StaticMesh;
import coffee3D.core.renderer.scene.SceneComponent;
import coffee3D.core.types.SphereBound;
import coffee3D.core.types.TypeHelper;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;

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

        _mesh.get().setMaterialList(RenderUtils.RENDER_MODE == GL_SELECT ? RenderUtils.getPickMaterialDrawList() : _mesh.get().getMaterials());
        _mesh.get().use(context);
        glEnable(GL_DEPTH_TEST);

        if (doesDisplayOutlines()) {
            glCullFace(GL_BACK);
            _mesh.get().setMaterialList(RenderUtils.getOutlineMaterialDrawList());
            _mesh.get().use(context);
            glCullFace(GL_FRONT);
        }
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