package coffee3D.core.assets.types;

import coffee3D.core.assets.Asset;
import coffee3D.core.audio.AudioSource;
import coffee3D.core.io.log.Log;
import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.resources.ResourceManager;
import coffee3D.core.resources.factories.AudioFactory;
import coffee3D.core.resources.types.WaveResource;
import coffee3D.core.types.Color;

import java.io.File;

public class SoundWave extends Asset {
    private static final long serialVersionUID = 1383091162971736086L;

    protected float _pitch;
    protected float _gain;
    private transient WaveResource _source;
    private static final Color _soundColor = new Color(153/255f, 102/255f, 255/255f, 1);
    private static final String[] _soundExtensions = new String[] {"wav"};

    @Override
    public String[] getAssetExtensions() {
        return _soundExtensions;
    }

    @Override
    public Color getAssetColor() {
        return _soundColor;
    }

    public SoundWave(String assetName, File sourceFilePath, File assetPath) { super(assetName, sourceFilePath, assetPath); }

    public WaveResource getResource() { return _source; }

    @Override
    public void load() {
        _source = AudioFactory.FromFile(getName() + "_resource", getSourcePath());
        AudioSource src = new AudioSource();
        src.setSource(this);
    }

    @Override
    public void reload() {
        ResourceManager.UnRegisterResource(_source);
        WaveResource newSource = null;
        try {
            newSource = AudioFactory.FromFile(getName() + "_resource", getSourcePath());
        }
        catch (Exception e) {
            Log.Warning("failed to load audio source : " + e.getMessage());
        }

        if (newSource != null) {
            _source = newSource;
        }
        else {
            ResourceManager.RegisterResource(newSource);
        }
    }

    @Override
    public void use(Scene context) {

    }
}
