package Core.UI.HUD;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

final class HudNodePosition {
    private HudNodePosition() {}
    private static HudNodePosition _instance;

    public float posX, posY, sizeX, sizeY;

    public static HudNodePosition Get(NodeAnchor anchor, PixelOffset offset) {
        if (_instance == null) _instance = new HudNodePosition();
        float availableSizeX = ImGui.getWindowSizeX();
        float availableSizeY = ImGui.getWindowSizeY();
        _instance.posX = availableSizeX * anchor.minX + ImGui.getWindowPosX() + offset.left;
        _instance.posY = availableSizeY * anchor.minY + ImGui.getWindowPosY() + offset.top;
        _instance.sizeX = availableSizeX * anchor.maxX - availableSizeX * anchor.minX + offset.right - offset.left;
        _instance.sizeY = availableSizeY * anchor.maxY - availableSizeY * anchor.minY + offset.bottom - offset.top;
        return _instance;
    }
}


public final class HudUtils {
    private HudUtils() {}

    private static int _containerCount;
    private static ImVec2 textSize;

    public static void ResetCounters() { _containerCount = 0; }
    public static final ImBoolean bDrawDebugBoxes = new ImBoolean(false);


    public static boolean BeginContainer(NodeAnchor anchor, PixelOffset offset) {
        HudNodePosition pos = HudNodePosition.Get(anchor, offset);
        ImGui.setNextWindowPos(pos.posX, pos.posY);
        ImGui.setNextWindowSize(pos.sizeX, pos.sizeY);
        return ImGui.beginChild(
                "DynamicContainer" + _containerCount++,
                pos.sizeX,
                pos.sizeY,
                bDrawDebugBoxes.get(),
                ImGuiWindowFlags.NoDecoration
        );
    }

    public static void EndContainer() {
        ImGui.endChild();
    }


    private static boolean RoundedButton(NodeAnchor anchor, PixelOffset offset) {
        return false;
    }

    public static boolean ImageButton(NodeAnchor anchor, PixelOffset offset, ButtonBehavior behavior, ImageParams image, TextParams text) {

        boolean bHasBeenPressed = false;

        HudNodePosition pos = HudNodePosition.Get(anchor, offset);

        if (HudUtils.BeginContainer(anchor, offset)) {

            if (textSize == null) textSize = new ImVec2();

            bHasBeenPressed = ImGui.invisibleButton(text._text + _containerCount++, ImGui.getContentRegionAvailX(), ImGui.getContentRegionAvailY());

            if (!ImGui.isItemHovered()) {
                pos.sizeX -= behavior._sensitivity * 2;
                pos.sizeY -= behavior._sensitivity * 2;
                pos.posX += behavior._sensitivity;
                pos.posY += behavior._sensitivity;
            }
            if (ImGui.isItemActive()) {
                pos.sizeX -= behavior._sensitivity * 2;
                pos.sizeY -= behavior._sensitivity * 2;
                pos.posX += behavior._sensitivity * 1.5f;
                pos.posY += behavior._sensitivity * 1.5f;
            }

            ImGui.getWindowDrawList().addImageRounded(image._textureId, pos.posX, pos.posY, pos.posX + pos.sizeX, pos.posY + pos.sizeY, 0, 1, 1, 0, image._color, image._rounding);
            text._font.setScale(text._size);
            ImGui.pushFont(text._font);
            ImGui.calcTextSize(textSize, text._text);
            ImGui.getWindowDrawList().addText((2 * pos.posX + pos.sizeX) / 2.f - textSize.x / 2, (2 * pos.posY + pos.sizeY) / 2.f - textSize.y / 2, text._color, text._text);
            text._font.setScale(1);
            ImGui.popFont();

        }
        HudUtils.EndContainer();

        return bHasBeenPressed;
    }

}
