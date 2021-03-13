package coffee3D.editor.ui.browsers;

import coffee3D.core.io.log.Log;
import coffee3D.core.ui.subWindows.SubWindow;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;

import java.io.File;
import java.util.ArrayList;

public class FolderPicker extends SubWindow {

    private File _currentDirectory;
    private boolean _bSetColumnWidth = false;
    private IFileValidated _validateEvent;
    private boolean _bValidated = false;

    public FolderPicker(String windowName, File currentFolder, IFileValidated validateEvent) {
        super(windowName);
        _currentDirectory = new File("./");
        _validateEvent = validateEvent;
        if (currentFolder != null) {
            if (currentFolder.isDirectory()) {
                _currentDirectory = currentFolder;
            }
            else {
                _currentDirectory = currentFolder.getParentFile();
                _currentDirectory = currentFolder;
            }
        }
    }

    @Override
    protected void draw() {
        // Display path
        ImGui.separator();
        drawTree();
        if (_currentDirectory.getParentFile() != null) {
            ImGui.sameLine();
            if (ImGui.arrowButton("moveBack", 0)) {
                _currentDirectory = _currentDirectory.getParentFile();
            }
        }
        ImGui.separator();

        // Initialize left side size
        float windowSize = Math.max(ImGui.getContentRegionAvailX() / 15.f, 150.f);
        ImGui.columns(2);
        if (!_bSetColumnWidth) {
            ImGui.setColumnWidth(0, windowSize);
            _bSetColumnWidth = true;
        }

        // Print default paths
        if (ImGui.button(".", ImGui.getContentRegionAvailX(), 0.f)) {
            _currentDirectory = new File(".");
        }
        if (ImGui.button("/", ImGui.getContentRegionAvailX(), 0.f)) {
            _currentDirectory = new File("/");
        }
        File[] drives = File.listRoots();
        if (drives != null && drives.length > 0) {
            for (File aDrive : drives) {
                if (ImGui.button(aDrive.toString(), ImGui.getContentRegionAvailX(), 0.f)) {
                    _currentDirectory = aDrive;
                }
            }
        }

        ImGui.nextColumn();

        // Print content
        if (ImGui.beginChild("outer_child", ImGui.getContentRegionAvailX(), ImGui.getContentRegionAvailY() - 100, false)) {
            ImGui.pushStyleVar(ImGuiStyleVar.ButtonTextAlign, 0, 0.5f);
            drawDirContent();
            ImGui.popStyleVar();
        }
        ImGui.endChild();

        // Bottom bar
        ImGui.nextColumn();
        ImGui.columns(1);
        ImGui.separator();
        if (ImGui.beginChild("selected_element", ImGui.getContentRegionAvailX() - 450, 32, true)) {
            //ImGui.text(_selectedElement == null ? "none" : _selectedElement.getName());
        }
        ImGui.endChild();
        ImGui.sameLine(ImGui.getContentRegionAvailX() - 400);


        // Validate button
        ImGui.dummy(0, 10);
        ImGui.dummy(ImGui.getContentRegionAvailX() - 320, 0);
        ImGui.sameLine();
        if (ImGui.button("validate", 320, 35)) {
            _validateEvent.applyFile(_currentDirectory);
            _bValidated = true;
            close();
        }
    }

    private void drawDirContent() {
        if (_currentDirectory.canRead()) {
            try {
                for (File file : _currentDirectory.listFiles()) {
                    if (file.isDirectory()) {
                        if (ImGui.button(file.getName(), ImGui.getContentRegionAvailX(), 0)) {
                            _currentDirectory = file;
                        }
                    }
                }
            } catch (Exception e) {
                Log.Warning(e.getMessage());
            }
            try {

                ImGui.pushStyleColor(ImGuiCol.Button, .7f, .7f, .8f, .5f);
                ImGui.pushStyleColor(ImGuiCol.ButtonHovered, .8f, .8f, .9f, .7f);
                ImGui.pushStyleColor(ImGuiCol.ButtonActive, .6f, .6f, .8f, .5f);

            } catch (Exception e) {
                Log.Warning(e.getMessage());
            } finally {

                ImGui.popStyleColor();
                ImGui.popStyleColor();
                ImGui.popStyleColor();
            }
        }
    }

    protected void drawTree() {
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
}
