package Core.Assets;

import Core.Factories.MeshFactory;
import Core.IO.Log;
import Core.Renderer.Scene.Scene;
import Core.Resources.MeshResource;
import org.joml.Matrix4f;

import java.util.ArrayList;

/**
 * NIY
 */
public class StaticMesh extends Asset {
    private transient MeshResource[] _sections;
    private String[] _materialNames;

    public StaticMesh(String name, String filePath, String[] materials) {
        super(name, filePath);
        _materialNames = materials;
    }

    @Override
    public void load() {
        _sections = MeshFactory.FromFile(getName(), getFilepath());
    }

    public void setMaterialModel(Matrix4f modelMatrix) {
        for (Material mat : getMaterials()) {
            mat.getShader().setMatrixParameter("model", modelMatrix);
        }
    }

    public Material[] getMaterials() {
        ArrayList<Material> materials = new ArrayList<>();
        for (String mat : _materialNames) {
            Material foundMat = (Material) AssetManager.GetAsset(mat);
            if (foundMat != null) {
                materials.add(foundMat);
            }
        }
        Material[] ret = new Material[materials.size()];
        materials.toArray(ret);
        return ret;
    }

    @Override
    public void use(Scene context) {
        if (_sections != null) {
            for (MeshResource section : _sections) {
                if (section != null) section.use(context);
            }
        }
        else {
            Log.Warning(toString() + " has empty sections");
        }
    }
}
