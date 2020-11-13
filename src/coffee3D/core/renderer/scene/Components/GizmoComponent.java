package coffee3D.core.renderer.scene.Components;

import coffee3D.core.io.log.Log;
import coffee3D.core.renderer.AssetReferences;
import coffee3D.core.renderer.debug.DebugRenderer;
import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.renderer.scene.SceneComponent;
import coffee3D.core.resources.factories.ImportZAxis;
import coffee3D.core.resources.factories.MaterialFactory;
import coffee3D.core.resources.factories.MeshFactory;
import coffee3D.core.resources.types.MaterialResource;
import coffee3D.core.resources.types.MeshResource;
import coffee3D.core.types.Color;
import coffee3D.core.types.TypeHelper;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class GizmoComponent extends SceneComponent {
    private static final long serialVersionUID = -5687518908809847596L;

    private static transient MeshResource gizmoMesh[];
    private static transient MaterialResource gizmoMaterial;

    public GizmoComponent(Vector3f position, Quaternionf rotation, Vector3f scale) {
        super(position, rotation, scale);
    }

    @Override
    public void draw(Scene context) {
        if (gizmoMesh == null) gizmoMesh = MeshFactory.FromFile("gizmoMesh", AssetReferences.GIZMO_MESH_PATH, ImportZAxis.ZUp);
        if (gizmoMaterial == null) gizmoMaterial = MaterialFactory.FromFiles("gizmoMaterial", (AssetReferences.GIZMO_MATERIAL_PATH.getPath() + ".vert"), (AssetReferences.GIZMO_MATERIAL_PATH.getPath() + ".frag"));

        if (gizmoMaterial == null || gizmoMesh == null || gizmoMesh.length == 0) {
            Log.Fail("failed to find gizmo asset path");
        }

        gizmoMaterial.use(context);
        gizmoMaterial.setModelMatrix(getWorldTransformationMatrix());
        gizmoMesh[0].use(context);

        DebugRenderer.DrawDebugLine(context, TypeHelper.getVector3(0, 0, 0), getWorldPosition(), Color.RED);
        DebugRenderer.DrawDebugLine(context, TypeHelper.getVector3(0, 0, 10), getWorldPosition(), Color.GREEN);
        DebugRenderer.DrawDebugLine(context, TypeHelper.getVector3(0, 0, 20), getWorldPosition(), Color.BLUE);

    }
}