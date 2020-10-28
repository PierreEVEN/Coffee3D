package Core.Assets.Types;

import Core.Assets.AssetReference;
import Core.Factories.MaterialFactory;
import Core.IO.LogOutput.Log;
import Core.Resources.MaterialResource;
import Core.Resources.ResourceManager;
import Core.Types.Color;

import java.io.File;

public class Material extends MaterialInterface {

    private static final long serialVersionUID = -2932087609993578842L;

    private transient MaterialResource _mat;

    private static final Color _matAssetColor = new Color(0f, .5f, 0f, 1);
    private static final String[] materialExtensions = new String[] {"vert"};

    public Material(String name, File filePath, File assetPath, AssetReference<Texture2D>[] textures) {
        super(name, filePath, assetPath, textures);
    }

    @Override
    public String[] getAssetExtensions() {
        return materialExtensions;
    }

    @Override
    public Color getAssetColor() {
        return _matAssetColor;
    }

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

    @Override
    public MaterialResource getResource() { return _mat; }
}
