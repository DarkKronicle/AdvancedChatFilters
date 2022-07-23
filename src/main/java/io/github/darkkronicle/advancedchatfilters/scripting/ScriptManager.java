/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatfilters.scripting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import fi.dy.masa.malilib.util.FileUtils;
import io.github.darkkronicle.advancedchatcore.interfaces.IMessageFilter;
import io.github.darkkronicle.advancedchatfilters.config.FiltersConfigStorage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ScriptManager implements IMessageFilter {

    private static final ScriptManager INSTANCE = new ScriptManager();

    private NashornSandbox engine;

    @Getter private List<ScriptFilter> filters = new ArrayList<>();

    @Getter private List<ScriptFilter> unimportedFilters = new ArrayList<>();

    public static ScriptManager getInstance() {
        return INSTANCE;
    }

    public JsonArray jsonData = new JsonArray();

    private ScriptManager() {}

    private void setupEngine() {
        engine = NashornSandboxes.create();
        // Restrict classes. Text is used for filters.
        engine.allow(Text.class);
        engine.allow(MutableText.class);
        engine.allow(Style.class);

        // Ensure no massive memory leaks. Stuff really shouldn't take over a second to happen
        engine.allowNoBraces(false);
        engine.setMaxMemory(1024 * 1024);
        engine.setMaxCPUTime(1000);
        engine.allowExitFunctions(false);
        engine.allowReadFunctions(false);
        engine.allowPrintFunctions(true);
        engine.setMaxPreparedStatements(30);
        engine.setExecutor(Executors.newSingleThreadExecutor());
    }

    /** Setup all advanced filters */
    public void init() {
        if (!FiltersConfigStorage.ADVANCED_ON.config.getBooleanValue()) {
            // Do ***not*** evaluate any code unless this is turned on.
            return;
        }
        // Setup sandbox settings as safety measures.
        setupEngine();
        unimportedFilters = new ArrayList<>();
        filters = new ArrayList<>();

        // Grab all *.js files
        File directory =
                FileUtils.getConfigDirectory()
                        .toPath()
                        .resolve("advancedchat")
                        .resolve("filters")
                        .toFile();
        if (!directory.exists()) {
            directory.mkdirs();
        }
        for (File f : directory.listFiles((dir, name) -> name.endsWith(".js"))) {
            ScriptFilter filter = ScriptFilter.fromFile(f);
            if (filter != null) {
                // If it's imported it's ok to be loaded. To import it requires an extra step from
                // the user.
                // This is to prevent code evaluating before manually confirmed.
                if (FiltersConfigStorage.IMPORTED_FILTERS.contains(filter.getId())) {
                    // Run the init
                    if (filter.runInit(engine)) {
                        filters.add(filter);
                    }
                } else {
                    unimportedFilters.add(filter);
                }
            }
        }
        applyJson(null);
    }

    /**
     * Applies JSON to Advanced Filters.
     *
     * @param array Array of stored {@link ScriptFilter}
     */
    public void applyJson(JsonArray array) {
        if (array == null) {
            array = jsonData;
        } else {
            jsonData = array;
        }
        for (JsonElement e : array) {
            if (!e.isJsonObject()) {
                continue;
            }
            JsonObject obj = e.getAsJsonObject();
            JsonElement id = obj.get("id");
            if (id == null) {
                continue;
            }
            for (ScriptFilter f : filters) {
                if (f.getId().equals(id.getAsString())) {
                    f.applyJson(obj);
                }
            }
        }
        Collections.sort(filters);
    }

    /**
     * Retrieves JSON data for the {@link ScriptFilter}'s
     *
     * @return Array of serialized data
     */
    public JsonArray getJson() {
        JsonArray array = new JsonArray();
        for (ScriptFilter filter : filters) {
            array.add(filter.getJson());
        }
        return array;
    }

    @Override
    public Optional<Text> filter(Text text) {
        if (!FiltersConfigStorage.ADVANCED_ON.config.getBooleanValue()) {
            // Do ***not*** evaluate any code unless this is turned on.
            return Optional.empty();
        }
        for (ScriptFilter filter : filters) {
            // Check if it should be run. Toggleable on/off
            if (!filter.getActive().getBooleanValue()) {
                continue;
            }
            try {
                text = filter.execute(engine, text);
            } catch (Exception e) {
                // TODO better error handling
                e.printStackTrace();
            }
        }
        return Optional.of(text);
    }
}
