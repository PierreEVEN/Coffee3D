package coffee3D.core.renderer.scene.Components;

import coffee3D.core.assets.AssetReference;
import coffee3D.core.assets.types.Texture2D;
import coffee3D.core.io.settings.EngineSettings;
import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.renderer.scene.SceneComponent;
import coffee3D.core.resources.factories.MaterialFactory;
import coffee3D.core.resources.factories.MeshFactory;
import coffee3D.core.resources.types.MaterialResource;
import coffee3D.core.resources.types.MeshResource;
import coffee3D.core.types.Vertex;
import com.sun.xml.internal.ws.api.pipe.Engine;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class BillboardComponent extends SceneComponent {

    private static final long serialVersionUID = -8089244852841439545L;

    protected AssetReference<Texture2D> _texture;
    protected float _scale;
    protected float _rotation;

    private static MeshResource _billboardMesh;
    private static MaterialResource _billboardMaterial;
    private static MeshResource GetBillboardMesh() {
        if (_billboardMesh == null) {
            Vertex[] vertices = new Vertex[]{
                    new Vertex(new Vector3f(-1, -1, 0), new Vector2f(0, 0)),
                    new Vertex(new Vector3f(1, -1, 0), new Vector2f(1, 0)),
                    new Vertex(new Vector3f(1, 1, 0), new Vector2f(1, 1)),
                    new Vertex(new Vector3f(-1, 1, 0), new Vector2f(0, 1)),
            };
            int[] triangles = new int[]{0, 1, 2, 0, 2, 3};
            _billboardMesh = MeshFactory.FromResources("BillboardMesh", vertices, triangles);
        }
        return _billboardMesh;
    }
    private static MaterialResource GetBillboardMaterial() {
        if (_billboardMaterial == null) {
            _billboardMaterial = MaterialFactory.FromFiles("BillboardMaterial", EngineSettings.BILLBOARD_MATERIAL_PATH + ".vert", EngineSettings.BILLBOARD_MATERIAL_PATH + ".frag");
        }
        return _billboardMaterial;
    }


    public BillboardComponent(Texture2D texture, Vector3f position, Quaternionf rotation, Vector3f scale) {
        super(position, rotation, scale);
        _texture = new AssetReference(Texture2D.class, texture);
        _scale = 1f;
        _rotation = 0;
    }


    @Override
    protected void draw(Scene context) {
        GetBillboardMaterial().use(context);
        GetBillboardMesh().use(context);
    }
}
