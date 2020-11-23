package coffee3D.core.ui.hud;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;

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
    private static final ImVec2 textSize = new ImVec2();

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
        ImageParams.ResetParamCount();
    }

    public static boolean BorderContainer(NodeAnchor anchor, PixelOffset offset, ImageParams image) {

        HudNodePosition pos = HudNodePosition.Get(anchor, offset);
        if (image._textureId >= 0) {
            ImGui.getWindowDrawList().addImageRounded(image._textureId, pos.posX, pos.posY, pos.posX + pos.sizeX, pos.posY + pos.sizeY, 0, 1, 1, 0, image._color, image._rounding);
        } else {
            ImGui.getWindowDrawList().addRectFilled(pos.posX, pos.posY, pos.posX + pos.sizeX, pos.posY + pos.sizeY, image._color);
        }


        return BeginContainer(anchor, offset);
    }

    public static void VerticalBox(NodeAnchor anchor, PixelOffset offset, IDrawContent[] content) {
        if (BeginContainer(anchor, offset)) {
            for (int i = 0; i < content.length; ++i) {
                if (BeginContainer(NodeAnchor.Get(0, (i / (float)content.length), 1, ((i + 1) / (float)content.length)), PixelOffset.DEFAULT)) {
                    content[i].draw();
                }
                EndContainer();
            }
        }
        EndContainer();
    }


    public static void HorizontalBox(NodeAnchor anchor, PixelOffset offset, IDrawContent[] content) {
        if (BeginContainer(anchor, offset)) {
            for (int i = 0; i < content.length; ++i) {
                if (BeginContainer(NodeAnchor.Get((i / (float)content.length), 0, ((i + 1) / (float)content.length), 1), PixelOffset.DEFAULT)) {
                    content[i].draw();
                }
                EndContainer();
            }
        }
        EndContainer();
    }

    public static void WidgetSwitcher(NodeAnchor anchor, PixelOffset offset, IDrawContent[] content, ImInt currentElement) {
        if (currentElement == null || currentElement.get() < 0 || currentElement.get() >= content.length) return;
        if (BeginContainer(anchor, offset)) {
            content[currentElement.get()].draw();
        }
        EndContainer();
    }

    public static boolean ImageButton(NodeAnchor anchor, PixelOffset offset, ButtonBehavior behavior, ImageParams image, TextParams text) {
        boolean bHasBeenPressed = false;
        HudNodePosition pos = HudNodePosition.Get(anchor, offset);

        if (HudUtils.BeginContainer(anchor, offset)) {

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

            if (image._textureId >= 0) {
                ImGui.getWindowDrawList().addImageRounded(image._textureId, pos.posX, pos.posY, pos.posX + pos.sizeX, pos.posY + pos.sizeY, 0, 1, 1, 0, image._color, image._rounding);
            }
            else {
                ImGui.getWindowDrawList().addRectFilled(pos.posX, pos.posY, pos.posX + pos.sizeX, pos.posY + pos.sizeY, image._color);
            }
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

    public static void ProgressBar(NodeAnchor anchor, PixelOffset offset, ImageParams backgroundImage, ImageParams foregroundImage, float progress, boolean bVertical) {
        HudNodePosition pos = HudNodePosition.Get(anchor, offset);
        if (HudUtils.BeginContainer(anchor, offset)) {

            if (backgroundImage._textureId >= 0) {
                ImGui.getWindowDrawList().addImageRounded(backgroundImage._textureId, pos.posX, pos.posY, pos.posX + pos.sizeX, pos.posY + pos.sizeY, 0, 1, 1, 0, backgroundImage._color, backgroundImage._rounding);
            } else {
                ImGui.getWindowDrawList().addRectFilled(pos.posX, pos.posY, pos.posX + pos.sizeX, pos.posY + pos.sizeY, backgroundImage._color);
            }

            float realPosY = bVertical ? pos.posY + pos.sizeY * (1 - progress) : pos.posY;
            float realSizeX = bVertical ? pos.sizeX : pos.sizeX * progress;

            if (foregroundImage._textureId >= 0) {
                ImGui.getWindowDrawList().addImageRounded(foregroundImage._textureId, pos.posX, realPosY, pos.posX + realSizeX, pos.posY + pos.sizeY, 0, bVertical ? progress : 1, bVertical ? 1 : progress, 0, foregroundImage._color, foregroundImage._rounding);
            } else {
                ImGui.getWindowDrawList().addRectFilled(pos.posX, pos.posY, pos.posX + pos.sizeX, pos.posY + pos.sizeY, foregroundImage._color);
            }
        }
        HudUtils.EndContainer();
    }
}
