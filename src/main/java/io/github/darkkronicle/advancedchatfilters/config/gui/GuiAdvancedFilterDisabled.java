/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatfilters.config.gui;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchatcore.util.Colors;
import io.github.darkkronicle.advancedchatcore.util.StyleFormatter;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public class GuiAdvancedFilterDisabled extends GuiBase {

    private final List<OrderedText> warning;

    public GuiAdvancedFilterDisabled(Screen parent) {
        this.title = StringUtils.translate("advancedchatfilters.screen.warning");
        setParent(parent);
        MutableText text = Text.literal(StringUtils.translate("advancedchatfilters.warning.advancedfilters"));
        warning = new ArrayList<>();
        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        for (Text t :
                StyleFormatter.wrapText(
                        client.textRenderer, width - 100, StyleFormatter.formatText(text))) {
            warning.add(t.asOrderedText());
        }
    }

    @Override
    public void init() {
        super.init();
        int x = 10;
        int y = 26;

        String backText = ButtonListener.Type.BACK.getDisplayName();
        int backWidth = StringUtils.getStringWidth(backText) + 10;
        ButtonGeneric back = new ButtonGeneric(x + backWidth, y, backWidth, true, backText);
        this.addButton(
                back, new GuiAdvancedFilterDisabled.ButtonListener(ButtonListener.Type.BACK, this));
        x += back.getWidth() + 2;
    }

    public void back() {
        closeGui(true);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        super.render(context, mouseX, mouseY, partialTicks);
        int width = client.getWindow().getScaledWidth();
        int y = 100;
        for (OrderedText warn : warning) {
            context.drawCenteredTextWithShadow(
                    client.textRenderer,
                    warn,
                    width / 2,
                    y,
                    Colors.getInstance().getColorOrWhite("white").color());
            y += client.textRenderer.fontHeight + 2;
        }
    }

    public static class ButtonListener implements IButtonActionListener {

        private final GuiAdvancedFilterDisabled parent;
        private final ButtonListener.Type type;

        public ButtonListener(ButtonListener.Type type, GuiAdvancedFilterDisabled parent) {
            this.type = type;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            if (this.type == ButtonListener.Type.BACK) {
                parent.back();
            }
        }

        public enum Type {
            BACK("back");

            private final String translation;

            private static String translate(String key) {
                return "advancedchatfilters.gui.button." + key;
            }

            Type(String key) {
                this.translation = translate(key);
            }

            public String getDisplayName() {
                return StringUtils.translate(translation);
            }
        }
    }
}
