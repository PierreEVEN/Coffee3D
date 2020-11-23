package coffee3D.core.navigation;

import coffee3D.core.io.log.Log;
import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.renderer.scene.SceneComponent;
import org.joml.Quaternionf;
import org.joml.Random;
import org.joml.Vector3f;

public class NavigableActor extends SceneComponent {

    private static final long serialVersionUID = 2644030132802605634L;

    private transient NavmeshComponent _navmesh;
    private transient NavigationPath _path;
    private final static Vector3f nextPosition = new Vector3f();

    public NavigableActor(Vector3f position, Quaternionf rotation, Vector3f scale) {
        super(position, rotation, scale);
    }


    public void setNavmesh(NavmeshComponent navmesh) {
        _navmesh = navmesh;
    }

    public void aiMoveTo(Vector3f targetLocation) {
        if (_navmesh == null) {
            Log.Error("invalid navmesh");
            return;
        }
        _path = _navmesh.findPathToLocation(getWorldPosition(), targetLocation);
    }

    private static final Random rand = new Random();
    @Override
    protected void tick(Scene context, double deltaTime) {
        if (_navmesh == null) {
            for (SceneComponent comp : context.getComponents()) {
                if (comp instanceof NavmeshComponent) {
                    setNavmesh((NavmeshComponent) comp);
                    aiMoveTo(new Vector3f(rand.nextInt(60) - 30, rand.nextInt(60) - 30, .5f));
                }
            }
        }

        super.tick(context, deltaTime);
        nextPosition.set(getRelativePosition());
        if (_path != null)
        {
            if (_path.move(nextPosition, 4, 1))  {
                _navmesh = null;
            }
        }
        setRelativePosition(nextPosition);
    }
}
