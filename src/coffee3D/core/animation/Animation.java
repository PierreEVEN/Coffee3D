package coffee3D.core.animation;

import coffee3D.core.assets.Asset;
import coffee3D.core.renderer.scene.Scene;

import java.io.File;
import java.util.ArrayList;

public class Animation extends Asset {
    private static final long serialVersionUID = 9027368482940015603L;

    protected Animation(String assetName, File sourceFilePath, File assetPath) {
        super(assetName, sourceFilePath, assetPath);
    }

    private final ArrayList<AnimNode> _nodes = new ArrayList<>();

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
