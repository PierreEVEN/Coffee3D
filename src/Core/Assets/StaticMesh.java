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
    private String[] _materialNames;
    private transient Material[] materialRefs;
    private Matrix4f _modelMatrix;

    public StaticMesh(String name, String filePath, String[] materials) {
        super(name, filePath);
        _materialNames = materials;
        materialRefs = new Material[_materialNames.length];
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
        if (materialRefs.length != _materialNames.length) materialRefs = new Material[_materialNames.length];

        for (int i = 0; i < _materialNames.length; ++i) {
            Material foundMat = AssetManager.FindAsset(_materialNames[i]);
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
                if (_sections[i] != null) {
                    _sections[i].use(context);
                }
                if (materials.length > i && materials[i] != null && materials[i].getShader() != null) {
                    materials[i].getShader().setMatrixParameter("model", _modelMatrix);
                    materials[i].use(context);
                }
            }
        }
        else {
            Log.Warning(toString() + " has empty sections");
        }
    }
}
