package io.github.darkkronicle.advancedchatfilters.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigString;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchatfilters.AdvancedChatFilters;
import io.github.darkkronicle.advancedchatcore.config.ConfigStorage;
import io.github.darkkronicle.advancedchatfilters.FiltersHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


@Environment(EnvType.CLIENT)
public class FiltersConfigStorage implements IConfigHandler {

    public static final String CONFIG_FILE_NAME = AdvancedChatFilters.MOD_ID + ".json";
    public static final List<Filter> FILTERS = new ArrayList<>();
    private static final int CONFIG_VERSION = 1;
    private static final String FILTER_KEY = "filters";

    public static void loadFromFile() {

        File configFile = FileUtils.getConfigDirectory().toPath().resolve("advancedchat").resolve(CONFIG_FILE_NAME).toFile();

        if (configFile.exists() && configFile.isFile() && configFile.canRead()) {
            JsonElement element = ConfigStorage.parseJsonFile(configFile);
            Filter.FilterJsonSave filterSave = new Filter.FilterJsonSave();

            if (element != null && element.isJsonObject()) {
                JsonObject root = element.getAsJsonObject();

                JsonElement o = root.get(FILTER_KEY);
                FILTERS.clear();
                if (o != null && o.isJsonArray()) {
                    for (JsonElement el : o.getAsJsonArray()) {
                        if (el.isJsonObject()) {
                            FILTERS.add(filterSave.load(el.getAsJsonObject()));
                        }
                    }
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
            Filter.FilterJsonSave filterSave = new Filter.FilterJsonSave();

            JsonArray arr = new JsonArray();
            for (Filter f : FILTERS) {
                arr.add(filterSave.save(f));
            }
            root.add(FILTER_KEY, arr);


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
