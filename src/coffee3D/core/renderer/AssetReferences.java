package coffee3D.core.renderer;

import coffee3D.core.assets.types.Material;
import coffee3D.core.assets.types.Texture2D;
import coffee3D.core.io.log.Log;
import coffee3D.core.io.settings.EngineSettings;

import java.io.File;

public final class AssetReferences {

    public static File ICON_SCENE_COMPONENT = new File("engineContent/assets/textures/icon/itemIcon.png");
    public static File ICON_STATIC_MESH = new File("engineContent/assets/textures/icon/meshIcon.png");
    private static Texture2D _icon_scene_component_texture;
    private static Texture2D _icon_static_mesh_component_texture;

    public static Texture2D GetIconSceneComponent() {
        if (_icon_scene_component_texture == null) {
            _icon_scene_component_texture = loadTexture("icon_scene_component_texture", ICON_SCENE_COMPONENT);
        }
        return _icon_scene_component_texture;
    }
    public static Texture2D GetIconMesh() {
        if (_icon_static_mesh_component_texture == null) {
            _icon_static_mesh_component_texture = loadTexture("_icon_static_mesh_component_texture", ICON_STATIC_MESH);
        }
        return _icon_static_mesh_component_texture;
    }



    private static Texture2D loadTexture(String name, File path) {
        Texture2D result;
        result = new Texture2D(name, path, null);
        if (result == null) {
            Log.Fail("failed to load texture " + name + " from : " + path);
        }
        RenderUtils.CheckGLErrors();
        return result;
    }

}
