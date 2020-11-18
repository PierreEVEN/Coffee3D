package coffee3D.core.renderer.scene.Components;

import coffee3D.core.assets.types.StaticMesh;
import coffee3D.core.io.log.Log;
import coffee3D.core.navigation.NavmeshComponent;
import coffee3D.core.renderer.scene.RenderScene;
import coffee3D.core.renderer.scene.SceneComponent;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ComponentManager {

    private static final List<Class<?>> components = new ArrayList<>();

    private static void RegisterComponent(Class<?> componentClass) {
        components.add(componentClass);
    }

    public static List<Class<?>> GetComponents() { return components; }

    public static void CreateComponent(Class<?> componentClass, RenderScene scene, Vector3f relativePosition) {
        try {
            Constructor ctor = componentClass.getConstructor(Vector3f.class, Quaternionf.class, Vector3f.class);
            if (ctor != null) {
                SceneComponent component = (SceneComponent) componentClass.getConstructor(Vector3f.class, Quaternionf.class, Vector3f.class)
                        .newInstance(relativePosition, new Quaternionf().identity(), new Vector3f(1));
                component.attachToScene(scene);
            }
            else {
                Log.Error("Failed to find default constructor");
            }
        } catch (Exception e) {
            Log.Error("failed to create component : " + e.getMessage());
        }
    }

    public static void RegisterBaseComponents() {
        RegisterComponent(SceneComponent.class);
        RegisterComponent(StaticMeshComponent.class);
        RegisterComponent(BillboardComponent.class);
        RegisterComponent(AudioComponent.class);
        RegisterComponent(NavmeshComponent.class);
    }
}
