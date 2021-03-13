package coffee3D.core.renderer.scene;

import coffee3D.core.assets.types.StaticMesh;
import coffee3D.core.renderer.scene.Components.StaticMeshComponent;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DrawList {

    private final FrustumIntersection _frustum = new FrustumIntersection();
    private final List<SceneComponent> frustumDrawList = new ArrayList<>();
    private final Matrix4f projectionMatrix = new Matrix4f();

    public void build(List<SceneComponent> components, Matrix4f viewMatrix, Matrix4f viewportMatrix) {
        fill(components, viewMatrix, viewportMatrix);
        sortDrawList();
    }

    private void fill(List<SceneComponent> components, Matrix4f viewMatrix, Matrix4f projection) {
        frustumDrawList.clear();
        _frustum.set(projectionMatrix.set(projection).mul(viewMatrix));
        for (int i = 0; i < components.size(); ++i) {
            SceneComponent component = components.get(i);
            component.setComponentIndex(i);
            if (_frustum.testSphere(component.getBound().position, component.getBound().radius)) {
                frustumDrawList.add(component);
            }
        }
    }

    private void sortDrawList() {
        frustumDrawList.sort((o1, o2) -> {
            if (o1 instanceof StaticMeshComponent && o2 instanceof StaticMeshComponent)
            {
                StaticMesh s1 = ((StaticMeshComponent)o1).getStaticMesh();
                StaticMesh s2 = ((StaticMeshComponent)o2).getStaticMesh();
                if (s1 != null && s2 != null) return s1.getName().compareTo(s2.getName());
            }
            return o1.getClass().hashCode() - o2.getClass().hashCode();
        });
    }

    public void preRender(RenderScene context) {
        for (SceneComponent component : frustumDrawList) component.preDrawInternal(context);
    }

    public void render(RenderScene context) {
        for (SceneComponent component : frustumDrawList) component.drawInternal(context);
    }

    public void postRender(RenderScene context) {
        for (SceneComponent component : frustumDrawList) component.postDrawInternal(context);
    }
}
