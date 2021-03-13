package coffee3D.core.animation;

import coffee3D.core.renderer.scene.Components.StaticMeshComponent;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.Serializable;

public class AnimNode implements Serializable {

    private static final long serialVersionUID = -1465415282022180611L;

    protected AnimNode(String componentName, float nodeTime) {
        _componentName = componentName;
        _nodeTime = nodeTime;
    }

    private transient StaticMeshComponent component;
    private final String _componentName;
    protected float _nodeTime;
    protected final Vector3f _nodePosition = new Vector3f();
    protected final Quaternionf _nodeRotation = new Quaternionf();
    protected final Vector3f _nodeScale = new Vector3f();
}
