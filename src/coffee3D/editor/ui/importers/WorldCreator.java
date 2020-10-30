package coffee3D.editor.ui.importers;

import coffee3D.core.assets.AssetManager;
import coffee3D.core.assets.types.MaterialInstance;
import coffee3D.core.assets.types.World;
import coffee3D.core.io.log.Log;
import coffee3D.core.io.settings.EngineSettings;
import coffee3D.core.renderer.scene.Scene;
import coffee3D.core.ui.subWindows.SubWindow;
import coffee3D.editor.ui.browsers.FolderPicker;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.type.ImString;

import java.io.File;

public class WorldCreator extends SubWindow {
    private Scene _editedScene;

    private ImString assetName;
    private File assetSavePath;
    private File sourceSavePath;


    public WorldCreator(Scene inScene, String windowName) {
        super(windowName);
        _editedScene = inScene;
        assetSavePath = EngineSettings.GAME_ASSET_PATH;
        sourceSavePath = EngineSettings.GAME_ASSET_PATH;
        assetName = new ImString(EngineSettings.DEFAULT_MAP_NAME);
    }

    @Override
    protected void draw() {

        ImGui.text("asset save path");
        ImGui.sameLine();
        if (ImGui.button(assetSavePath.isDirectory() ? assetSavePath.getPath() + "##1" : "null##1")) {
            new FolderPicker("set save path", assetSavePath, file -> {
                assetSavePath = file;
            });
        }
        ImGui.text("asset source path");
        ImGui.sameLine();
        if (ImGui.button(sourceSavePath.isDirectory() ? sourceSavePath.getPath() + "##2" : "null##2")) {
            new FolderPicker("set source path", sourceSavePath, file -> {
                sourceSavePath = file;
            });
        }


        boolean bIsNameValid = AssetManager.IsAssetNameFree(assetName.get());
        if (!bIsNameValid) {
            ImGui.pushStyleColor(ImGuiCol.FrameBg, 1.f, .2f, .2f, .5f);
            ImGui.pushStyleColor(ImGuiCol.Text, 1.f, .4f, .4f, .8f);
        }

        ImGui.text("asset name : ");
        ImGui.sameLine();
        ImGui.inputText("##file name", assetName);

        if (!bIsNameValid) {
            ImGui.sameLine();
            ImGui.text("invalid file name");
            ImGui.popStyleColor();
            ImGui.popStyleColor();
        }


        if (bIsNameValid && assetSavePath.isDirectory() && sourceSavePath.isDirectory()) {
            ImGui.dummy(0, ImGui.getContentRegionAvailY() - 50);
            ImGui.dummy(ImGui.getContentRegionAvailX() - 250, 0);
            ImGui.sameLine();

            File newMapFile = new File(sourceSavePath + "/" + assetName.get() + ".map");
            File newAssetFile = new File(assetSavePath + "/" + assetName.get() + ".asset");
            if (!newMapFile.exists() && !newAssetFile.exists()) {
                if (ImGui.button("Create", ImGui.getContentRegionAvailX(), 0)) {
                    World world = new World(assetName.get(), newMapFile, newAssetFile, _editedScene);
                    world.save();
                    close();
                }
            }
        }

    }
}
