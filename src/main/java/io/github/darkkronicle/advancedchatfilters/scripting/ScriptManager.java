package io.github.darkkronicle.advancedchatfilters.scripting;

import fi.dy.masa.malilib.util.FileUtils;
import io.github.darkkronicle.advancedchatcore.interfaces.IMessageFilter;
import io.github.darkkronicle.advancedchatcore.util.FluidText;
import io.github.darkkronicle.advancedchatfilters.config.AdvancedFilter;
import io.github.darkkronicle.advancedchatfilters.config.FiltersConfigStorage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.ScriptEngine;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ScriptManager implements IMessageFilter {

    private final static ScriptManager INSTANCE = new ScriptManager();

    private ScriptEngine nashornEngine;

    private Map<String, ScriptFilter> filters;

    public static ScriptManager getInstance() {
        return INSTANCE;
    }

    private ScriptManager() {

    }

    public void init() {
        NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
        nashornEngine = factory.getScriptEngine();
        filters = new HashMap<>();
        File directory = FileUtils.getConfigDirectory().toPath().resolve("advancedchat").resolve("filters").toFile();
        if (!directory.exists()) {
            directory.mkdirs();
        }
        for (File f : directory.listFiles((dir, name) -> name.endsWith(".js"))) {
            ScriptFilter filter = ScriptFilter.fromFile(f);
            if (filter != null) {
                filters.put(filter.getName(), filter);
            }
        }

        Map<String, AdvancedFilter> cFilters = FiltersConfigStorage.ADVANCED_FILTERS;
        Map<String, AdvancedFilter> realFilters = new HashMap<>();

        for (ScriptFilter f : filters.values()) {
            AdvancedFilter filter = cFilters.get(f.getName());
            if (filter != null) {
                realFilters.put(f.getName(), filter);
            } else {
                AdvancedFilter added = new AdvancedFilter();
                added.getName().config.setValueFromString(f.getName());
                realFilters.put(added.getName().config.getStringValue(), added);
            }
        }
        FiltersConfigStorage.ADVANCED_FILTERS.clear();
        FiltersConfigStorage.ADVANCED_FILTERS.putAll(realFilters);

    }

    @Override
    public Optional<FluidText> filter(FluidText text) {
        for (Map.Entry<String, ScriptFilter> filter : filters.entrySet()) {
            if (!FiltersConfigStorage.ADVANCED_FILTERS.get(filter.getKey()).getActive().config.getBooleanValue()) {
                continue;
            }
            try {
                text = filter.getValue().execute(nashornEngine, text);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }
}
