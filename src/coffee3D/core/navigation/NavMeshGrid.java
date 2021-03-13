package coffee3D.core.navigation;

import coffee3D.core.io.log.Log;
import com.sun.org.apache.xml.internal.utils.IntVector;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

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
        if (start == null && end == null) return null;
        ArrayList<Vector3f> pathPoints = new ArrayList<>();


        Vector3f fromReal = new Vector3f();
        localToWorld(start.location, fromReal);
        Vector3f toReal = new Vector3f();
        localToWorld(end.location, toReal);

        pathPoints.add(to);
        pathPoints.add(toReal);

        beginPathfindingOperation(start, end);

        if (explorePath(end)) {
            findPathRecursive(start, end, pathPoints);
        }

        pathPoints.add(fromReal);
        pathPoints.add(from);

        Collections.reverse(pathPoints);

        Vector3f[] points = new Vector3f[pathPoints.size()];
        pathPoints.toArray(points);
        return new NavigationPath(points);
    }

    private void findPathRecursive(NavmeshPoint from, NavmeshPoint to, ArrayList<Vector3f> pathPoints) {
        NavmeshPoint currentNode = to;
        while (!currentNode.equals(from)) {
            Vector3f pos = new Vector3f();
            localToWorld(currentNode.location, pos);
            pathPoints.add(pos);
            currentNode = currentNode._source;
        }
        Vector3f pos = new Vector3f();
        localToWorld(currentNode.location, pos);
        pathPoints.add(pos);
    }

    private final static ArrayList<NavmeshPoint> nodesToExplore = new ArrayList<>();

    private void beginPathfindingOperation(NavmeshPoint from, NavmeshPoint to) {
        for (NavmeshPoint node : _navMesh) {
            node.reset(node.isNavigable, calcGCost(node.location, to.location));
        }
        nodesToExplore.clear();
        nodesToExplore.add(from);
        from._isExplored = true;
        from._HCost = 0;
    }

    private boolean explorePath(NavmeshPoint to) {
        while (nodesToExplore.size() > 0) {
            if (ExploreNode(getLowestCostNode(), to)) {
                return true;
            }
        }
        return false;
    }

    private NavmeshPoint getLowestCostNode() {
        int minFCost = 0;
        NavmeshPoint point = null;
        for (NavmeshPoint navmeshPoint : nodesToExplore) {
            if (point == null || navmeshPoint._FCost < minFCost) {
                point = navmeshPoint;
                minFCost = navmeshPoint._FCost;
            }
        }
        return point;
    }

    private static final NavmeshPoint[] _newNodesToExplore = new NavmeshPoint[4];
    private boolean ExploreNode(NavmeshPoint node, NavmeshPoint target) {
        node._isExplored = true;
        nodesToExplore.remove(node);

        _newNodesToExplore[0] = getPoint(node.location.x + 1, node.location.y);
        _newNodesToExplore[1] = getPoint(node.location.x - 1, node.location.y);
        _newNodesToExplore[2] = getPoint(node.location.x, node.location.y + 1);
        _newNodesToExplore[3] = getPoint(node.location.x, node.location.y - 1);

        for (NavmeshPoint newNode : _newNodesToExplore) {
            if (newNode == null || !isLocationNavigable(newNode.location)) continue;

            /* If node is already explored but a shortest path is found, refresh costs */
            if (newNode._isExplored && node._HCost + 1 < newNode._HCost) {
                newNode._source = node;
                newNode._HCost = node._HCost + 1;
            }
            /* If node isn't explored, add it to the pending exploration nodes */
            else if (!newNode._isExplored) {
                newNode._source = node;
                newNode._HCost = node._HCost + 1;
                newNode._isExplored = true;
                nodesToExplore.add(newNode);
            }

            /** If we found the target, return true */
            if (newNode.equals(target)) {
                return true;
            }
        }

        return false;
    }


    private int calcGCost(Vector2i from, Vector2i to) {
        return (int) from.gridDistance(to);
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
            if (point.isNavigable && dist < closestDistance) {
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
        return isLocationInNavmesh(location.x, location.y);
    }
    public boolean isLocationInNavmesh(int posX, int posY) {
        return posX >= 0 && posY >= 0 && posX < _size.x && posY < _size.y;
    }

    public boolean isLocationNavigable(Vector2i location) {
        NavmeshPoint point = getPoint(location);
        return point != null && point.isNavigable;
    }

    public NavmeshPoint getPoint(Vector2i position) {
        return getPoint(position.x, position.y);
    }

    public NavmeshPoint getPoint(int posX, int posY) {
        if (!isLocationInNavmesh(posX, posY)) return null;
        return _navMesh[posX + posY * _size.x];
    }
}
