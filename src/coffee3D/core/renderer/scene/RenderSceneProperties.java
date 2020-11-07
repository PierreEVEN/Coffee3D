package coffee3D.core.renderer.scene;

import coffee3D.core.io.log.Log;
import coffee3D.core.types.Color;
import coffee3D.core.types.TypeHelper;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class RenderSceneProperties extends SceneProperty {
    private static final long serialVersionUID = -7591302437531604737L;
    public Color _backgroundColor = new Color(0,0,0,1);

    public float sunPower = 1;
    public Quaternionf sunOrientation;
    private static final Vector3f zAxis = new Vector3f(0, 0, -1);
    public float shadowIntensity = 1;

    public Vector3f getSunVector() {
        if (sunOrientation == null) {
            sunPower = 1;
            sunOrientation = new Quaternionf().identity().rotateXYZ((float)Math.toRadians(90), (float)Math.toRadians(20), 0);
        }
        Vector3f sunVector = TypeHelper.getVector3();
        sunOrientation.positiveY(sunVector);

        return sunVector.mul((float) (sunPower * -1 * (Math.max(0, sunVector.dot(zAxis) + .5))));
    }
}