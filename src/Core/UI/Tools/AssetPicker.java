package Core.UI.Tools;

import Core.Assets.*;
import Core.UI.SubWindows.SubWindow;
import imgui.ImGui;
import imgui.type.ImInt;

import java.util.List;

public class AssetPicker extends SubWindow {

    private static AssetPicker _currentPicker;
    private AssetReference _assetPtr;

    private String[] _assetNames;
    private List<Asset> _availableAssets;
    private ImInt _selectedItem;

    public AssetPicker(String windowName, AssetReference assetPtr) {
        super(windowName);
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
            close();
        }
    }


}
