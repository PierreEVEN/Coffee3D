package coffee3D.core.maths;

import coffee3D.core.io.log.Log;
import coffee3D.core.renderer.scene.RenderScene;
import coffee3D.core.types.TypeHelper;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class MathLibrary {

    public final static Vector3f lineToPointVector = new Vector3f();
    public final static Vector3f surfaceNormal = new Vector3f();
    public final static Vector3f normal = new Vector3f();
    public final static Vector3f dir1 = new Vector3f();
    public final static Vector3f dir2 = new Vector3f();
    public final static Vector3f dir3 = new Vector3f();


    public static float GetPointDistanceToLine(Vector3f direction, Vector3f origin, Vector3f pointLocation) {
        lineToPointVector.set(origin).sub(pointLocation);
        surfaceNormal.set(direction).cross(lineToPointVector);
        normal.set(surfaceNormal).cross(direction).normalize();

        if (surfaceNormal.x == 0f && surfaceNormal.y == 0f && surfaceNormal.z == 0f) return 0f;
        return Math.abs(normal.dot(lineToPointVector));
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



    public static double GetSegmentDistanceToLine(Vector3f lineOrigin, Vector3f lineDirection, Vector3f segmentP1, Vector3f segmentP2) {
        dir1.set(segmentP1).sub(lineOrigin).normalize();
        dir2.set(segmentP2).sub(lineOrigin).normalize();
        dir3.set(segmentP1).sub(segmentP2).normalize();

        float d1 = TypeHelper.getVector3(dir3).dot(dir1);
        float d2 = TypeHelper.getVector3(dir3).dot(dir2);
        float dvc = TypeHelper.getVector3(dir3).dot(lineDirection);

        if (d2 < d1) {
            float temp = d1;
            d1 = d2;
            d2 = temp;
        }

        if (d1 > dvc || d2 < dvc) {
            return Math.min(GetPointDistanceToLine(lineDirection, lineOrigin, segmentP1), GetPointDistanceToLine(lineDirection, lineOrigin, segmentP2));
        }

        dir1.set(segmentP2).sub(segmentP1);
        lineToPointVector.set(lineDirection).cross(dir1);
        normal.set(lineOrigin).sub(segmentP1);
        float distance = Math.abs(normal.dot(lineToPointVector));

        return distance;
    }
}
