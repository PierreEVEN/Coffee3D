package coffee3D.core.animation;

import coffee3D.core.assets.AssetReference;
import coffee3D.core.renderer.scene.SceneComponent;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SkeletalMeshComponent extends SceneComponent {


    private static final long serialVersionUID = -4473509192515344898L;

    /**
     * constructor
     *
     * @param position relative position
     * @param rotation relative rotation
     * @param scale    relative scale
     */
    public SkeletalMeshComponent(Vector3f position, Quaternionf rotation, Vector3f scale) {
        super(position, rotation, scale);
    }

    protected final AssetReference<SkeletalMesh> _mesh = new AssetReference<SkeletalMesh>(SkeletalMesh.class);
    protected final AssetReference<Animation> _animation = new AssetReference<Animation>(Animation.class);

}
