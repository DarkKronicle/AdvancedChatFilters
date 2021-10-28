/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatfilters.config.gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchatfilters.FiltersHandler;
import io.github.darkkronicle.advancedchatfilters.config.Filter;
import io.github.darkkronicle.advancedchatfilters.config.FiltersConfigStorage;
import net.minecraft.client.gui.screen.Screen;

/** Screen for importing and exporting {@link Filter} */
public class SharingScreen extends GuiBase {

    private final String starting;
    private static final Gson GSON = new GsonBuilder().create();
    private GuiTextFieldGeneric text;

    public SharingScreen(String starting, Screen parent) {
        this.setParent(parent);
        this.setTitle(StringUtils.translate("advancedchatfilters.gui.menu.import"));
        this.starting = starting;
    }

    /** Creates a SharingScreen from a filter */
    public static SharingScreen fromFilter(Filter filter, Screen parent) {
        Filter.FilterJsonSave filterJsonSave = new Filter.FilterJsonSave();
        return new SharingScreen(GSON.toJson(filterJsonSave.save(filter)), parent);
    }

    @Override
    public void init() {
        int x = this.width / 2 - 150;
        int y = 50;
        text = new GuiTextFieldGeneric(x, y, 300, 20, client.textRenderer);
        y -= 24;
        text.setMaxLength(12800);
        if (starting != null) {
            text.setText(starting);
            text.setTextFieldFocused(true);
        }
        text.changeFocus(true);
        text.setDrawsBackground(true);
        text.setEditable(true);
        text.changeFocus(true);
        this.addTextField(text, null);
        String filterName = ButtonListener.Type.IMPORT_FILTER.getDisplayName();
        int filterWidth = StringUtils.getStringWidth(filterName) + 10;
        this.addButton(
                new ButtonGeneric(x, y, filterWidth, 20, filterName),
                new ButtonListener(ButtonListener.Type.IMPORT_FILTER, this));
    }

    private static class ButtonListener implements IButtonActionListener {

        public enum Type {
            IMPORT_FILTER("importfilter");

            public final String translationString;

            private static String translate(String key) {
                return "advancedchatfilters.gui.button." + key;
            }

            Type(String key) {
                this.translationString = translate(key);
            }

            public String getDisplayName() {
                return StringUtils.translate(translationString);
            }
        }

        private final Type type;
        private final SharingScreen parent;

        public ButtonListener(Type type, SharingScreen parent) {
            this.type = type;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            try {
                if (parent.text.getText().equals("")) {
                    throw new NullPointerException("Message can't be blank!");
                }
                if (type == Type.IMPORT_FILTER) {
                    Filter.FilterJsonSave filterSave = new Filter.FilterJsonSave();
                    // If you don't use deprecated it won't work
                    Filter filter =
                            filterSave.load(
                                    new JsonParser()
                                            .parse(parent.text.getText())
                                            .getAsJsonObject());
                    if (filter == null) {
                        throw new NullPointerException("Filter is null!");
                    }
                    FiltersConfigStorage.FILTERS.add(filter);
                    FiltersHandler.getInstance().loadFilters();
                    parent.addGuiMessage(
                            Message.MessageType.SUCCESS,
                            5000,
                            StringUtils.translate("advancedchat.gui.message.successful"));
                }
            } catch (Exception e) {
                parent.addGuiMessage(
                        Message.MessageType.ERROR,
                        10000,
                        StringUtils.translate("advancedchat.gui.message.error")
                                + ": "
                                + e.getMessage());
            }
        }
    }
}
