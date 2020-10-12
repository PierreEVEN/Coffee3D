package Core.Assets;

import Core.Factories.MaterialFactory;
import Core.Renderer.Scene.Scene;
import Core.Resources.MaterialResource;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL46.*;

public class Material extends Asset {

    private transient MaterialResource _mat;
    private final String[] _textureNames;

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

    public List<Texture2D> getTextures() {
        List<Texture2D> res = new ArrayList<>();
        if (_textureNames == null) return res;
        for (String texture : _textureNames) {
            Texture2D foundTexture = AssetManager.FindAsset(texture);
            if (foundTexture != null) res.add(foundTexture);
        }
        return res;
    }

    @Override
    public void use(Scene context) {
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
