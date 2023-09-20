package com.github.xpenatan.teavm.generator.ui.view;

import com.badlogic.gdx.math.MathUtils;
import imgui.ImGui;

public class SpinnerView {

//    public static boolean drawSpinner(String id, float radius, int thickness, int color, float gTime) {
//        float posX = ImGui.GetWindowDCCursorPosX();
//        float posY = ImGui.GetWindowDCCursorPosY();
//        return drawSpinner(id, radius, thickness, color, gTime, posX, posY, true);
//    }

    public static boolean drawSpinner(String id, float radius, int thickness, int color, float gTime, float posX, float posY, boolean itemAdd) {
//        if(ImGui.GetWindowSkipItem())
            return false;

//        ImDrawList imDrawList = ImGui.GetWindowDrawList();
//
//        ImGuiStyle imGuiStyle = ImGui.GetStyle();
//
//        float sizeX = (radius) * 2;
//        float sizeY = (radius + imGuiStyle.getFramePadding().getY()) * 2;
//
//        float pos2X = posX + sizeX;
//        float pos2Y = posY + sizeY;
//
//        if(itemAdd) {
//            ImGui.ItemSize(posX, posY, pos2X, pos2Y, imGuiStyle.getFramePadding().getY());
//            if(!ImGui.ItemAdd(posX, posY, pos2X, pos2Y, id))
//                return false;
//        }
//
//        imDrawList.PathClear();
//
//        int num_segments = 30;
//        int start = (int)Math.abs(MathUtils.sin(gTime * 1.8f) * (num_segments - 5));
//
//        float a_min = MathUtils.PI * 2.0f * ((float)start) / (float)num_segments;
//        float a_max = MathUtils.PI * 2.0f * ((float)num_segments - 3) / (float)num_segments;
//
//        float centerX = posX + radius;
//        float centerY = posY + radius + imGuiStyle.getFramePadding().getY();
//
//        for(int i = 0; i < num_segments; i++) {
//            float a = a_min + ((float)i / (float)num_segments) * (a_max - a_min);
//            float pathX = centerX + MathUtils.cos(a + gTime * 8) * radius;
//            float pathY = centerY + MathUtils.sin(a + gTime * 8) * radius;
//            imDrawList.PathLineTo(pathX, pathY);
//        }
//        imDrawList.PathStroke(color, 0, thickness);
//        return true;
    }
}
