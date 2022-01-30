package io.github.darkkronicle.advancedchatfilters.filters.processors;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.options.ConfigString;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import io.github.darkkronicle.Konstruct.NodeException;
import io.github.darkkronicle.Konstruct.nodes.Node;
import io.github.darkkronicle.Konstruct.reader.builder.NodeBuilder;
import io.github.darkkronicle.advancedchatcore.config.SaveableConfig;
import io.github.darkkronicle.advancedchatcore.interfaces.IJsonApplier;
import io.github.darkkronicle.advancedchatcore.interfaces.IMatchProcessor;
import io.github.darkkronicle.advancedchatcore.interfaces.IScreenSupplier;
import io.github.darkkronicle.advancedchatcore.util.FluidText;
import io.github.darkkronicle.advancedchatcore.util.SearchResult;
import io.github.darkkronicle.advancedchatfilters.AdvancedChatFilters;
import io.github.darkkronicle.advancedchatfilters.FiltersHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class KonstructProcessor implements IMatchProcessor, IJsonApplier, IScreenSupplier {

    private SaveableConfig<ConfigString> content = SaveableConfig.fromConfig("nodeData",
            new ConfigString("advancedchatfilters.config.konstruct.nodedata", "", "advancedchatfilters.config.konstruct.nodedata"));

    private Node node;

    @Override
    public Result processMatches(FluidText text, @Nullable FluidText unfiltered, @Nullable SearchResult search) {
        if (node != null) {
            node.parse(FiltersHandler.getInstance().createTextContext(text, search));
        }
        return Result.PROCESSED;
    }


    @Override
    public JsonObject save() {
        JsonObject obj = new JsonObject();
        obj.add("nodeData", content.config.getAsJsonElement());
        return obj;
    }

    @Override
    public void load(JsonElement element) {
        node = null;
        if (!element.isJsonObject()) {
            return;
        }
        JsonObject obj = element.getAsJsonObject();
        if (obj.has("nodeData")) {
            content.config.setValueFromJsonElement(obj.get("nodeData"));
            loadNode();
        }
    }

    public void loadNode() {
        try {
            node = new NodeBuilder(content.config.getStringValue()).build();
        } catch (NodeException e) {
            AdvancedChatFilters.LOGGER.log(Level.ERROR, "Problem setting up Konstruct processor.", e);
            node = null;
        }
    }

    @Override
    public Supplier<Screen> getScreen(@Nullable Screen parent) {
        return () -> new KonstructConfig(parent, this);
    }

    public static class KonstructConfig extends GuiBase {

        private GuiTextFieldGeneric text;
        private final KonstructProcessor processor;

        public KonstructConfig(Screen parent, KonstructProcessor processor) {
            this.setParent(parent);
            this.processor = processor;
        }

        @Override
        public void initGui() {
            text = new GuiTextFieldGeneric(10, 26, MinecraftClient.getInstance().getWindow().getScaledWidth() - 20, 13, MinecraftClient.getInstance().textRenderer);
            text.setMaxLength(64000);
            text.setText(processor.content.config.getStringValue());
            addTextField(text, null);
        }

        @Override
        public void closeGui(boolean showParent) {
            processor.content.config.setValueFromString(text.getText());
            processor.loadNode();
            super.closeGui(showParent);
        }


    }
}
