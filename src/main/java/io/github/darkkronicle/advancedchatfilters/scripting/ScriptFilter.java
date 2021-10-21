package io.github.darkkronicle.advancedchatfilters.scripting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import delight.nashornsandbox.NashornSandbox;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import io.github.darkkronicle.advancedchatcore.config.ConfigStorage;
import io.github.darkkronicle.advancedchatcore.util.FluidText;
import io.github.darkkronicle.advancedchatfilters.interfaces.IScript;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.script.SimpleScriptContext;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.io.IOUtils;
import org.openjdk.nashorn.api.scripting.NashornScriptEngine;

@Environment(EnvType.CLIENT)
public class ScriptFilter implements IScript<FluidText> {

    @Getter
    @Setter
    private ScriptContext context;

    @Getter
    @Setter
    private String id;

    @Setter
    @Getter
    private Integer order = 0;

    @Getter
    @Setter
    private String displayName;

    @Getter
    @Setter
    private String author;

    @Getter
    private List<String> hoverLines;

    @Setter
    @Getter
    private boolean imported = false;

    private final String code;

    @Getter
    private final ConfigBoolean active = new ConfigBoolean(
        "active",
        false,
        "active"
    );

    public static void maliciousCheck(String code, String id) {
        // Not great... but can make it more obvious if someone is malicious
        String[] banned = new String[] {
            "ScriptManager",
            "MinecraftClient",
            "client",
            "ConfigStorage",
        };
        for (String ban : banned) {
            if (code.toLowerCase().contains(ban.toLowerCase())) {
                throw new IllegalArgumentException(
                    "Script with name " +
                    id +
                    " is using an illegal word '" +
                    ban +
                    "'"
                );
            }
        }
    }

    public ScriptFilter(String id, String code) {
        this.id = id;
        this.displayName = id;
        this.code = code;
        this.context = new SimpleScriptContext();
    }

    public void setHoverLines(String text) {
        hoverLines = Arrays.asList(text.split("\n"));
    }

    public void init(NashornSandbox engine)
        throws NoSuchMethodException, ScriptException {
        engine.eval(code);
        Invocable inv = engine.getSandboxedInvocable();
        inv.invokeFunction("setup", this);
    }

    @Override
    public FluidText execute(NashornSandbox engine, FluidText input)
        throws Exception {
        engine.eval(code, context);
        ScriptFilterContext context = new ScriptFilterContext(input);
        Invocable inv = engine.getSandboxedInvocable();
        inv.invokeFunction("filter", context);
        return context.getText();
    }

    public static ScriptFilter fromFile(File file) {
        String fullName = file.getName();
        // Remove .js
        String name = fullName.substring(0, fullName.length() - 3);
        String code;
        try (FileInputStream stream = new FileInputStream(file)) {
            code = IOUtils.toString(stream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        ScriptFilter filter;
        try {
            filter = new ScriptFilter(name, code);
        } catch (IllegalArgumentException e) {
            // Failed
            e.printStackTrace();
            return null;
        }

        return filter;
    }

    public boolean runInit(NashornSandbox engine) {
        boolean active = getActive().getBooleanValue();
        try {
            init(engine);
        } catch (NoSuchMethodException | ScriptException e) {
            e.printStackTrace();
            return false;
        }
        getActive().setBooleanValue(active);
        imported = true;
        return true;
    }

    public void applyJson(JsonObject obj) {
        if (obj.get("order") != null) {
            try {
                setOrder(obj.get("order").getAsInt());
            } catch (Exception e) {
                setOrder(0);
            }
        }
        JsonElement activeEl = obj.get(active.getName());
        if (activeEl != null) {
            active.setValueFromJsonElement(obj.get(active.getName()));
        }
    }

    public JsonObject getJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", getId());
        obj.add(active.getName(), active.getAsJsonElement());
        obj.addProperty("order", getOrder());
        return obj;
    }
}
