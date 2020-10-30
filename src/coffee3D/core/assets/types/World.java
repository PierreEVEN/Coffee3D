package coffee3D.core.assets.types;

import coffee3D.core.assets.Asset;
import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.types.Color;

import java.io.File;

public class World extends Asset {
    private static final long serialVersionUID = 243302515535612352L;
    private transient static final Color worldAssetColor = new Color(197/255f, 159/255f, 53/255f, 1);

    protected World(String name, File sourcePath, File assetPath) {
        super(name, sourcePath, assetPath);
    }

    @Override
    public void load() {

    }

    @Override
    public void reload() {

    }

    @Override
    public void use(Scene context) {





    }
}
