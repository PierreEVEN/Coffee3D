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
    private String[] _textureNames;

    public Material(String name, String filePath) {
        super(name, filePath);
        _textureNames = null;
    }

    public Material(String name, String filePath, String[] textureNames) {
        super(name, filePath);
        _textureNames = textureNames;
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

    public List<Texture2D> getTextures() {
        List<Texture2D> res = new ArrayList<>();
        if (_textureNames == null) return res;
        for (String texture : _textureNames) {
            Texture2D foundTexture = AssetManager.FindAsset(texture);
            if (foundTexture != null) res.add(foundTexture);
        }
        return res;
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
        List<Texture2D> textures = getTextures();
        if (_textureNames != null) {
            for (int i = 0; i < textures.size(); ++i) {
                _mat.setIntParameter("texture" + i, i);
                glActiveTexture(GL_TEXTURE0 + i);
                glBindTexture(GL_TEXTURE_2D, textures.get(i).getTextureID());
            }
        }
    }
}
