/*
 * Copyright (C) 2021-2022 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatfilters.config.gui;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigString;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.ConfigButtonOptionList;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetDropDownList;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.Konstruct.NodeException;
import io.github.darkkronicle.advancedchatcore.ModuleHandler;
import io.github.darkkronicle.advancedchatcore.config.gui.widgets.WidgetColor;
import io.github.darkkronicle.advancedchatcore.config.gui.widgets.WidgetLabelHoverable;
import io.github.darkkronicle.advancedchatcore.config.gui.widgets.WidgetToggle;
import io.github.darkkronicle.advancedchatcore.util.*;
import io.github.darkkronicle.advancedchatfilters.FiltersHandler;
import io.github.darkkronicle.advancedchatfilters.config.Filter;
import io.github.darkkronicle.advancedchatfilters.filters.ParentFilter;
import io.github.darkkronicle.advancedchatfilters.filters.ReplaceFilter;
import io.github.darkkronicle.advancedchatfilters.registry.MatchReplaceRegistry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class GuiFilterEditor extends GuiBase {

    public final Filter filter;

    private GuiTextFieldGeneric name;
    private GuiTextFieldGeneric findString;
    private GuiTextFieldGeneric replaceString;
    private WidgetDropDownList<MatchReplaceRegistry.MatchReplaceOption> replaceTypeWidget;
    private WidgetToggle setTextColor;
    private WidgetColor textColor;
    private WidgetToggle setBackgroundColor;
    private WidgetColor backgroundColor;
    private WidgetToggle stripColors;

    private GuiTextFieldGeneric test;
    private List<Text> outputMessage;

    public FilterTab tab = FilterTab.CONFIG;

    public GuiFilterEditor(Filter filter, Screen parent) {
        this.filter = filter;
        this.title = filter.getName().config.getStringValue();
        this.setParent(parent);
    }

    @Override
    public void close() {
        save();
        super.close();
    }

    @Override
    protected void closeGui(boolean showParent) {
        // Save the changes :)
        save();
        super.closeGui(showParent);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        int y = test.getY() + 20;
        int x = 10;
        for (Text t : outputMessage) {
            if (t != null) {
                textRenderer.drawWithShadow(matrixStack, t, x, y, -1);
            }
            y += textRenderer.fontHeight + 2;
        }
    }

    @Override
    public void initGui() {
        super.initGui();

        int x = 10;
        int y = 26;
        for (FilterTab tab : FilterTab.values()) {
            int width = this.getStringWidth(tab.getDisplayName()) + 10;

            if (x >= this.width - width - 10) {
                x = 10;
                y += 22;
            }

            x += this.createButton(x, y, width, tab);
        }
        x = 10;
        y += 24;
        createButtons(x, y);
    }

    private int createButton(int x, int y, int width, FilterTab tab) {
        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, tab.getDisplayName());
        button.setEnabled(this.tab != tab);
        this.addButton(button, new ButtonListenerFilterTabs(tab, this));

        return button.getWidth() + 2;
    }

    private int getWidth() {
        return 200;
    }

    public void save() {
        filter.getName().config.setValueFromString(name.getText());
        filter.getFindString().config.setValueFromString(findString.getText());
        filter.getReplaceTo().config.setValueFromString(replaceString.getText());
        filter.getTextColor().config.setValueFromString(textColor.getText());
        filter.getReplaceTextColor().config.setBooleanValue(setTextColor.isCurrentlyOn());
        filter.getReplaceType().config.setOptionListValue(replaceTypeWidget.getSelectedEntry());
        filter.getBackgroundColor()
                .config
                .setValueFromString(backgroundColor.getText());
        filter.getReplaceBackgroundColor()
                .config
                .setBooleanValue(setBackgroundColor.isCurrentlyOn());
        filter.getStripColors().config.setBooleanValue(stripColors.isCurrentlyOn());
        FiltersHandler.getInstance().loadFilters();
    }

    private void createButtons(int x, int y) {
        int windowWidth = client.getWindow().getScaledWidth();
        int defaultX = x;

        // Top buttons
        String backText = ButtonListener.Type.BACK.getDisplayName();
        int backWidth = StringUtils.getStringWidth(backText) + 10;
        ButtonGeneric back = new ButtonGeneric(x + backWidth, y, backWidth, true, backText);
        this.addButton(back, new ButtonListener(ButtonListener.Type.BACK, this));
        int topx = x;
        topx += back.getWidth() + 2;
        String exportText = ButtonListener.Type.EXPORT.getDisplayName();
        int exportWidth = StringUtils.getStringWidth(exportText) + 10;
        ButtonGeneric export = new ButtonGeneric(topx + exportWidth, y, exportWidth, true, exportText);
        this.addButton(export, new ButtonListener(ButtonListener.Type.EXPORT, this));
        y += back.getHeight() + 15;

        int dY = y;
        // Name button
        y += this.addLabel(x, y, filter.getName().config) + 1;
        name = this.addStringConfigButton(x, y, 100, 13, filter.getName().config);
        y += name.getHeight() + 10;

        // Strip colors
        int labelHeight = this.addLabel(x, y, filter.getStripColors().config);
        y += labelHeight + 2;
        stripColors = new WidgetToggle(x, y, 100, false, "advancedchatfilters.config.filter.textcoloractive", filter.getStripColors().config.getBooleanValue());
        this.addButton(stripColors, null);
        y += stripColors.getHeight() + 4;

        // Find type button
        y += this.addLabel(x, y, filter.getFindType().config) + 2;
        ConfigButtonOptionList findType = new ConfigButtonOptionList(x, y, 100, 20, filter.getFindType().config);
        this.addButton(findType, null);
        y += findType.getHeight() + 4;

        y += this.addLabel(x, y, filter.getReplaceType().config) + 2;
        replaceTypeWidget = new WidgetDropDownList<>(x, y, 100, 20, 200, 10, ImmutableList.copyOf(MatchReplaceRegistry.getInstance().getAll()), MatchReplaceRegistry.MatchReplaceOption::getDisplayName);
        replaceTypeWidget.setZLevel(getZOffset() + 100);
        replaceTypeWidget.setSelectedEntry((MatchReplaceRegistry.MatchReplaceOption) filter.getReplaceType().config.getOptionListValue());
        this.addWidget(replaceTypeWidget);
        y += stripColors.getHeight() + 4;

        // Text color
        this.addLabel(x, y, filter.getTextColor().config);
        y += this.addLabel(x + getWidth() / 2, y, filter.getReplaceTextColor().config) + 1;
        textColor =
                new WidgetColor(
                        x,
                        y,
                        getWidth() / 2 - 1,
                        18,
                        filter.getTextColor().config.get(),
                        textRenderer);
        this.addTextField(textColor, null);
        setTextColor = new WidgetToggle(
                x + getWidth() / 2 + 1,
                y, getWidth() / 2 - 1,
                false,
                "advancedchatfilters.config.filter.textcoloractive",
                filter.getReplaceTextColor().config.getBooleanValue()
        );
        this.addButton(setTextColor, null);
        y += findType.getHeight() + 2;

        // Background color
        // If the HUD module isn't active, changing this will do nothing
        boolean enableBackgroundColor = ModuleHandler.getInstance().fromId("advancedchathud").isPresent();
        if (enableBackgroundColor) {
            this.addLabel(x, y, filter.getBackgroundColor().config);
            y += this.addLabel(x + getWidth() / 2, y, filter.getReplaceBackgroundColor().config) + 1;
        }
        backgroundColor = new WidgetColor(x, y, getWidth() / 2 - 1, 18, filter.getBackgroundColor().config.get(), textRenderer);
        setBackgroundColor = new WidgetToggle(x + getWidth() / 2 + 1, y, getWidth() / 2 - 1, false, "advancedchatfilters.config.filter.backgroundcoloractive", filter.getReplaceBackgroundColor().config.getBooleanValue());
        if (enableBackgroundColor) {
            this.addTextField(backgroundColor, null);
            this.addButton(setBackgroundColor, null);
            y += findType.getHeight() + 2;
        }

        x = 120;
        // Find
        y = dY;
        y += this.addLabel(x, y, filter.getFindString().config) + 1;
        findString = this.addStringConfigButton(x, y, windowWidth - (defaultX * 2) - 120, 13, filter.getFindString().config);
        findString.setMaxLength(64000);
        findString.setText(filter.getFindString().config.getStringValue());

        y += findType.getHeight() + 10;

        // Replace
        y += this.addLabel(x, y, filter.getReplaceTo().config) + 1;
        replaceString = this.addStringConfigButton(x, y, windowWidth - (defaultX * 2) - 120, 13, filter.getReplaceTo().config);
        replaceString.setMaxLength(64000);
        replaceString.setText(filter.getReplaceTo().config.getStringValue());

        y = backgroundColor.getY() + 25;

        String testText = StringUtils.translate("advancedchatfilters.button.test");
        int testWidth = StringUtils.getStringWidth(testText) + 10;
        x = defaultX;
        ButtonGeneric updateTest = new ButtonGeneric(x, y - 1, testWidth, 15, testText);
        addButton(updateTest, (button, mouseButton) -> updateTestMessage());
        test = addStringConfigButton(defaultX + updateTest.getWidth() + 2, y, windowWidth - (defaultX * 2) - testWidth, 13, null);
        test.setMaxLength(1000);

        updateTestMessage();
    }

    private void updateTestMessage() {
        save();
        ReplaceFilter testFilter = new ReplaceFilter(
                filter.getReplaceTo().config.getStringValue().replaceAll("&", "§"),
                filter.getReplace(),
                filter.getReplaceTextColor().config.getBooleanValue() ? filter.getTextColor().config.get() : null
        );
        ParentFilter parent;
            parent = new ParentFilter(
                    filter.getFind(),
                    filter.getFindString().config.getStringValue().replace("&", "§"),
                    filter.getStripColors().config.getBooleanValue()
            );

        List<Text> built = new ArrayList<>();
        TextBuilder outputMessageBuilder = new TextBuilder();
        outputMessageBuilder.append("Input Message: ", Style.EMPTY.withFormatting(Formatting.BOLD, Formatting.GRAY));
        String testString = test.getText().replaceAll("&", "§");
        if (testString.isEmpty()) {
            outputMessageBuilder.append("None", Style.EMPTY.withFormatting(Formatting.RED));
        } else {
            outputMessageBuilder.append(testString);
        }
        built.add(outputMessageBuilder.build());
        outputMessageBuilder = new TextBuilder();
        SearchResult result;
        try {
            result = SearchResult.searchOf(testString, parent.getFindString(), parent.getFindType());
        } catch (Exception e) {
            outputMessage = new ArrayList<>();
            outputMessage.add(Text.literal("RegEx parsing error! " + e.getMessage()).formatted(Formatting.RED));
            return;
        }
        boolean searchSuccess = result.size() > 0;
        outputMessageBuilder.append("Matched: ", Style.EMPTY.withFormatting(Formatting.BOLD, Formatting.GRAY))
                .append(
                        String.valueOf(searchSuccess), Style.EMPTY.withFormatting(searchSuccess ? Formatting.GREEN : Formatting.RED)
                );
        built.add(outputMessageBuilder.build());
        outputMessageBuilder = new TextBuilder();
        MutableText input = StyleFormatter.formatText(Text.literal(testString));
        try {
            MutableText output = StyleFormatter.formatText(testFilter.filter(parent, input, input, result).orElse(input));
            outputMessageBuilder.append("Output Message: ", Style.EMPTY.withFormatting(Formatting.BOLD, Formatting.GRAY));
            outputMessageBuilder.append(output);
        } catch (NodeException e) {
            outputMessageBuilder.append("Konstruct error! " + e.getMessage(), Style.EMPTY.withFormatting(Formatting.RED));
        }
        built.add(outputMessageBuilder.build());
        outputMessage = built;
    }

    private int addLabel(int x, int y, IConfigBase config) {
        int width = StringUtils.getStringWidth(config.getConfigGuiDisplayName());
        WidgetLabelHoverable label =
                new WidgetLabelHoverable(
                        x,
                        y,
                        width,
                        8,
                        Colors.getInstance().getColorOrWhite("white").color(),
                        config.getConfigGuiDisplayName());
        label.setHoverLines(StringUtils.translate(config.getComment()));
        this.addWidget(label);
        return 8;
    }

    private GuiTextFieldGeneric addStringConfigButton(int x, int y, int width, int height, ConfigString conf) {
        GuiTextFieldGeneric name = new GuiTextFieldGeneric(x, y, width, height, this.textRenderer);
        name.setMaxLength(128);
        if (conf != null) {
            name.setText(conf.getStringValue());
        }
        this.addTextField(name, null);
        return name;
    }

    public void back() {
        closeGui(true);
    }

    public static class ButtonListener implements IButtonActionListener {

        private final GuiFilterEditor parent;
        private final Type type;

        public ButtonListener(Type type, GuiFilterEditor parent) {
            this.type = type;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            if (this.type == Type.BACK) {
                parent.back();
            } else if (this.type == Type.EXPORT) {
                parent.save();
                GuiBase.openGui(SharingScreen.fromFilter(parent.filter, parent));
            }
        }

        public enum Type {
            BACK("back"),
            EXPORT("export");

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

    public enum FilterTab {
        CONFIG("config"),
        PROCESSORS("processors");

        private final String translation;

        private static String translate(String key) {
            return "advancedchatfilters.config.filter.editor." + key;
        }

        FilterTab(String key) {
            this.translation = translate(key);
        }

        public String getDisplayName() {
            return StringUtils.translate(translation);
        }
    }

    public static class ButtonListenerFilterTabs implements IButtonActionListener {

        private final FilterTab tab;
        private final GuiFilterEditor parent;

        public ButtonListenerFilterTabs(FilterTab type, GuiFilterEditor parent) {
            this.parent = parent;
            this.tab = type;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            parent.save();
            parent.tab = tab;
            if (tab == FilterTab.CONFIG) {
                GuiBase.openGui(new GuiFilterEditor(parent.filter, parent.getParent()));
            } else if (tab == FilterTab.PROCESSORS) {
                GuiBase.openGui(new GuiFilterProcessors(this.parent));
            }
        }
    }
}
