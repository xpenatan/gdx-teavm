package com.github.xpenatan.teavm.generator.ui.view;

import com.badlogic.gdx.math.MathUtils;
import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImGuiInternal;
import imgui.ImGuiStyle;
import imgui.ImGuiWindow;
import imgui.ImRect;
import imgui.ImVec2;

public class SpinnerView {

    public static boolean drawSpinner(String id, float radius, int thickness, int color, float gTime) {
        ImGuiWindow imGuiWindow = ImGuiInternal.GetCurrentWindow();
        ImVec2 cursorPos = imGuiWindow.get_DC().get_CursorPos();
        float posX = cursorPos.get_x();
        float posY = cursorPos.get_y();
        return drawSpinner(id, radius, thickness, color, gTime, posX, posY, true);
    }

    public static boolean drawSpinner(String id, float radius, int thickness, int color, float gTime, float posX, float posY, boolean itemAdd) {
        ImGuiWindow imGuiWindow = ImGuiInternal.GetCurrentWindow();
        if(imGuiWindow.get_SkipItems())
            return false;

        ImDrawList imDrawList = imGuiWindow.get_DrawList();

        ImGuiStyle imGuiStyle = ImGui.GetStyle();

        float sizeX = (radius) * 2;
        float sizeY = (radius + imGuiStyle.get_FramePadding().get_y()) * 2;

        float pos2X = posX + sizeX;
        float pos2Y = posY + sizeY;

        if(itemAdd) {
            ImGuiInternal.ItemSize_2(ImRect.TMP_1.set(posX, posY, pos2X, pos2Y), imGuiStyle.get_FramePadding().get_y());
            if(!ImGuiInternal.ItemAdd(ImRect.TMP_1.set(posX, posY, pos2X, pos2Y), id.hashCode()))
                return false;
        }

        imDrawList.PathClear();

        int num_segments = 30;
        int start = (int)Math.abs(MathUtils.sin(gTime * 1.8f) * (num_segments - 5));

        float a_min = MathUtils.PI * 2.0f * ((float)start) / (float)num_segments;
        float a_max = MathUtils.PI * 2.0f * ((float)num_segments - 3) / (float)num_segments;

        float centerX = posX + radius;
        float centerY = posY + radius + imGuiStyle.get_FramePadding().get_y();

        for(int i = 0; i < num_segments; i++) {
            float a = a_min + ((float)i / (float)num_segments) * (a_max - a_min);
            float pathX = centerX + MathUtils.cos(a + gTime * 8) * radius;
            float pathY = centerY + MathUtils.sin(a + gTime * 8) * radius;
            imDrawList.PathLineTo(ImVec2.TMP_1.set(pathX, pathY));
        }
        imDrawList.PathStroke(color, 0, thickness);
        return true;
    }
}
