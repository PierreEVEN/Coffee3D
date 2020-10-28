package Core.Assets;

import Core.Factories.MaterialFactory;
import Core.IO.LogOutput.Log;
import Core.Renderer.Scene.Scene;
import Core.Resources.MaterialResource;
import Core.Resources.ResourceManager;
import Core.Types.Color;
import imgui.ImGui;

import java.io.File;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL46.*;

public class Material extends Asset {

    private static final long serialVersionUID = -2932087609993578842L;
    private transient MaterialResource _mat;
    protected Color _materialColor = new Color(1f, 1f, 1f, 1f);
    protected ArrayList<AssetReference<Texture2D>> _textures = new ArrayList<>();

    private static final Color _matAssetColor = new Color(0f, .5f, 0f, 1);
    private static final String[] materialExtensions = new String[] {"vert"};

    public Color getColor() { return _materialColor; }
    public void setColor(Color color) { _materialColor = color; }

    public Material(String name, File filePath, File assetPath) {
        super(name, filePath, assetPath);
    }

    public Material(String name, File filePath, File assetPath, String[] textureNames) {
        super(name, filePath, assetPath);
        for (String texture : textureNames) {
            _textures.add(new AssetReference<>(Texture2D.class, texture));
        }
    }

    @Override
    public String[] getAssetExtensions() {
        return materialExtensions;
    }

    @Override
    public Color getAssetColor() {
        return _matAssetColor;
    }

    public MaterialResource getShader() { return _mat; }

    @Override
    public void load() {
        String fileCleanName = getSourcePath().getPath().replaceFirst("[.][^.]+$", "");
        _mat = MaterialFactory.FromFiles(getName(), fileCleanName + ".vert", fileCleanName + ".frag");
    }

    @Override
    public void reload() {
        String fileCleanName = getSourcePath().getPath().replaceFirst("[.][^.]+$", "");
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
        _mat.setColorParameter("color", _materialColor);
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
