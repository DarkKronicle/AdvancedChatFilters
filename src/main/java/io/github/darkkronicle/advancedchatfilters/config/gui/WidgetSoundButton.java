/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatfilters.config.gui;

import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

public class WidgetSoundButton extends GuiTextFieldGeneric {

    public WidgetSoundButton(int x, int y, int width, int height, TextRenderer textRenderer) {
        super(x, y, width, height, textRenderer);
        MinecraftClient.getInstance().getSoundManager();
    }
}
