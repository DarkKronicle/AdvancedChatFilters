/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatfilters.config.gui;

import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import fi.dy.masa.malilib.gui.wrappers.TextFieldWrapper;
import io.github.darkkronicle.advancedchatfilters.config.Filter;
import io.github.darkkronicle.advancedchatfilters.config.FiltersConfigStorage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;

public class WidgetListFilters extends WidgetListBase<Filter, WidgetFilterEntry> {

    public Filter filter;
    protected final List<TextFieldWrapper<? extends GuiTextFieldGeneric>> textFields =
            new ArrayList<>();

    @Override
    protected void reCreateListEntryWidgets() {
        this.textFields.clear();
        super.reCreateListEntryWidgets();
    }

    public WidgetListFilters(
            int x,
            int y,
            int width,
            int height,
            ISelectionListener<Filter> selectionListener,
            Filter filter,
            Screen parent) {
        super(x, y, width, height, selectionListener);
        this.browserEntryHeight = 22;
        this.filter = filter;
        this.setParent(parent);
    }

    public void addTextField(TextFieldWrapper<? extends GuiTextFieldGeneric> text) {
        textFields.add(text);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        clearTextFieldFocus();
        return super.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void clearTextFieldFocus() {
        for (TextFieldWrapper<? extends GuiTextFieldGeneric> field : this.textFields) {
            GuiTextFieldGeneric textField = field.getTextField();

            if (textField.isFocused()) {
                textField.setFocused(false);
                break;
            }
        }
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers) {
        for (WidgetFilterEntry widget : this.listWidgets) {
            if (widget.onKeyTyped(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return super.onKeyTyped(keyCode, scanCode, modifiers);
    }

    @Override
    protected WidgetFilterEntry createListEntryWidget(
            int x, int y, int listIndex, boolean isOdd, Filter entry) {
        return new WidgetFilterEntry(
                x,
                y,
                this.browserEntryWidth,
                this.getBrowserEntryHeightFor(entry),
                isOdd,
                entry,
                listIndex,
                this);
    }

    @Override
    protected Collection<Filter> getAllEntries() {
        return FiltersConfigStorage.FILTERS;
    }
}
