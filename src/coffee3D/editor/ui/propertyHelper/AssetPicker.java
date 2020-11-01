package coffee3D.editor.ui.propertyHelper;

import coffee3D.core.assets.Asset;
import coffee3D.core.assets.AssetReference;
import coffee3D.editor.ui.propertyHelper.writers.OnAssetEdited;
import coffee3D.core.ui.subWindows.SubWindow;
import coffee3D.core.assets.AssetManager;
import imgui.ImGui;
import imgui.type.ImInt;

import java.util.List;

public class AssetPicker extends SubWindow {

    private static AssetPicker _currentPicker;
    private AssetReference _assetPtr;

    private String[] _assetNames;
    private List<Asset> _availableAssets;
    private ImInt _selectedItem;
    private final OnAssetEdited _execEvent;


    public AssetPicker(String windowName, AssetReference assetPtr, OnAssetEdited execEvent) {
        super(windowName);
        _execEvent = execEvent;
        _assetPtr = assetPtr;
        if (_currentPicker != null) {
            _currentPicker.close();
        }
        _currentPicker = this;

        _selectedItem = new ImInt(0);
        _availableAssets = AssetManager.GetAssetByClass(_assetPtr.getGenericClass());
        _assetNames = new String[_availableAssets.size() + 1];
        _assetNames[0] = "none";
        for (int i = 1; i <= _availableAssets.size(); ++i) {
            _assetNames[i] = _availableAssets.get(i - 1).getName();
            if (_availableAssets.get(i - 1) == assetPtr.get()) {
                _selectedItem = new ImInt(i);
            }
        }
    }

    @Override
    public void close() {
        super.close();
        if (_currentPicker == this) _currentPicker = null;
    }

    @Override
    protected void draw() {
        int lastIndex = _selectedItem.get();
        ImGui.listBox("##Assets", _selectedItem, _assetNames, _assetNames.length);
        if (lastIndex != _selectedItem.get()) {
            if (_selectedItem.get() == 0) {
                _assetPtr.set(null);
            }
            else {
                _assetPtr.set(_availableAssets.get(_selectedItem.get() - 1));
            }
            if (_execEvent != null) _execEvent.execute();
            close();
        }
    }


}
