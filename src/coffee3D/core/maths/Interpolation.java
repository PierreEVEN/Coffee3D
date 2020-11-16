package coffee3D.core.maths;

import coffee3D.core.renderer.Window;
import org.joml.Vector3f;

public class Interpolation {
    public static float FInterpTo(float from, float to, float speed) {
        float deltaSpeed = (float) Window.GetPrimaryWindow().getDeltaTime() * speed;
        return from * (1 - deltaSpeed) + to * deltaSpeed;
    }

    public static float FInterpToConstant(float from, float to, float speed) {
        float deltaSpeed = (float) Window.GetPrimaryWindow().getDeltaTime() * speed;
        if (to - from > 0) return Math.min(to, from + deltaSpeed);
        else return Math.max(to, from - deltaSpeed);
    }

    public static void VInterpTo(Vector3f from, Vector3f to, float speed) {

    }
}
