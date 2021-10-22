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
import javax.script.CompiledScript;
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
import org.jetbrains.annotations.NotNull;
import org.openjdk.nashorn.api.scripting.NashornScriptEngine;

@Environment(EnvType.CLIENT)
public class ScriptFilter
    implements IScript<FluidText>, Comparable<ScriptFilter> {

    @Getter
    @Setter
    private ScriptContext context;

    /**
     * ID of the filter. This is what is saved internally. For uninstalled scripts this
     * defaults to the filename without the .js extension.
     */
    @Getter
    @Setter
    private String id;

    @Setter
    @Getter
    private Integer order = 0;

    /**
     * The display name of the filter. This is what shows up in configuration.
     */
    @Getter
    @Setter
    private String displayName;

    /**
     * Author of the filter.
     */
    @Getter
    @Setter
    private String author = "";

    /**
     * Lines that will be shown when hovered in the configuration screen. If unimported a special message
     * talking about what importing does.
     */
    @Getter
    private List<String> hoverLines;

    /**
     * Whether or not this filter is OK to have it's code evaluated and run. Requires user input.
     */
    @Setter
    @Getter
    private boolean imported = false;

    /**
     * The code of the filter (in JS) that will be evaluated.
     */
    private final String code;

    private CompiledScript script;

    @Getter
    private final ConfigBoolean active = new ConfigBoolean(
        "active",
        false,
        "active"
    );

    public ScriptFilter(String id, String code) {
        this.id = id;
        this.displayName = id;
        this.code = code;
        this.context = new SimpleScriptContext();
    }

    /**
     * Set's lines for hover from a string and automatically splits the lines.
     * @param text String
     */
    public void setHoverLines(String text) {
        hoverLines = Arrays.asList(text.split("\n"));
    }

    /**
     * Evaluates the init function of the code
     * @param engine Engine to run it on
     * @throws NoSuchMethodException If the function doesn't exist
     * @throws ScriptException If there is a problem with the script
     */
    public void init(NashornSandbox engine)
        throws NoSuchMethodException, ScriptException {
        script = engine.compile(code);
        engine.eval(script);
        Invocable inv = engine.getSandboxedInvocable();
        inv.invokeFunction("setup", this);
    }

    /**
     * Executes the filter and return's the filtered {@link FluidText}
     * @param engine Engine to run the code off of
     * @param input Input text to filter
     * @return Filtered text
     * @throws Exception If script error or functions not found
     */
    @Override
    public FluidText execute(NashornSandbox engine, FluidText input)
        throws Exception {
        engine.eval(script);
        ScriptFilterContext context = new ScriptFilterContext(input);
        Invocable inv = engine.getSandboxedInvocable();
        inv.invokeFunction("filter", context);
        return context.getText();
    }

    /**
     * Instantiates a new script based off of a file. This does not evaluate any code.
     * @param file
     * @return
     */
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
        return new ScriptFilter(name, code);
    }

    /**
     * Evaluates the init function of the code. Will enforce active to false and mark the filter as imported.
     * @param engine Engine to run the code
     * @return If it was initialized correctly
     */
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

    /**
     * Applies serialized {@link JsonObject} containing data. Mainly order and if it is active.
     * @param obj Serialized data
     */
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

    /**
     * Serializes the filter into a JSON object.
     * @return Json object serialized.
     */
    public JsonObject getJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", getId());
        obj.add(active.getName(), active.getAsJsonElement());
        obj.addProperty("order", getOrder());
        return obj;
    }

    @Override
    public int compareTo(@NotNull ScriptFilter o) {
        return order.compareTo(o.order);
    }
}
