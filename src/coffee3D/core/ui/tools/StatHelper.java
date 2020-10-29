package coffee3D.core.ui.tools;

import coffee3D.core.assets.AssetManager;
import coffee3D.core.renderer.scene.RenderScene;
import coffee3D.core.renderer.Window;
import coffee3D.core.resources.ResourceManager;
import coffee3D.core.types.TypeHelper;
import imgui.ImGui;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;

public class StatHelper {

    private static final float[] framerateHistory = new float[120];
    private static int fpsIndex = 0;
    private static float lastFpsUpdate = 0;
    private static float averageFps = 0;
    public static void DrawStats(RenderScene context) {
        ImGui.beginChild("stats", 400, 500);
        DrawFpsBox();

        ImGui.separator();

        ImGui.text("components : " + context.getComponents().size());
        ImGui.text("resources : " + ResourceManager.GetResources().size());
        ImGui.text("assets : " + AssetManager.GetAssets().size());

        ImGui.separator();
        TypeHelper.DrawStats();
        ImGui.separator();
        PrintGCStats();

        ImGui.endChild();
    }

    private static void DrawFpsBox() {

        float fps = (float) (1f / Window.GetPrimaryWindow().getDeltaTime());

        if (lastFpsUpdate > 1 / 60f || Window.GetPrimaryWindow().getDeltaTime() > (1/30f)) {
            lastFpsUpdate = 0;
            fpsIndex = ((fpsIndex + 1) % framerateHistory.length);
            framerateHistory[fpsIndex] = fps;
        }
        lastFpsUpdate += (float) Window.GetPrimaryWindow().getDeltaTime();

        averageFps = 0;
        float min = framerateHistory[0];
        float max = framerateHistory[0];
        for (float val : framerateHistory) {
            averageFps += val;
            if (val < min) min = val;
            if (val > max) max = val;
        }
        averageFps /= framerateHistory.length;

        ImGui.plotLines("framerate : max = " + max, framerateHistory, framerateHistory.length, fpsIndex, "average : " + (int)(averageFps) + " fps", 0, averageFps * 1.4f, 300, 100);

        ImGui.text(String.format ("max delta time : %.5f", 1 / min));
    }

    private static long garbageCollectionValue = 0;
    private static long garbageCollectionTimeValue = 0;
    private static long lastGarbageCollection = 0;
    private static long lastGarbageCollectionTime = 0;

    public static void PrintGCStats() {

        long garbageCollection = 0;
        long garbageCollectionTime = 0;


        for(GarbageCollectorMXBean gc :
                ManagementFactory.getGarbageCollectorMXBeans()) {

            long count = gc.getCollectionCount();

            if(count >= 0) {
                garbageCollection += count;
            }

            long time = gc.getCollectionTime();

            if(time >= 0) {
                garbageCollectionTime += time;
            }

        }

        if (garbageCollection != lastGarbageCollection || garbageCollectionTime != lastGarbageCollectionTime) {
            garbageCollectionValue = garbageCollection - lastGarbageCollection;
            garbageCollectionTimeValue = garbageCollectionTime - lastGarbageCollectionTime;
            lastGarbageCollection = garbageCollection;
            lastGarbageCollectionTime = garbageCollectionTime;
        }

        ImGui.text("Total Garbage Collections: " + garbageCollectionValue);
        ImGui.text("Total Garbage Collection Time (ms): " + garbageCollectionTimeValue);
        if (ImGui.button("flush GC")) {
            System.gc();
        }
    }

}
