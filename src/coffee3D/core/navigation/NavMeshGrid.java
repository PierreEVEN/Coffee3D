package coffee3D.core.navigation;

import org.joml.Vector2i;
import org.joml.Vector3f;

import java.io.Serializable;
import java.util.ArrayList;

public class NavMeshGrid implements Serializable {

    private static final long serialVersionUID = -4528707062414566942L;

    private final NavmeshPoint[] _navMesh;
    private final Vector3f _navmeshLocation;
    private final Vector2i _size;
    private final float _cellSize;

    public NavMeshGrid(Vector3f navMeshLocation, Vector2i navmeshSize, float cellSize) {
        _navMesh = new NavmeshPoint[navmeshSize.x * navmeshSize.y];
        _navmeshLocation = navMeshLocation;
        _size = navmeshSize;
        _cellSize = cellSize;
        for (int i = 0; i < _navMesh.length; ++i) {
            _navMesh[i] = new NavmeshPoint();
            _navMesh[i].location.set(i % _size.x, i / _size.x);
            _navMesh[i].isNavigable = false;
        }
    }

    public Vector3f getNavmeshLocation() { return _navmeshLocation; }

    public final NavmeshPoint[] getNavmesh() {
        return _navMesh;
    }

    public float getCellSize() { return _cellSize; }
    public Vector2i getGridSize() { return _size; }

    public NavigationPath findPathToLocation(Vector3f from, Vector3f to) {
        NavmeshPoint start = findClosestNavmeshPoint(from);
        NavmeshPoint end = findClosestNavmeshPoint(to);
        ArrayList<Vector3f> pathPoints = new ArrayList<>();
        return new NavigationPath(null);
    }

    public NavmeshPoint findClosestNavmeshPoint(Vector3f worldPosition) {
        NavmeshPoint closestPoint = null;
        Vector2i searchPoint = new Vector2i();
        worldToLocal(worldPosition, searchPoint);
        float closestDistance = 0;
        for (NavmeshPoint point : _navMesh) {
            if (closestPoint == null) {
                closestPoint = point;
                closestDistance = (float) point.location.gridDistance(searchPoint);
                continue;
            }
            float dist = (float) point.location.gridDistance(searchPoint);
            if (dist < closestDistance) {
                closestPoint = point;
                closestDistance = dist;
            }
        }
        return closestPoint;
    }

    public void worldToLocal(Vector3f world, Vector2i local) {
        local.set(
                (int) ((world.x - _navmeshLocation.x) / _cellSize + _cellSize / 2),
                (int) ((world.y - _navmeshLocation.y) / _cellSize + _cellSize / 2)
        );
    }

    public void localToWorld(Vector2i local, Vector3f world) {
        world.set(
                (local.x * _cellSize) + _navmeshLocation.x,
                (local.y * _cellSize) + _navmeshLocation.y,
                _navmeshLocation.z
        );
    }

    public boolean isLocationInNavmesh(Vector2i location) {
        return location.x >= 0 && location.y >= 0 && location.x < _size.x && location.y < _size.y;
    }

    public boolean isLocationNavigable(Vector2i location) {
        NavmeshPoint point = getPoint(location);
        return point != null && point.isNavigable;
    }

    public NavmeshPoint getPoint(Vector2i position) {
        if (!isLocationInNavmesh(position)) return null;
        return _navMesh[position.x + position.y * _size.x];
    }
}
