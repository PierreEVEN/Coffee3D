package Editor.UI.Browsers;

import Core.Assets.Asset;
import Core.Assets.AssetManager;
import Core.Resources.GraphicResource;
import Core.UI.SubWindows.SubWindow;
import imgui.ImGui;

import java.util.ArrayList;
import java.util.Collection;

public class ContentBrowser extends SubWindow {
    public ContentBrowser(String windowName) {
        super(windowName);
    }

    @Override
    protected void draw() {
        ArrayList<Class> orderedAssets = new ArrayList<>();

        Collection<Asset> assets = AssetManager.GetAssets();

        for (Asset asset : assets) {
            if (!orderedAssets.contains(asset.getClass())) orderedAssets.add(asset.getClass());
        }

        for (Class cl : orderedAssets) {
            ImGui.separator();
            ImGui.text(cl.getSimpleName());
            ArrayList<Asset> subAssets = new ArrayList<>();
            for (Asset asset : assets) {
                if (asset.getClass() == cl) subAssets.add(asset);
            }

            float sizeX = ImGui.getContentRegionAvailX();

            int widthItems = (int)(sizeX / 80);
            ImGui.columns(widthItems < 1 ? 1 : widthItems, "", false);

            int currentElemIndex = 0;
            for (Asset elem : subAssets) {
                elem.drawThumbnail();
                ImGui.nextColumn();
            }
            ImGui.columns(1);
        }
    }
}