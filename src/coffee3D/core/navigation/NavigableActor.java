package coffee3D.core.navigation;

import coffee3D.core.renderer.scene.SceneComponent;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class NavigableActor extends SceneComponent {


    public NavigableActor(Vector3f position, Quaternionf rotation, Vector3f scale) {
        super(position, rotation, scale);
    }
}
