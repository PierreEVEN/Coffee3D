package Core.UI;

import Core.IO.LogOutput.Log;
import Core.Renderer.Scene.RenderScene;
import imgui.ImGui;

public class DrawScenWindow {

    public static void Draw(RenderScene inScene) {

        int sizeX = (int)ImGui.getContentRegionAvailX();
        int sizeY = (int)ImGui.getContentRegionAvailY();

        if (sizeX != inScene.getFramebuffer().getWidth() || sizeY != inScene.getFramebuffer().getHeight()) {
            inScene.getFramebuffer().resizeFramebuffer(sizeX, sizeY);
        }


        ImGui.image(
                inScene.getFramebuffer().getColorBuffer(),
                inScene.getFramebuffer().getWidth(),
                inScene.getFramebuffer().getHeight(),
                0, 1, 1, 0);



    }

}
