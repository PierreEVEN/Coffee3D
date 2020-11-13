package coffee3D.core.renderer;

import coffee3D.core.assets.types.Texture2D;
import coffee3D.core.io.log.Log;
import coffee3D.core.io.settings.EngineSettings;

import java.io.File;

public final class AssetReferences {


    public static File POST_PROCESS_MATERIAL = new File("engineContent/assets/shaders/postProcessInternal");
    public static File SHADOW_MATERIAL_PATH = new File("engineContent/assets/shaders/internal/shadowShader");
    public static File DEBUG_MATERIAL_PATH = new File("engineContent/assets/shaders/debugMaterial");
    public static File BILLBOARD_PICK_MATERIAL_PATH = new File("engineContent/assets/shaders/billboardPickMaterial");
    public static File OUTLINE_MATERIAL_PATH = new File("engineContent/assets/shaders/outlineMaterial");
    public static File PICK_MATERIAL_PATH = new File("engineContent/assets/shaders/pickMaterial");
    public static File BILLBOARD_MATERIAL_PATH = new File("engineContent/assets/shaders/billboardMaterial");

    public static File GIZMO_MESH_PATH = new File(EngineSettings.Get().engineAssetsPath.getPath() + "/assets/models/gizmo.fbx");
    public static File GIZMO_MATERIAL_PATH = new File(EngineSettings.Get().engineAssetsPath.getPath() + "/assets/shaders/gizmoMaterial");

    public static File ICON_SCENE_COMPONENT = new File(EngineSettings.Get().engineAssetsPath.getPath() + "/assets/textures/icon/itemIcon.png");
    public static File ICON_STATIC_MESH = new File(EngineSettings.Get().engineAssetsPath.getPath() + "/assets/textures/icon/meshIcon.png");
    public static File ICON_AUDIO = new File(EngineSettings.Get().engineAssetsPath.getPath() + "/assets/textures/icon/audioIcon.png");
    private static Texture2D _icon_scene_component_texture;
    private static Texture2D _icon_static_mesh_component_texture;
    private static Texture2D _icon_audio_component;

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
    public static Texture2D GetIconAudio() {
        if (_icon_audio_component == null) {
            _icon_audio_component = loadTexture("_icon_static_mesh_component_texture", ICON_AUDIO);
        }
        return _icon_audio_component;
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
