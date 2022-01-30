/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatfilters.config.gui;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchatcore.config.gui.GuiConfig;
import io.github.darkkronicle.advancedchatcore.config.gui.GuiConfigHandler;
import io.github.darkkronicle.advancedchatfilters.config.Filter;
import io.github.darkkronicle.advancedchatfilters.config.FiltersConfigStorage;
import java.util.Collections;
import java.util.List;

public class GuiFilterManager extends GuiListBase<Filter, WidgetFilterEntry, WidgetListFilters>
        implements ISelectionListener<Filter> {


    public GuiFilterManager() {
        super(10, 60);
        this.title = StringUtils.translate("advancedchat.screen.main");
    }

    @Override
    protected WidgetListFilters createListWidget(int listX, int listY) {
        return new WidgetListFilters(
                listX, listY, this.getBrowserWidth(), this.getBrowserHeight(), this, null, this);
    }

    @Override
    protected int getBrowserWidth() {
        return this.width - 20;
    }

    @Override
    public void initGui() {
        super.initGui();

        int x;
        int y;

        int rows = GuiConfig.addTabButtons(this, 10, 26);

        this.setListPosition(this.getListX(), 68 + (rows - 1) * 22);
        this.reCreateListWidget();

        y = 68 + (rows - 3) * 22;

        this.getListWidget().refreshEntries();

        y += 24;
        x = this.width - 10;
        x -= this.addButton(x, y, ButtonListener.Type.ADD_FILTER) + 2;
        x -= this.addButton(x, y, ButtonListener.Type.ADVANCED) + 2;
        x -= this.addButton(x, y, ButtonListener.Type.IMPORT) + 2;
    }

    protected int addButton(int x, int y, ButtonListener.Type type) {
        ButtonGeneric button = new ButtonGeneric(x, y, -1, true, type.getDisplayName());
        this.addButton(button, new ButtonListener(type, this));

        return button.getWidth();
    }

    private static class ButtonListener implements IButtonActionListener {

        private final Type type;
        private final GuiFilterManager gui;

        public ButtonListener(Type type, GuiFilterManager gui) {
            this.type = type;
            this.gui = gui;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            if (this.type == Type.ADD_FILTER) {
                FiltersConfigStorage.FILTERS.add(Filter.getRandomFilter());
                Collections.sort(FiltersConfigStorage.FILTERS);
                this.gui.getListWidget().refreshEntries();
            } else if (this.type == Type.IMPORT) {
                GuiBase.openGui(new SharingScreen(null, gui));
            } else if (this.type == Type.ADVANCED) {
                GuiBase.openGui(new GuiAdvancedFilterManager(gui));
            }
        }

        public enum Type {
            ADD_FILTER("addfilter"),
            IMPORT("import"),
            ADVANCED("advanced");

            private static String translate(String key) {
                return "advancedchatfilters.gui.button." + key;
            }

            private final String translationKey;

            Type(String translationKey) {
                this.translationKey = translate(translationKey);
            }

            public String getDisplayName() {
                return StringUtils.translate(this.translationKey);
            }
        }
    }

    @Override
    protected int getBrowserHeight() {
        return this.height - 6 - this.getListY();
    }

    @Override
    public void onSelectionChange(Filter entry) {}

}
