package io.github.darkkronicle.advancedchatfilters.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigString;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchatcore.config.ConfigStorage;
import io.github.darkkronicle.advancedchatcore.interfaces.IJsonSave;
import lombok.Data;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
@Data
public class AdvancedFilter implements Comparable<AdvancedFilter> {

    private static String translate(String key) {
        return StringUtils.translate("advancedchatfilters.config.advancedfilter." + key);
    }

    private Integer order = 0;

    private ConfigStorage.SaveableConfig<ConfigBoolean> active = ConfigStorage.SaveableConfig.fromConfig("active",
            new ConfigBoolean(translate("active"), false, translate("info.active")));

    private ConfigStorage.SaveableConfig<ConfigString> name = ConfigStorage.SaveableConfig.fromConfig(
            "name",
            new ConfigString(translate("name"), "Default", translate("info.name"))
    );


    private final ImmutableList<ConfigStorage.SaveableConfig<?>> options = ImmutableList.of(
        name,
        active
    );

    @Override
    public int compareTo(@NotNull AdvancedFilter o) {
        return order.compareTo(o.order);
    }

    public static class AdvancedFilterJsonSave implements IJsonSave<AdvancedFilter> {

        @Override
        public AdvancedFilter load(JsonObject obj) {
            AdvancedFilter f = new AdvancedFilter();
            if (obj.get("order") != null) {
                try {
                    f.setOrder(obj.get("order").getAsInt());
                } catch (Exception e) {
                    f.setOrder(0);
                }
            }
            for (ConfigStorage.SaveableConfig<?> conf : f.getOptions()) {
                IConfigBase option = conf.config;
                if (obj.has(conf.key)) {
                    option.setValueFromJsonElement(obj.get(conf.key));
                }
            }

            return f;
        }

        @Override
        public JsonObject save(AdvancedFilter filter) {
            JsonObject obj = new JsonObject();
            for (ConfigStorage.SaveableConfig<?> option : filter.getOptions()) {
                obj.add(option.key, option.config.getAsJsonElement());
            }

            obj.addProperty("order", filter.getOrder());
            return obj;
        }

    }

}
