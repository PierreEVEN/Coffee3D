package coffee3D.core.renderer.scene.Components;

import coffee3D.core.assets.AssetReference;
import coffee3D.core.assets.types.SoundWave;
import coffee3D.core.audio.AudioSource;
import coffee3D.core.renderer.AssetReferences;
import coffee3D.core.renderer.debug.DebugRenderer;
import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.renderer.scene.SceneComponent;
import coffee3D.core.resources.types.TextureResource;
import coffee3D.core.types.Color;
import imgui.type.ImBoolean;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class AudioComponent extends SceneComponent {
    private static final long serialVersionUID = -268812367789246679L;

    protected AssetReference<SoundWave> sourceWave;
    protected transient AudioSource source;
    protected transient SoundWave lastAudio;

    public static final ImBoolean DEBUG_DRAW_AUDIO_BOUNDS = new ImBoolean(false);

    @Override
    public TextureResource getComponentIcon() {
        return AssetReferences.GetIconAudio().getResource();
    }

    public AudioComponent(SoundWave audioSource, Vector3f position, Quaternionf rotation, Vector3f scale) {
        super(position, rotation, scale);
        sourceWave = new AssetReference(SoundWave.class, audioSource);
    }

    @Override
    protected void draw(Scene context) {
        super.draw(context);
        if (DEBUG_DRAW_AUDIO_BOUNDS.get()) DebugRenderer.DrawDebugSphere(context, getWorldPosition(), 20, 10, Color.RED);

        if (lastAudio != sourceWave.get()) {
            if (source == null) source = new AudioSource();
            lastAudio = sourceWave.get();
            source.setSource(lastAudio);
        }
    }
}
