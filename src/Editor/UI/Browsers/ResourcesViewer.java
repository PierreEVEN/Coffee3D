package Editor.UI.Browsers;

import Core.Resources.GraphicResource;
import Core.Resources.ResourceManager;
import Core.UI.SubWindows.SubWindow;
import imgui.ImGui;

import java.util.ArrayList;
import java.util.Collection;

public class ResourcesViewer extends SubWindow {
    public ResourcesViewer(String windowName) {
        super(windowName);
    }

    @Override
    protected void draw() {
        ArrayList<Class> orderedResources = new ArrayList<>();

        Collection<GraphicResource> resources = ResourceManager.GetResources();

        for (GraphicResource resource : resources) {
            if (!orderedResources.contains(resource.getClass())) orderedResources.add(resource.getClass());
        }

        for (Class cl : orderedResources) {
            ImGui.separator();
            ImGui.text(cl.getSimpleName());
            ImGui.indent();
            for (GraphicResource resource : resources) {
                if (resource.getClass() == cl) ImGui.text(resource.toString());
            }
            ImGui.unindent();
        }
    }
}
