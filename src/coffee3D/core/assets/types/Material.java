package coffee3D.core.assets.types;

import coffee3D.core.assets.AssetReference;
import coffee3D.core.resources.factories.MaterialFactory;
import coffee3D.core.io.log.Log;
import coffee3D.core.resources.types.MaterialResource;
import coffee3D.core.resources.ResourceManager;
import coffee3D.core.types.Color;

import java.io.File;

public class Material extends MaterialInterface {

    private static final long serialVersionUID = -2932087609993578842L;

    private transient MaterialResource _mat;

    private static final Color _matAssetColor = new Color(99/255f, 224/255f, 106/255f, 1);
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
