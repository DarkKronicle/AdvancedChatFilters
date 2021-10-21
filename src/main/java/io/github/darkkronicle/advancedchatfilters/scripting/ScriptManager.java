package io.github.darkkronicle.advancedchatfilters.scripting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import fi.dy.masa.malilib.util.FileUtils;
import io.github.darkkronicle.advancedchatcore.interfaces.IMessageFilter;
import io.github.darkkronicle.advancedchatcore.util.FluidText;
import io.github.darkkronicle.advancedchatcore.util.RawText;
import io.github.darkkronicle.advancedchatfilters.config.FiltersConfigStorage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ScriptManager implements IMessageFilter {

    private static final ScriptManager INSTANCE = new ScriptManager();

    private NashornSandbox nashornEngine;

    @Getter
    private List<ScriptFilter> filters = new ArrayList<>();

    @Getter
    private List<ScriptFilter> unimportedFilters = new ArrayList<>();

    public static ScriptManager getInstance() {
        return INSTANCE;
    }

    public JsonArray jsonData = new JsonArray();

    private ScriptManager() {}

    public void init() {
        if (!FiltersConfigStorage.ADVANCED_ON.config.getBooleanValue()) {
            // Do ***not*** evaluate any code unless this is turned on.
            return;
        }
        nashornEngine = NashornSandboxes.create();
        nashornEngine.allow(Text.class);
        nashornEngine.allow(FluidText.class);
        nashornEngine.allow(RawText.class);
        nashornEngine.allow(Style.class);
        nashornEngine.allowNoBraces(false);
        nashornEngine.setMaxMemory(1024 * 1024);
        nashornEngine.setMaxCPUTime(1000);
        nashornEngine.allowExitFunctions(false);
        nashornEngine.allowReadFunctions(false);
        nashornEngine.setMaxPreparedStatements(30);
        nashornEngine.setExecutor(Executors.newSingleThreadExecutor());
        unimportedFilters = new ArrayList<>();
        filters = new ArrayList<>();
        File directory = FileUtils
            .getConfigDirectory()
            .toPath()
            .resolve("advancedchat")
            .resolve("filters")
            .toFile();
        if (!directory.exists()) {
            directory.mkdirs();
        }
        for (File f : directory.listFiles((dir, name) -> name.endsWith(".js")
        )) {
            ScriptFilter filter = ScriptFilter.fromFile(f);
            if (filter != null) {
                if (
                    FiltersConfigStorage.IMPORTED_FILTERS.contains(
                        filter.getId()
                    )
                ) {
                    if (filter.runInit(nashornEngine)) {
                        filters.add(filter);
                    }
                } else {
                    unimportedFilters.add(filter);
                }
            }
        }
        applyJson(null);
    }

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
    }

    public JsonArray getJson() {
        JsonArray array = new JsonArray();
        for (ScriptFilter filter : filters) {
            array.add(filter.getJson());
        }
        return array;
    }

    @Override
    public Optional<FluidText> filter(FluidText text) {
        if (!FiltersConfigStorage.ADVANCED_ON.config.getBooleanValue()) {
            // Do ***not*** evaluate any code unless this is turned on.
            return Optional.empty();
        }
        for (ScriptFilter filter : filters) {
            if (!filter.getActive().getBooleanValue()) {
                continue;
            }
            try {
                text = filter.execute(nashornEngine, text);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }
}
