package io.github.darkkronicle.advancedchatfilters;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchatcore.chat.MessageDispatcher;
import io.github.darkkronicle.advancedchatfilters.config.FiltersConfigStorage;
import io.github.darkkronicle.advancedchatcore.chat.ChatHistory;
import io.github.darkkronicle.advancedchatcore.config.gui.GuiConfigHandler;
import io.github.darkkronicle.advancedchatfilters.config.gui.GuiFilterManager;
import io.github.darkkronicle.advancedchatfilters.filters.matchreplace.ChildrenTextReplace;
import io.github.darkkronicle.advancedchatfilters.filters.matchreplace.FullMessageTextReplace;
import io.github.darkkronicle.advancedchatfilters.filters.matchreplace.OnlyMatchTextReplace;
import io.github.darkkronicle.advancedchatfilters.filters.matchreplace.OwOTextReplace;
import io.github.darkkronicle.advancedchatfilters.filters.matchreplace.RainbowTextReplace;
import io.github.darkkronicle.advancedchatfilters.filters.matchreplace.ReverseTextReplace;
import io.github.darkkronicle.advancedchatfilters.filters.matchreplace.RomanNumeralTextReplace;
import io.github.darkkronicle.advancedchatfilters.filters.processors.ActionBarProcessor;
import io.github.darkkronicle.advancedchatfilters.filters.processors.ForwardProcessor;
import io.github.darkkronicle.advancedchatfilters.filters.processors.NarratorProcessor;
import io.github.darkkronicle.advancedchatfilters.filters.processors.SoundProcessor;
import io.github.darkkronicle.advancedchatfilters.registry.MatchProcessorRegistry;
import io.github.darkkronicle.advancedchatfilters.registry.MatchReplaceRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

import java.util.List;

@Environment(EnvType.CLIENT)
public class FiltersInitHandler implements IInitializationHandler {

    @Override
    public void registerModHandlers() {
        ConfigManager.getInstance().registerConfigHandler(AdvancedChatFilters.MOD_ID, new FiltersConfigStorage());
        GuiConfigHandler.getInstance().addGuiSection(new GuiConfigHandler.Tab() {
            @Override
            public String getName() {
                return StringUtils.translate("advancedchatfilters.config.tab.filters");
            }

            @Override
            public Screen getScreen(List<GuiConfigHandler.TabButton> buttons) {
                GuiConfigHandler.getInstance().activeTab = this.getName();
                return new GuiFilterManager(buttons);
            }
        });

        // Make it so filters do stuff
        MessageDispatcher.getInstance().registerPreFilter(FiltersHandler.getInstance(), -1);

        // Initiate match types
        MatchReplaceRegistry matchRegistry = MatchReplaceRegistry.getInstance();
        matchRegistry.register(() -> null, "none", "advancedchatfilters.config.replacetype.none", "advancedchatfilters.config.replacetype.info.none", true, true);
        matchRegistry.register(ChildrenTextReplace::new, "children", "advancedchatfilters.config.replacetype.children", "advancedchatfilters.config.replacetype.info.children", true, false);
        matchRegistry.register(FullMessageTextReplace::new, "fullmessage", "advancedchatfilters.config.replacetype.fullmessage", "advancedchatfilters.config.replacetype.info.fullmessage", true, false);
        matchRegistry.register(OnlyMatchTextReplace::new, "onlymatch", "advancedchatfilters.config.replacetype.onlymatch", "advancedchatfilters.config.replacetype.info.onlymatch", true, false);
        matchRegistry.register(OwOTextReplace::new, "owo", "advancedchatfilters.config.replacetype.owo", "advancedchatfilters.config.replacetype.info.owo", true, false);
        matchRegistry.register(RainbowTextReplace::new, "rainbow", "advancedchatfilters.config.replacetype.rainbow", "advancedchatfilters.config.replacetype.info.rainbow", true, false);
        matchRegistry.register(RomanNumeralTextReplace::new, "romannumeral", "advancedchatfilters.config.replacetype.romannumeral", "advancedchatfilters.config.replacetype.info.romannumeral", true, false);
        matchRegistry.register(ReverseTextReplace::new, "reverse", "advancedchatfilters.config.replacetype.reverse", "advancedchatfilters.config.replacetype.info.reverse", true, false);

        // Initiate processors
        MatchProcessorRegistry processorRegistry = MatchProcessorRegistry.getInstance();
        processorRegistry.register(ForwardProcessor::new, "forward", "advancedchatfilters.config.processor.forward", "advancedchatfilters.config.processor.info.forward", true, false);
        processorRegistry.register(ActionBarProcessor::new, "actionbar", "advancedchatfilters.config.processor.actionbar", "advancedchatfilters.config.processor.info.actionbar", false, false);
        processorRegistry.register(SoundProcessor::new, "sound", "advancedchatfilters.config.processor.sound", "advancedchatfilters.config.processor.info.sound", false, false);
        processorRegistry.register(NarratorProcessor::new, "narrator", "advancedchatfilters.config.processor.narrator", "advancedchatfilters.config.processor.info.narrator", false, false);

    }

}
