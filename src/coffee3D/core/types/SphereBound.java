package coffee3D.core.types;

import org.joml.Vector3f;

public class SphereBound {
    public SphereBound() {
        position = new Vector3f().zero();
        radius = 0;
    }
    public Vector3f position;
    public float radius;

    public static final SphereBound ZERO = new SphereBound();
    private static final SphereBound _pointInst = new SphereBound();
    public static final SphereBound GetPoint(Vector3f pos) {
        _pointInst.position = pos;
        return _pointInst;
    }
}
