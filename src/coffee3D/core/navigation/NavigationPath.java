package coffee3D.core.navigation;

import org.joml.Vector3f;

public class NavigationPath {

    private final Vector3f[] _pathPoints;

    NavigationPath(Vector3f[] pathPoints) {
        _pathPoints = pathPoints;
    }
}
