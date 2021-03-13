package coffee3D.core.ui.tools;

import coffee3D.core.assets.AssetManager;
import coffee3D.core.renderer.scene.RenderScene;
import coffee3D.core.renderer.Window;
import coffee3D.core.resources.ResourceManager;
import coffee3D.core.types.TypeHelper;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;

public class StatHelper {

    private static final float[] framerateHistory = new float[120];
    private static int fpsIndex = 0;
    private static float lastFpsUpdate = 0;
    private static float averageFps = 0;

    private static final float[] durationHistory = new float[120];
    private static int durationIndex = 0;
    private static float lastDurationUpdate = 0;
    private static float averageDuration = 0;

    public static void DrawStats(RenderScene context) {
        if (ImGui.beginChild("stats", 700, 800)) {
            DrawFpsBox();
            DrawFrameDurationBox();


            if (ImGui.collapsingHeader("scene informations")) {
                ImGui.text("components : " + context.getComponents().size());
                ImGui.text("resources : " + ResourceManager.GetResources().size());
                ImGui.text("assets : " + AssetManager.GetAssets().size());
            }

            TypeHelper.DrawStats();
            PrintGCStats();
        }
        ImGui.endChild();
    }

    private static void DrawFpsBox() {
        if (ImGui.collapsingHeader("displayed framerate")) {

            float fps = (float) (1f / Window.GetPrimaryWindow().getDeltaTime());

            if (lastFpsUpdate > 1 / 60f || Window.GetPrimaryWindow().getDeltaTime() > (1 / 30f)) {
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

            ImGui.plotLines("framerate : min = " + min, framerateHistory, framerateHistory.length, fpsIndex, "average : " + (int) (averageFps) + " fps", 0, averageFps * 1.4f, 300, 100);

            ImGui.text(String.format("max fps : %.5f", max));
        }
    }

    private static void DrawFrameDurationBox() {
        if (ImGui.collapsingHeader("frame duration (without fps cap or buffers swap)")) {

            float ms = (float) Window.GetPrimaryWindow().getFrameDuration();

            if (lastDurationUpdate > 1 / 60f || Window.GetPrimaryWindow().getDeltaTime() > (1 / 30f)) {
                lastDurationUpdate = 0;
                durationIndex = ((durationIndex + 1) % durationHistory.length);
                durationHistory[durationIndex] = ms;
            }
            lastDurationUpdate += (float) Window.GetPrimaryWindow().getDeltaTime();

            averageDuration = 0;
            float min = durationHistory[0];
            float max = durationHistory[0];
            for (float val : durationHistory) {
                averageDuration += val;
                if (val < min) min = val;
                if (val > max) max = val;
            }
            averageDuration /= (float) durationHistory.length;

            ImGui.plotLines("frame duration : max = " + max * 1000, durationHistory, durationHistory.length, durationIndex, "average : " + averageDuration * 1000 + " ms", 0, averageDuration * 1.8f, 300, 100);

            ImGui.text(String.format("theoretical fps : %.5f", 1 / averageDuration));
        }
    }

    private static long garbageCollectionValue = 0;
    private static long garbageCollectionTimeValue = 0;
    private static long lastGarbageCollection = 0;
    private static long lastGarbageCollectionTime = 0;

    public static void PrintGCStats() {

        if (ImGui.collapsingHeader("Garbage collector and memory management")) {
            long garbageCollection = 0;
            long garbageCollectionTime = 0;


            for (GarbageCollectorMXBean gc :
                    ManagementFactory.getGarbageCollectorMXBeans()) {

                long count = gc.getCollectionCount();

                if (count >= 0) {
                    garbageCollection += count;
                }

                long time = gc.getCollectionTime();

                if (time >= 0) {
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
            ImGui.sameLine();
            if (ImGui.button("clear memory")) {
                TypeHelper.ClearMemory();
            }
        }
    }

}
