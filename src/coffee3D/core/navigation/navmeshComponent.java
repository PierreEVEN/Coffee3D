package coffee3D.core.navigation;

import coffee3D.core.renderer.scene.SceneComponent;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class navmeshComponent extends SceneComponent {

    public navmeshComponent(Vector3f position, Quaternionf rotation, Vector3f scale) {
        super(position, rotation, scale);
    }


}
