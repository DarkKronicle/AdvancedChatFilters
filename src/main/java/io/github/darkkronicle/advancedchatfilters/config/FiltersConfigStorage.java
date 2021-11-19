/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatfilters.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import io.github.darkkronicle.advancedchatcore.config.ConfigStorage;
import io.github.darkkronicle.advancedchatcore.config.SaveableConfig;
import io.github.darkkronicle.advancedchatfilters.AdvancedChatFilters;
import io.github.darkkronicle.advancedchatfilters.FiltersHandler;
import io.github.darkkronicle.advancedchatfilters.scripting.ScriptManager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class FiltersConfigStorage implements IConfigHandler {

    public static final String CONFIG_FILE_NAME = AdvancedChatFilters.MOD_ID + ".json";
    public static final List<Filter> FILTERS = new ArrayList<>();
    private static final int CONFIG_VERSION = 1;
    private static final String FILTER_KEY = "filters";
    private static final String ADVANCED_FILTER_KEY = "advancedfilters";
    public static final List<String> IMPORTED_FILTERS = new ArrayList<>();
    private static final String IMPORTED_KEY = "importedfilters";

    public static final SaveableConfig<ConfigBoolean> ADVANCED_ON =
            SaveableConfig.fromConfig(
                    "advanced_filters_on",
                    new ConfigBoolean("advanced_filters_on", false, "advanced_filters_on"));

    public static void loadFromFile() {
        File configFile =
                FileUtils.getConfigDirectory()
                        .toPath()
                        .resolve("advancedchat")
                        .resolve(CONFIG_FILE_NAME)
                        .toFile();

        if (configFile.exists() && configFile.isFile() && configFile.canRead()) {
            JsonElement element = ConfigStorage.parseJsonFile(configFile);
            Filter.FilterJsonSave filterSave = new Filter.FilterJsonSave();

            if (element != null && element.isJsonObject()) {
                ScriptManager.getInstance().init();
                JsonObject root = element.getAsJsonObject();

                JsonElement advanced = root.get(ADVANCED_ON.key);
                if (advanced != null) {
                    ADVANCED_ON.config.setValueFromJsonElement(advanced);
                }

                JsonElement o = root.get(FILTER_KEY);
                FILTERS.clear();
                if (o != null && o.isJsonArray()) {
                    for (JsonElement el : o.getAsJsonArray()) {
                        if (el.isJsonObject()) {
                            FILTERS.add(filterSave.load(el.getAsJsonObject()));
                        }
                    }
                }

                IMPORTED_FILTERS.clear();
                JsonElement i = root.get(IMPORTED_KEY);
                if (i != null && i.isJsonArray()) {
                    for (JsonElement e : i.getAsJsonArray()) {
                        IMPORTED_FILTERS.add(e.getAsString());
                    }
                }

                JsonElement adv = root.get(ADVANCED_FILTER_KEY);
                if (adv != null && adv.isJsonArray()) {
                    ScriptManager.getInstance().applyJson(adv.getAsJsonArray());
                }

                int version = JsonUtils.getIntegerOrDefault(root, "configVersion", 0);
            }
        }
        FiltersHandler.getInstance().loadFilters();
    }

    public static void saveFromFile() {
        File dir = FileUtils.getConfigDirectory().toPath().resolve("advancedchat").toFile();

        if ((dir.exists() && dir.isDirectory()) || dir.mkdirs()) {
            JsonObject root = new JsonObject();
            root.add(ADVANCED_ON.key, ADVANCED_ON.config.getAsJsonElement());

            Filter.FilterJsonSave filterSave = new Filter.FilterJsonSave();
            JsonArray arr = new JsonArray();
            for (Filter f : FILTERS) {
                arr.add(filterSave.save(f));
            }

            JsonArray imported = new JsonArray();
            for (String s : IMPORTED_FILTERS) {
                imported.add(s);
            }
            root.add(FILTER_KEY, arr);
            root.add(ADVANCED_FILTER_KEY, ScriptManager.getInstance().getJson());
            root.add(IMPORTED_KEY, imported);
            root.add("config_version", new JsonPrimitive(CONFIG_VERSION));

            ConfigStorage.writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));
        }
    }

    @Override
    public void load() {
        loadFromFile();
    }

    @Override
    public void save() {
        saveFromFile();
    }
}
