package io.github.darkkronicle.advancedchatfilters.filters.processors;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigString;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonOnOff;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.Konstruct.functions.Function;
import io.github.darkkronicle.Konstruct.functions.NamedFunction;
import io.github.darkkronicle.Konstruct.nodes.Node;
import io.github.darkkronicle.Konstruct.parser.IntRange;
import io.github.darkkronicle.Konstruct.parser.ParseContext;
import io.github.darkkronicle.Konstruct.type.NullObject;
import io.github.darkkronicle.advancedchatcore.config.SaveableConfig;
import io.github.darkkronicle.advancedchatcore.config.gui.widgets.WidgetLabelHoverable;
import io.github.darkkronicle.advancedchatcore.gui.buttons.BackButtonListener;
import io.github.darkkronicle.advancedchatcore.gui.buttons.Buttons;
import io.github.darkkronicle.advancedchatcore.interfaces.IClosable;
import io.github.darkkronicle.advancedchatcore.interfaces.IJsonApplier;
import io.github.darkkronicle.advancedchatcore.interfaces.IMatchProcessor;
import io.github.darkkronicle.advancedchatcore.interfaces.IScreenSupplier;
import io.github.darkkronicle.advancedchatcore.util.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Supplier;

public class ToastProcessor implements IMatchProcessor, IScreenSupplier, IJsonApplier {

    public static class ToastFunction implements NamedFunction {

        @Override
        public String getName() {
            return "toToast";
        }

        @Override
        public io.github.darkkronicle.Konstruct.parser.Result parse(ParseContext context, List<Node> input) {
            io.github.darkkronicle.Konstruct.parser.Result r1 = Function.parseArgument(context, input, 0);
            Text titleText = StyleFormatter.formatText(new FluidText(new RawText(r1.getContent().getString(), Style.EMPTY)));
            Text descriptionText = null;
            boolean instant = false;
            if (input.size() > 1) {
                io.github.darkkronicle.Konstruct.parser.Result r2 = Function.parseArgument(context, input, 1);
                descriptionText = StyleFormatter.formatText(new FluidText(new RawText(r2.getContent().getString(), Style.EMPTY)));
            }
            if (input.size() > 2) {
                io.github.darkkronicle.Konstruct.parser.Result r3 = Function.parseArgument(context, input, 2);
                instant = r3.getContent().getBoolean();
            }
            ToastManager manager = MinecraftClient.getInstance().getToastManager();
            if (instant) {
                SystemToast.show(manager, SystemToast.Type.PERIODIC_NOTIFICATION, titleText, descriptionText);
            } else {
                SystemToast.add(manager, SystemToast.Type.PERIODIC_NOTIFICATION, titleText, descriptionText);
            }
            return io.github.darkkronicle.Konstruct.parser.Result.success(new NullObject());
        }

        @Override
        public IntRange getArgumentCount() {
            return IntRange.of(1, 3);
        }
    }

    private static String translate(String key) {
        return "advancedchatfilters.config.processor.toast." + key;
    }

    private final SaveableConfig<ConfigString> title =
            SaveableConfig.fromConfig(
                    "title",
                    new ConfigString(translate("title"), "Cool title", translate("info.title")));

    private final SaveableConfig<ConfigString> description =
            SaveableConfig.fromConfig(
                    "message",
                    new ConfigString(translate("description"), "", translate("info.description")));

    @Override
    public Result processMatches(FluidText text, FluidText unfiltered, SearchResult search) {
        Text titleText = text;
        if (!title.config.getStringValue().isEmpty()) {
            String content = search.getGroupReplacements(title.config.getStringValue(), 0);
            titleText = StyleFormatter.formatText(new FluidText(new RawText(content, Style.EMPTY)));
        }
        Text descriptionText = null;
        if (!description.config.getStringValue().isEmpty() && !title.config.getStringValue().isEmpty()) {
            String content = search.getGroupReplacements(description.config.getStringValue(), 0);
            descriptionText = StyleFormatter.formatText(new FluidText(new RawText(content, Style.EMPTY)));
        }
        ToastManager manager = MinecraftClient.getInstance().getToastManager();
        SystemToast.add(manager, SystemToast.Type.PERIODIC_NOTIFICATION, titleText, descriptionText);
        return Result.getFromBool(true);
    }

    @Override
    public JsonObject save() {
        JsonObject obj = new JsonObject();
        obj.add(title.key, title.config.getAsJsonElement());
        obj.add(description.key, description.config.getAsJsonElement());
        return obj;
    }

    @Override
    public void load(JsonElement element) {
        if (element == null || !element.isJsonObject()) {
            return;
        }
        JsonObject obj = element.getAsJsonObject();
        title.config.setValueFromJsonElement(obj.get(title.key));
        description.config.setValueFromJsonElement(obj.get(description.key));
    }

    @Override
    public Supplier<Screen> getScreen(Screen parent) {
        return () -> new SenderScreen(parent);
    }

    public class SenderScreen extends GuiBase implements IClosable {

        private GuiTextFieldGeneric titleField;
        private GuiTextFieldGeneric descriptionField;

        @Override
        public void close() {
            save();
            super.close();
        }

        public SenderScreen(Screen parent) {
            this.setParent(parent);
            this.setTitle(StringUtils.translate("advancedchatfilters.screen.toast"));
        }

        @Override
        protected void closeGui(boolean showParent) {
            save();
            super.closeGui(showParent);
        }

        public void save() {
            ToastProcessor.this.title.config.setValueFromString(titleField.getText());
            description.config.setValueFromString(descriptionField.getText());
        }

        private int getWidth() {
            return 300;
        }

        @Override
        public void initGui() {
            super.initGui();
            int x = 10;
            int y = 26;

            this.addButton(Buttons.BACK.createButton(x, y), new BackButtonListener(this));
            y += 30;
            y += this.addLabel(x, y, ToastProcessor.this.title.config) + 1;
            titleField = new GuiTextFieldGeneric(x, y, getWidth(), 20, MinecraftClient.getInstance().textRenderer);
            titleField.setMaxLength(64000);
            titleField.setText(ToastProcessor.this.title.config.getStringValue());
            this.addTextField(titleField, null);
            y += 30;
            y += this.addLabel(x, y, description.config) + 1;
            descriptionField = new GuiTextFieldGeneric(x, y, getWidth(), 20, MinecraftClient.getInstance().textRenderer);
            descriptionField.setMaxLength(64000);
            descriptionField.setText(description.config.getStringValue());
            this.addTextField(descriptionField, null);
        }

        private int addLabel(int x, int y, IConfigBase config) {
            int width = StringUtils.getStringWidth(config.getConfigGuiDisplayName());
            WidgetLabelHoverable label =
                    new WidgetLabelHoverable(
                            x,
                            y,
                            width,
                            8,
                            Colors.getInstance().getColorOrWhite("white").color(),
                            config.getConfigGuiDisplayName());
            label.setHoverLines(StringUtils.translate(config.getComment()));
            this.addWidget(label);
            return 8;
        }

        @Override
        public void close(ButtonBase base) {
            this.closeGui(true);
        }

    }

}
