package coffee3D.editor.ui.animationEditor;

import coffee3D.core.animation.Animation;
import coffee3D.core.animation.SkeletalMesh;
import coffee3D.core.assets.AssetReference;
import coffee3D.core.io.log.Log;
import coffee3D.core.renderer.scene.Components.ChildSceneComponent;
import coffee3D.core.renderer.scene.RenderScene;
import coffee3D.core.renderer.scene.RenderSceneSettings;
import coffee3D.editor.ui.SceneViewport;
import coffee3D.editor.ui.propertyHelper.writers.AssetButton;
import imgui.ImGui;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class AnimationViewport extends SceneViewport {

    private final AssetReference<Animation> _currentAnimation = new AssetReference<Animation>(Animation.class);

    public AnimationViewport(SkeletalMesh mesh, String windowName) {
        super(new RenderScene(RenderSceneSettings.DEFAULT_WINDOWED), windowName + " : " + mesh.getName());

        ChildSceneComponent child = new ChildSceneComponent(mesh, new Vector3f(0), new Quaternionf().identity(), new Vector3f(1));
        child.attachToScene(getScene());
        new AnimationOutliner(mesh, "testOutliner");
    }

    @Override
    protected void draw() {
        ImGui.columns(2);
        ImGui.text("selected animation");
        ImGui.sameLine();
        AssetButton.Draw("selected", _currentAnimation);

        ImGui.text("test");

        ImGui.nextColumn();
        getScene().renderScene();
        super.draw();
        ImGui.columns(1);
    }

    protected void drawComponentTree() {

    }

    @Override
    public void close() {
        super.close();
        getScene().delete();
    }
}
