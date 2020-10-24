package Core.Assets;

import Core.Factories.MaterialFactory;
import Core.IO.LogOutput.Log;
import Core.Renderer.Scene.Scene;
import Core.Resources.MaterialResource;
import Core.Resources.ResourceManager;
import imgui.ImGui;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL46.*;

public class Material extends Asset {

    private static final long serialVersionUID = -2932087609993578842L;
    private transient MaterialResource _mat;
    private ArrayList<AssetReference<Texture2D>> _textures = new ArrayList<>();

    public Material(String name, String filePath) {
        super(name, filePath);
    }

    public Material(String name, String filePath, String[] textureNames) {
        super(name, filePath);
        for (String texture : textureNames) {
            _textures.add(new AssetReference<>(Texture2D.class, texture));
        }
    }

    public MaterialResource getShader() { return _mat; }

    @Override
    public void load() {
        String fileCleanName = getFilepath().replaceFirst("[.][^.]+$", "");
        _mat = MaterialFactory.FromFiles(getName(), fileCleanName + ".vert", fileCleanName + ".frag");
    }

    public void recompile() {
        String fileCleanName = getFilepath().replaceFirst("[.][^.]+$", "");
        ResourceManager.UnRegisterResource(_mat);
        MaterialResource newMat = null;

        try {
            newMat = MaterialFactory.FromFiles(getName(), fileCleanName + ".vert", fileCleanName + ".frag");
        }
        catch (Exception e) {
            Log.Warning("failed to load or compile shaders : " + e.getMessage());
        }

        if (newMat != null) {
            _mat = newMat;
        }
        else {
            ResourceManager.RegisterResource(_mat);
        }
    }


    protected void drawThumbnailImage() {
        if (ImGui.button(("#" + getName()), 64, 64)) {
            if (_assetEditFunction != null) _assetEditFunction.applyAsset(this);
        }
    }

    @Override
    public void use(Scene context) {
        if (_mat == null) return;
        _mat.use(context);
        if (_textures != null) {
            for (int i = 0; i < _textures.size(); ++i) {
                if (_textures.get(i).get() != null) {
                    _mat.setIntParameter("texture" + i, i);
                    glActiveTexture(GL_TEXTURE0 + i);
                    glBindTexture(GL_TEXTURE_2D, _textures.get(i).get().getTextureID());
                }
            }
        }
    }
}
