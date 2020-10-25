package Core.Assets;

import Core.Factories.MeshFactory;
import Core.IO.LogOutput.Log;
import Core.Renderer.Scene.Scene;
import Core.Resources.MeshResource;
import org.joml.Matrix4f;

import java.util.ArrayList;

public class StaticMesh extends Asset {
    private static final long serialVersionUID = -3309672296934490500L;
    private transient MeshResource[] _sections;
    protected ArrayList<AssetReference<Material>> _materials;
    private transient Material[] materialRefs = new Material[0];
    private Matrix4f _modelMatrix;

    public StaticMesh(String name, String filePath, String[] materials) {
        super(name, filePath);

        _materials = new ArrayList<>();
        for (String mat : materials) {
            _materials.add(new AssetReference<>(Material.class, mat));
        }

        _modelMatrix = new Matrix4f().identity();
    }

    @Override
    public void load() {
        _sections = MeshFactory.FromFile(getName(), getFilepath());
    }

    public void setMaterialModel(Matrix4f modelMatrix) {
        _modelMatrix = modelMatrix;
    }

    public Material[] getMaterials() {
        if (materialRefs.length != _materials.size()) materialRefs = new Material[_materials.size()];

        for (int i = 0; i < _materials.size(); ++i) {
            Material foundMat = _materials.get(i).get();
            if (foundMat != null) {
                materialRefs[i] = foundMat;
            }
            else {
                materialRefs[i] = null;
            }
        }
        return materialRefs;
    }

    @Override
    public void use(Scene context) {
        Material[] materials = getMaterials();
        if (_sections != null) {
            for (int i = 0; i < _sections.length; ++i) {
                if (materials.length > i && materials[i] != null && materials[i].getShader() != null) {
                    materials[i].getShader().setMatrixParameter("model", _modelMatrix);
                    materials[i].use(context);
                }
                if (_sections[i] != null) {
                    _sections[i].use(context);
                }
            }
        }
        else {
            Log.Warning(toString() + " has empty sections");
        }
    }
}
