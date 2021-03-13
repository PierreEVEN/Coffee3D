package coffee3D.core.navigation;

import coffee3D.core.io.log.Log;
import coffee3D.core.maths.Interpolation;
import coffee3D.core.renderer.Window;
import coffee3D.core.renderer.debug.DebugRenderer;
import coffee3D.core.renderer.scene.RenderScene;
import coffee3D.core.types.Color;
import org.joml.Vector3f;


public class NavigationPath {

    private final Vector3f[] _pathPoints;
    private int currentIndex;

    NavigationPath(Vector3f[] pathPoints) {
        _pathPoints = pathPoints;
        currentIndex = 0;
    }

    public void debugDraw(RenderScene context) {
        for (int i = 1; i < _pathPoints.length; ++i) {
            DebugRenderer.DrawDebugLine(context, _pathPoints[i - 1], _pathPoints[i], Color.RED);
        }
    }

    private static final Vector3f _direction = new Vector3f();

    public boolean move(Vector3f position, float speed, float smoothness) {
        if (currentIndex >= _pathPoints.length) return true;
        if (_pathPoints[currentIndex].distance(position) < 0.05) {
            currentIndex++;
            if (currentIndex >= _pathPoints.length) return true;
        }


        _direction.set(_pathPoints[currentIndex]).sub(position).normalize();

        double delta = Window.GetPrimaryWindow().getDeltaTime();

        if (position.distance(_pathPoints[currentIndex]) < speed * delta) {
            _direction.mul(position.distance(_pathPoints[currentIndex]));
            currentIndex++;
        }
        else {
            _direction.mul((float) (speed * delta));
        }
        position.add(_direction);
        return false;
    }
}
