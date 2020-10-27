package Core.Maths;

import Core.IO.LogOutput.Log;
import Core.Renderer.Scene.RenderScene;
import Core.Renderer.Scene.Scene;
import Core.Renderer.Scene.SceneComponent;
import Core.Types.SphereBound;
import Core.Types.TypeHelper;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class MathLibrary {

    private static Vector3f vec0 = new Vector3f();


    public static float GetPointDistanceToLine(Vector3f direction, Vector3f origin, Vector3f pointLocation) {
        Vector3f lineToPointVector = TypeHelper.getVector3(origin).sub(pointLocation);
        Vector3f surfaceNormal = TypeHelper.getVector3(direction).cross(lineToPointVector);
        Vector3f normal =  TypeHelper.getVector3(surfaceNormal).cross(direction).normalize();

        if (surfaceNormal.x == 0f && surfaceNormal.y == 0f && surfaceNormal.z == 0f) return 0f;
        return Math.abs(TypeHelper.getVector3(normal).dot(lineToPointVector));
    }

    public static float GetPointDistanceToSegment(Vector3f posA, Vector3f posB, Vector3f point) {
        Vector3f segmentsDirection = TypeHelper.getVector3(posB).sub(posA);
        Vector3f targetDirectionA = TypeHelper.getVector3(point).sub(posA);
        if (TypeHelper.getVector3(segmentsDirection).dot(targetDirectionA) < 0) return point.distance(posA);
        Vector3f targetDirectionB = TypeHelper.getVector3(point).sub(posB);
        segmentsDirection.mul(-1f);
        if (TypeHelper.getVector3(segmentsDirection).dot(targetDirectionB) < 0) return point.distance(posB);
        return GetPointDistanceToLine(TypeHelper.getVector3(posA).sub(posB).normalize(), posA, point);
    }

    public static void PixelToSceneDirection(RenderScene targetScene, float viewportRelativeX, float viewportRelativeY, Vector3f result) {
        float localPosX = (viewportRelativeX / targetScene.getFbWidth()) * 2 - 1;
        float localPosY = -1 * ((viewportRelativeY / targetScene.getFbHeight()) * 2 - 1);

        Matrix4f invMat = TypeHelper.getMat4().set(targetScene.getProjection(targetScene.getFbWidth(), targetScene.getFbHeight(), targetScene.getCamera())).mul(targetScene.getCamera().getViewMatrix()).invert();
        Vector4f nearPos = TypeHelper.getVector4(localPosX, localPosY, -1f, 1f);
        Vector4f farPos = TypeHelper.getVector4(localPosX, localPosY, 1f, 1f);
        Vector4f nearResult = nearPos.mul(invMat);
        Vector4f farResult = farPos.mul(invMat);
        nearResult.div(nearResult.w);
        farResult.div(farResult.w);
        result.set(farResult.x, farResult.y, farResult.z).sub(nearResult.x, nearResult.y, nearResult.z).normalize();
    }
}
