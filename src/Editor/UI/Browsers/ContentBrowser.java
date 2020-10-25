package Editor.UI.Browsers;

import Core.Assets.Asset;
import Core.Assets.AssetManager;
import Core.IO.Settings.EngineSettings;
import Core.UI.SubWindows.SubWindow;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImBoolean;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class ContentBrowser extends SubWindow {
    private boolean _bSetColumnWidth = false;
    private ImBoolean _bShowAllContent = new ImBoolean(false);
    private File _selectedFile;

    public ContentBrowser(String windowName) {
        super(windowName);

        _selectedFile = EngineSettings.DEFAULT_ASSET_PATH;

    }

    protected void drawHierarchy(File f) {
        for (File file : f.listFiles()) {
            if (file.isDirectory()) {

                int flags = ImGuiTreeNodeFlags.OpenOnDoubleClick;
                boolean bHasFolderChild = false;
                for (File fi : file.listFiles()) {
                    if (fi.isDirectory()) {
                        bHasFolderChild = true;
                        break;
                    }
                }
                if (!bHasFolderChild) flags |= ImGuiTreeNodeFlags.Leaf;
                if (_selectedFile.equals(file)) flags |= ImGuiTreeNodeFlags.Selected;
                boolean bExpand = ImGui.treeNodeEx(file.getName(), flags);
                if (ImGui.isItemClicked()) {
                    _selectedFile = file;
                    _bShowAllContent.set(false);
                }
                if (bExpand) {
                    drawHierarchy(file);
                    ImGui.treePop();
                }
            }
        }
    }

    @Override
    protected void draw() {
        ImGui.dummy(ImGui.getContentRegionAvailX() - 150, 0);
        ImGui.sameLine();
        ImGui.checkbox("show all", _bShowAllContent);
        ImGui.separator();


        Collection<Asset> assets = AssetManager.GetAssets();


        // Initialize left side size
        float windowSize = Math.max(ImGui.getContentRegionAvailX() / 7.f, 150.f);
        ImGui.columns(2);
        if (!_bSetColumnWidth) {
            ImGui.setColumnWidth(0, windowSize);
            _bSetColumnWidth = true;
        }

        drawHierarchy(EngineSettings.DEFAULT_ASSET_PATH);

        ImGui.nextColumn();


        if (ImGui.beginChild("contentAssets")) {

            float sizeX = ImGui.getContentRegionAvailX();

            int widthItems = (int) (sizeX / 80);
            ImGui.columns(Math.max(widthItems, 1), "", false);

            int currentElemIndex = 0;
            for (Asset elem : assets) {
                if (elem.getSavePath() != null && (_bShowAllContent.get() || elem.getSavePath().getParentFile().equals(_selectedFile))) {
                    elem.drawThumbnail();
                    ImGui.nextColumn();
                }
            }
            ImGui.columns(1);
        }
        ImGui.endChild();
        ImGui.columns(1);
    }
}