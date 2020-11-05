package coffee3D.core.renderer.scene.Components;

import coffee3D.core.assets.AssetReference;
import coffee3D.core.assets.types.MaterialInterface;
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
    private AssetReference<StaticMesh> lastMesh;
    private transient SphereBound _componentBound;
    protected AssetReference<MaterialInterface>[] _materialOverride;
    private MaterialInterface[] _materialList;

    public StaticMeshComponent(StaticMesh mesh, Vector3f position, Quaternionf rotation, Vector3f scale) {
        super(position, rotation, scale);
        _mesh = new AssetReference(StaticMesh.class, mesh);
        lastMesh = new AssetReference<>(StaticMesh.class);
        rebuildOverrides();
    }

    public void setMaterial(MaterialInterface material, int index) {
        if (_materialOverride == null) return;
        if (index < _materialOverride.length) _materialOverride[index].set(material);
    }

    @Override
    public void draw(Scene context) {

        // If mesh is null, draw default billboard
        if (_mesh.get() == null) {
            super.draw(context);
            return;
        }

        // rebuild override list if mesh changed
        if (lastMesh.get() != _mesh.get()) rebuildOverrides();

        // Send model matrix
        _mesh.get().setMaterialModel(getWorldTransformationMatrix());

        // Select material drawList
        switch (RenderUtils.RENDER_MODE) {
            case Select -> _mesh.get().setMaterialList(RenderUtils.getPickMaterialDrawList());
            case Shadow -> _mesh.get().setMaterialList(RenderUtils.getShadowDrawList());
            case Color -> {
                for (int i = 0; i < _materialList.length; ++i) {
                    if (_materialOverride[i].get() != null) _materialList[i] = _materialOverride[i].get();
                    else _materialList[i] = _mesh.get().getMaterials()[i];
                }
                _mesh.get().setMaterialList(_materialList);
            }
        }

        // Render mesh
        _mesh.get().use(context);

        if (doesDisplayOutlines()) drawOutlines(context);
    }

    private void drawOutlines(Scene context) {
        glDisable(GL_CULL_FACE);
        _mesh.get().setMaterialList(RenderUtils.getOutlineMaterialDrawList());
        _mesh.get().use(context);
        glDisable(GL_CULL_FACE);
    }

    private void rebuildOverrides() {
        if (lastMesh.get() == _mesh.get()) return;
        lastMesh.set(_mesh.get());

        if (_mesh.get() == null) return;
        _materialOverride = new AssetReference[_mesh.get().getMaterials().length];
        for (int i = 0; i < _materialOverride.length; ++i) {
            _materialOverride[i] = new AssetReference<>(MaterialInterface.class);
        }
        _materialList = new MaterialInterface[_materialOverride.length];
    }

    public StaticMesh getStaticMesh() { return _mesh.get(); }

    public void setStaticMesh(StaticMesh mesh) {
        _mesh.set(mesh);
        rebuildOverrides();
    }

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