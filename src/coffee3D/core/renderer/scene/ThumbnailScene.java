package coffee3D.core.renderer.scene;

import coffee3D.core.assets.Asset;
import coffee3D.core.assets.AssetManager;
import coffee3D.core.assets.types.MaterialInterface;
import coffee3D.core.assets.types.StaticMesh;
import coffee3D.core.renderer.scene.Components.StaticMeshComponent;
import coffee3D.core.types.TypeHelper;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ThumbnailScene extends RenderScene {

    private static ThumbnailScene _instance;
    public static ThumbnailScene Get() {
        if (_instance == null) {
            _instance = new ThumbnailScene();
        }
        return _instance;
    }


    private final StaticMeshComponent object;

    private ThumbnailScene() {
        super(RenderSceneSettings.DEFAULT_THUMBNAIL);
        object = new StaticMeshComponent(
                null,
                new Vector3f(0,0,0),
                new Quaternionf().identity(),
                new Vector3f(1,1,1));
        object.attachToScene(this);

        StaticMeshComponent background = new StaticMeshComponent(
                null,
                new Vector3f(0, 0, 0),
                new Quaternionf().identity(),
                new Vector3f(-700, -700, -700));
        background.attachToScene(this);
        background.setStaticMesh(AssetManager.FindAsset("default_sphere"));
        background.setMaterial(AssetManager.FindAsset("skyboxMaterial"), 0);
        resizeBuffers(Asset.THUMBNAIL_RESOLUTION.x, Asset.THUMBNAIL_RESOLUTION.y);
        getCamera().setYawInput(20);
        getCamera().setPitchInput(30);
    }

    public void use(MaterialInterface material, StaticMesh mesh) {
        if (mesh == null && material != null) {
            object.setStaticMesh(AssetManager.FindAsset("default_sphere"));
            object.setMaterial(material, 0);
        }
        else if (mesh != null) {
            object.setStaticMesh(mesh);
            object.setMaterial(null, 0);
        }
        getCamera().setRelativePosition(
                TypeHelper.getVector3(getCamera().getForwardVector())
                        .mul(object.getBound().radius * -3)
                        .add(object.getBound().position));

        renderScene();
    }

}
