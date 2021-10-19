package io.github.darkkronicle.advancedchatfilters.scripting;

import io.github.darkkronicle.advancedchatcore.util.FluidText;
import io.github.darkkronicle.advancedchatfilters.interfaces.IFilter;
import io.github.darkkronicle.advancedchatfilters.interfaces.IScript;
import lombok.Getter;
import lombok.Setter;
import maow.owo.util.IOUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.io.IOUtils;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.SimpleBindings;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Environment(EnvType.CLIENT)
public class ScriptFilter implements IScript<FluidText> {

    @Getter
    @Setter
    private Bindings bindings;

    @Getter
    private final String name;
    @Getter
    @Setter
    private String displayName;

    private final String code;

    public ScriptFilter(String name, String code) {
        this.name = name;
        this.displayName = name;
        this.bindings = new SimpleBindings();
        this.code = code;
    }

    @Override
    public FluidText execute(ScriptEngine engine, FluidText input) throws Exception {
        engine.setBindings(getBindings(), ScriptContext.ENGINE_SCOPE);
        engine.eval(code);
        ScriptFilterContext context = new ScriptFilterContext(input);
        Invocable inv = (Invocable) engine;
        inv.invokeFunction("filter", context);
        setBindings(engine.getBindings(ScriptContext.ENGINE_SCOPE));
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
        return new ScriptFilter(name, code);
    }

}
