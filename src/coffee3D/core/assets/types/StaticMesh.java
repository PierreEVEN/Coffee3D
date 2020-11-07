package coffee3D.core.assets.types;

import coffee3D.core.assets.Asset;
import coffee3D.core.assets.AssetReference;
import coffee3D.core.resources.factories.ImportZAxis;
import coffee3D.core.resources.factories.MeshFactory;
import coffee3D.core.io.log.Log;
import coffee3D.core.io.settings.EngineSettings;
import coffee3D.core.renderer.debug.DebugRenderer;
import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.resources.types.MeshResource;
import coffee3D.core.resources.ResourceManager;
import coffee3D.core.types.Color;
import coffee3D.core.types.SphereBound;
import coffee3D.core.types.TypeHelper;
import imgui.ImGui;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.File;
import java.util.ArrayList;

public class StaticMesh extends Asset {
    private transient static final long serialVersionUID = -3309672296934490500L;
    private transient MeshResource[] _sections;
    protected ArrayList<AssetReference<MaterialInterface>> _materials;
    private transient MaterialInterface[] materialRefs = new MaterialInterface[0];
    private transient Matrix4f _modelMatrix;
    private transient static final Color meshColor = new Color(0/255f, 255/255f, 255/255f, 1);
    private transient SphereBound _meshBound;
    private static final String[] meshExtensions = new String[] {"fbx", "obj", "FBX"};
    private transient MaterialInterface[] _materialDrawList;
    protected ImportZAxis _zAxis;

    public StaticMesh(String name, File filePath, File assetPath, String[] materials) {
        super(name, filePath, assetPath);
        _materials = new ArrayList<>();
        Log.Warning("set axis");
        for (String mat : materials) {
            _materials.add(new AssetReference<>(MaterialInterface.class, mat));
        }

        _modelMatrix = new Matrix4f().identity();
    }

    @Override
    public String[] getAssetExtensions() {
        return meshExtensions;
    }

    @Override
    public Color getAssetColor() {
        return meshColor;
    }

    @Override
    public void load() {
        if (_zAxis == null) _zAxis = ImportZAxis.ZUp;
        _sections = MeshFactory.FromFile(getName(), getSourcePath(), _zAxis);
    }

    @Override
    public void reload() {
        if (_sections != null) {
            for (MeshResource section : _sections) {
                ResourceManager.UnRegisterResource(section);
            }
        }
        MeshResource[] newSections = null;
        try {
            newSections = MeshFactory.FromFile(getName(), getSourcePath(), _zAxis);
        }
        catch (Exception e) {
            Log.Warning("failed to load or compile shaders : " + e.getMessage());
        }

        if (newSections != null) {
            _sections = newSections;
        }
        else {
            for (MeshResource section : _sections) {
                ResourceManager.RegisterResource(section);
            }
        }
    }

    public void setMaterialModel(Matrix4f modelMatrix) {
        _modelMatrix = modelMatrix;
    }

    public MaterialInterface[] getMaterials() {
        if (materialRefs == null || materialRefs.length != _materials.size()) materialRefs = new MaterialInterface[_materials.size()];

        for (int i = 0; i < _materials.size(); ++i) {
            MaterialInterface foundMat = _materials.get(i).get();
            if (foundMat != null) {
                materialRefs[i] = foundMat;
            }
            else {
                materialRefs[i] = null;
            }
        }
        return materialRefs;
    }

    public void setMaterialList(MaterialInterface[] materials) {
        _materialDrawList = materials;
    }

    @Override
    public void use(Scene context) {
        if (_sections != null) {
            for (int i = 0; i < _sections.length; ++i) {
                if (_materialDrawList != null && _materialDrawList.length > i && _materialDrawList[i] != null && _materialDrawList[i].getResource() != null) {
                    _materialDrawList[i].use(context);
                    _materialDrawList[i].getResource().setMatrixParameter("model", _modelMatrix);
                }
                if (_sections[i] != null) {
                    _sections[i].use(context);
                }
            }
        }
        else {
            Log.Warning(toString() + " has empty sections");
        }
        if (EngineSettings.DRAW_DEBUG_BOUNDS.get()) {
            drawBound(context);
        }
    }

    private void rebuildBound() {
        _meshBound = new SphereBound();

        if (_sections != null) {
            for (MeshResource section : _sections) {
                _meshBound.position.add(section.getStaticBounds().position);
            }
            _meshBound.position.div(_sections.length);

            for (MeshResource section : _sections) {
                float radius = _meshBound.position.distance(section.getStaticBounds().position) + section.getStaticBounds().radius;
                if (radius > _meshBound.radius) {
                    _meshBound.radius = radius;
                }
            }
        }
    }


    public SphereBound getBound() {
        if (_meshBound == null) rebuildBound();
        return _meshBound;
    }

    public void drawBound(Scene context) {
        if (_sections != null) {
            for (int i = 0; i < _sections.length; ++i) {
                Vector3f pos = TypeHelper.getVector3(_sections[i].getStaticBounds().position);
                _modelMatrix.transformPosition(pos);
                Vector3f scale = TypeHelper.getVector3();
                _modelMatrix.getScale(scale);
                DebugRenderer.DrawDebugSphere(context, pos, _sections[i].getStaticBounds().radius * Math.max(scale.x, Math.max(scale.y, scale.z)), 20, Color.GREEN);
            }
            Vector3f myPos = TypeHelper.getVector3(getBound().position);
            Vector3f myScale = TypeHelper.getVector3();
            _modelMatrix.getScale(myScale);
            _modelMatrix.transformPosition(myPos);
            DebugRenderer.DrawDebugSphere(context, myPos, getBound().radius * Math.max(myScale.x, Math.max(myScale.y, myScale.z)), 20, Color.BLUE);
        }
    }

    @Override
    public void drawDetailedContent() {
        super.drawDetailedContent();

        ImGui.text("section count : " + (_sections == null ? "null" : _sections.length));
    }
}
