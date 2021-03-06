package coffee3D.editor.ui.browsers;

import coffee3D.core.assets.Asset;
import coffee3D.core.assets.AssetManager;
import coffee3D.core.assets.types.Font;
import coffee3D.core.io.settings.EngineSettings;
import coffee3D.core.ui.subWindows.SubWindow;
import coffee3D.editor.EditorModule;
import coffee3D.editor.ui.assets.EditorAssetUtils;
import coffee3D.editor.ui.importers.*;
import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImBoolean;
import imgui.type.ImString;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SceneContentBrowser extends SubWindow {
    private boolean _bSetColumnWidth = false;
    private ImBoolean _bShowAllContent = new ImBoolean(false);
    private File _selectedFile;
    private final ImString searchString = new ImString("", 256);

    private final ArrayList<Class> _filters = new ArrayList<>();
    private final ImBoolean _filterBool = new ImBoolean();


    public SceneContentBrowser(String windowName) {
        super(windowName);

        _selectedFile = EngineSettings.Get().gameAssetsPath;
    }


    @Override
    protected void draw() {

        Font contentFont = AssetManager.FindAsset("Roboto-Regular");

        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 10, 7);
        if (ImGui.button("Create")) {
            ImGui.openPopup("CreatePopup");

        }

        if (ImGui.beginPopup("CreatePopup")) {
            if (ImGui.menuItem("Static mesh")) new MeshImporter("Mesh importer");
            if (ImGui.menuItem("Material")) new MaterialImporter("Material importer");
            if (ImGui.menuItem("Material instance")) new MaterialInstanceImporter("create material instance");
            if (ImGui.menuItem("Texture2D")) new TextureImporter("Texture importer");
            if (ImGui.menuItem("Font")) new FontImporter("Font importer");
            if (ImGui.menuItem("Skeleton")) new AnimationImporter("SkeletalMesh");
            //if (ImGui.menuItem("Audio")) new AudioImporter("Audio importer");
            ImGui.endPopup();
        }

        ImGui.sameLine();

        if (ImGui.button("Save All")) EditorModule.SaveAll();

        ImGui.popStyleVar();

        ImGui.separator();

        drawHierarchy();
        ImGui.nextColumn();

        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, 10, 3);
        ImGui.inputText("##searchBox", searchString);

        drawFilters();
        ImGui.dummy(0, 5);

        ImGui.popStyleVar();
        if (ImGui.beginChild("contentAssets")) {


            if (contentFont != null) contentFont.use();

            float sizeX = ImGui.getContentRegionAvailX();

            int widthItems = (int) (sizeX / 70);
            ImGui.columns(Math.max(widthItems, 1), "", false);

            for (Class key : AssetManager.GetAssetMap().keySet()) {

                for (Asset elem : AssetManager.GetAssetMap().get(key)) {

                    if (!_filters.isEmpty() && !_filters.contains(elem.getClass()) || (!searchString.get().equals("") && !elem.getName().contains(searchString.get()))) continue;
                    if (elem.getSavePath() != null && (_bShowAllContent.get() || isInDirectoryRecursive(elem.getSavePath(), _selectedFile))) {
                        EditorAssetUtils.DrawAssetThumbnail(elem);
                        ImGui.nextColumn();
                    }
                }
            }
            ImGui.columns(1);

            if (contentFont != null) contentFont.pop();
        }
        ImGui.endChild();
        ImGui.columns(1);
    }

    private boolean isInDirectoryRecursive(File f, File directory) {
        File parent = f.getParentFile();
        if (parent == null) return false;
        if (parent.equals(directory)) return true;
        return isInDirectoryRecursive(parent, directory);

    }

    private void drawFilters() {
        HashMap<Class, List<Asset>> assetMap = AssetManager.GetAssetMap();
        boolean bFirstElem = true;
        for (Class key : assetMap.keySet()) {
            boolean containsKey = _filters.contains(key);
            _filterBool.set(containsKey);
            if (bFirstElem) bFirstElem = false;
            else ImGui.sameLine();
            ImGui.checkbox(key.getSimpleName(), _filterBool);
            if (containsKey != _filterBool.get()) {
                if (containsKey) {
                    _filters.remove(key);
                }
                else {
                    _filters.add(key);
                }
            }
        }
    }

    private void drawHierarchy() {
        // Initialize left side size
        float windowSize = Math.max(ImGui.getContentRegionAvailX() / 7.f, 150.f);
        ImGui.columns(2);
        if (!_bSetColumnWidth) {
            ImGui.setColumnWidth(0, windowSize);
            _bSetColumnWidth = true;
        }
        if (ImGui.beginChild("folders")) {
            drawHierarchy(EngineSettings.Get().gameAssetsPath);
            drawHierarchy(EngineSettings.Get().engineAssetsPath);
        }
        ImGui.endChild();
    }

    private void drawHierarchy(File f) {
        if (!f.exists()) return;
        int flags = ImGuiTreeNodeFlags.OpenOnDoubleClick;
        boolean bHasFolderChild = false;
        File[] childs = f.listFiles();
        if (childs != null) {
            for (File fi : childs) {
                if (fi.isDirectory()) {
                    bHasFolderChild = true;
                    break;
                }
            }
        }
        if (!bHasFolderChild) flags |= ImGuiTreeNodeFlags.Leaf;
        if (_selectedFile.equals(f)) flags |= ImGuiTreeNodeFlags.Selected;
        boolean bExpand = ImGui.treeNodeEx(f.getName(), flags);
        if (ImGui.isItemClicked()) {
            _selectedFile = f;
            _bShowAllContent.set(false);
        }
        if (bExpand) {
            if (childs != null) {
                for (File child : childs) {
                    if (child.isDirectory()) drawHierarchy(child);
                }
            }
            ImGui.treePop();
        }
    }
}