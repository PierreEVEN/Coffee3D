package Core.Assets;

import Core.Factories.MeshFactory;
import Core.IO.LogOutput.Log;
import Core.IO.Settings.EngineSettings;
import Core.Renderer.DebugRendering.DebugRenderer;
import Core.Renderer.RenderUtils;
import Core.Renderer.Scene.Scene;
import Core.Resources.MeshResource;
import Core.Types.Color;
import Core.Types.SphereBound;
import Core.Types.TypeHelper;
import imgui.ImGui;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.File;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.GL_SELECT;

public class StaticMesh extends Asset {
    private static final long serialVersionUID = -3309672296934490500L;
    private transient MeshResource[] _sections;
    protected ArrayList<AssetReference<Material>> _materials;
    private transient Material[] materialRefs = new Material[0];
    private Matrix4f _modelMatrix;
    private static final Color meshColor = new Color(.19f, .8f, .9f, 1);
    private SphereBound _meshBound;

    public StaticMesh(String name, String filePath, File assetPath, String[] materials) {
        super(name, filePath, assetPath);

        _materials = new ArrayList<>();
        for (String mat : materials) {
            _materials.add(new AssetReference<>(Material.class, mat));
        }

        _modelMatrix = new Matrix4f().identity();
    }

    @Override
    public Color getAssetColor() {
        return meshColor;
    }

    @Override
    public void load() {
        _sections = MeshFactory.FromFile(getName(), getFilepath());
    }

    public void setMaterialModel(Matrix4f modelMatrix) {
        _modelMatrix = modelMatrix;
    }

    public Material[] getMaterials() {
        if (materialRefs == null || materialRefs.length != _materials.size()) materialRefs = new Material[_materials.size()];

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

    static final Matrix3f test = new Matrix3f();

    @Override
    public void use(Scene context) {
        Material[] materials = null;
        if (RenderUtils.RENDER_MODE == GL_SELECT) {
            RenderUtils.getPickMaterial().use(context);
            RenderUtils.getPickMaterial().getShader().setMatrixParameter("model", _modelMatrix);
            RenderUtils.CheckGLErrors();
        }
        else {
            materials = getMaterials();
        }
        if (_sections != null) {
            for (int i = 0; i < _sections.length; ++i) {
                if (materials != null && materials.length > i && materials[i] != null && materials[i].getShader() != null) {
                    materials[i].use(context);
                    materials[i].getShader().setMatrixParameter("model", _modelMatrix);
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
                _meshBound.position.add(section.getBound().position);
            }
            _meshBound.position.div(_sections.length);

            for (MeshResource section : _sections) {
                float radius = _meshBound.position.distance(section.getBound().position) + section.getBound().radius;
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
                Vector3f pos = TypeHelper.getVector3(_sections[i].getBound().position);
                _modelMatrix.transformPosition(pos);
                Vector3f scale = TypeHelper.getVector3();
                _modelMatrix.getScale(scale);
                DebugRenderer.DrawDebugSphere(context, pos, _sections[i].getBound().radius * Math.max(scale.x, Math.max(scale.y, scale.z)), 20, Color.GREEN);
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
        ImGui.text("section count : " + _sections.length);
    }
}
