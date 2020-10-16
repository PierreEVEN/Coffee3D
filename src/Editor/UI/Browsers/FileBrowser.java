package Editor.UI.Browsers;

import Core.UI.SubWindows.SubWindow;
import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

public class FileBrowser extends SubWindow {

    private File _currentDirectory;
    private String[] _desiredExtensions;

    public FileBrowser(String windowName, String[] desiredExtension) {
        super(windowName);
        _desiredExtensions = desiredExtension;
        _currentDirectory = new File("./");
    }

    private void drawTree() {
        File editedFile = _currentDirectory;
        ArrayList<File> elems = new ArrayList<>();
        while (editedFile != null) {
            elems.add(editedFile);
            editedFile = editedFile.getParentFile();
        }
        for (int i = elems.size() - 1; i >= 0; --i) {
            if (i < elems.size() - 1) ImGui.sameLine();
            if (ImGui.button(elems.get(i).getName())) {
                _currentDirectory = elems.get(i);
            }
            if (i != 0) {
                ImGui.sameLine();
                ImGui.text("/");
            }
        }
    }

    private boolean isValidExtension(File file) {
        Optional<String> extension = Optional.ofNullable(file.getName())
                .filter(f -> f.contains("."))
                .map(f -> f.substring(file.getName().lastIndexOf(".") + 1));

        if (extension == null) return false;
        if (_desiredExtensions == null) return true;

        for (String ext : _desiredExtensions) {
            if (extension.equals(ext)) return true;
        }
        return false;
    }

    @Override
    protected void draw() {

        drawTree();
        ImGui.separator();



        for (File file : _currentDirectory.listFiles()) {
            if (file.isDirectory()) {
                if (ImGui.button(file.getName(), ImGui.getContentRegionAvailX(), 0)) {
                    _currentDirectory = file;
                }
            }
        }

        ImGui.pushStyleColor(ImGuiCol.Button, .7f, .7f, .8f, .5f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, .8f, .8f, .9f, .7f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, .6f, .6f, .8f, .5f);

        for (File file : _currentDirectory.listFiles()) {
            if (!file.isDirectory() && (_desiredExtensions == null || isValidExtension(file))) {
                if (ImGui.button(file.getName(), ImGui.getContentRegionAvailX(), 0)){

                }
            }
        }

        ImGui.popStyleColor();
        ImGui.popStyleColor();
        ImGui.popStyleColor();
    }
}
