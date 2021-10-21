package io.github.darkkronicle.advancedchatfilters.config.gui;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchatcore.util.ColorUtil;
import io.github.darkkronicle.advancedchatcore.util.FluidText;
import io.github.darkkronicle.advancedchatcore.util.RawText;
import io.github.darkkronicle.advancedchatcore.util.StyleFormatter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class GuiAdvancedFilterDisabled extends GuiBase {

    private final List<OrderedText> warning;

    public GuiAdvancedFilterDisabled(Screen parent) {
        setParent(parent);
        FluidText text = new FluidText(RawText.withStyle(StringUtils.translate("advancedchatfilters.warning.advancedfilters"), Style.EMPTY));
        warning = new ArrayList<>();
        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getScaledWidth();
        for (Text t : StyleFormatter.wrapText(client.textRenderer, width - 100, StyleFormatter.formatText(text))) {
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
        this.addButton(back, new GuiAdvancedFilterDisabled.ButtonListener(ButtonListener.Type.BACK, this));
        x += back.getWidth() + 2;
    }

    public void back() {
        closeGui(true);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        int width = client.getWindow().getScaledWidth();
        int y = 100;
        for (OrderedText warn : warning) {
            drawCenteredTextWithShadow(matrixStack, client.textRenderer, warn, width / 2, y, ColorUtil.WHITE.color());
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
            BACK("back"),
            ;
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
