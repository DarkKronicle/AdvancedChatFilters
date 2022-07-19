/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatfilters;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import io.github.darkkronicle.advancedchatcore.chat.MessageDispatcher;
import io.github.darkkronicle.advancedchatcore.config.gui.GuiConfigHandler;
import io.github.darkkronicle.advancedchatfilters.config.FiltersConfigStorage;
import io.github.darkkronicle.advancedchatfilters.config.gui.GuiFilterManager;
import io.github.darkkronicle.advancedchatfilters.filters.matchreplace.FullMessageTextReplace;
import io.github.darkkronicle.advancedchatfilters.filters.matchreplace.OnlyMatchTextReplace;
import io.github.darkkronicle.advancedchatfilters.filters.processors.*;
import io.github.darkkronicle.advancedchatfilters.registry.MatchProcessorRegistry;
import io.github.darkkronicle.advancedchatfilters.registry.MatchReplaceRegistry;
import io.github.darkkronicle.advancedchatfilters.scripting.ScriptManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class FiltersInitHandler implements IInitializationHandler {

    @Override
    public void registerModHandlers() {
        ConfigManager.getInstance()
                .registerConfigHandler(AdvancedChatFilters.MOD_ID, new FiltersConfigStorage());
        GuiConfigHandler.getInstance().addTab(
                GuiConfigHandler.wrapScreen("filters", "advancedchatfilters.config.tab.filters", (parent) -> new GuiFilterManager())
        );

        // Make it so filters do stuff
        MessageDispatcher.getInstance().registerPreFilter(FiltersHandler.getInstance(), -1);
        ScriptManager.getInstance().init();
        MessageDispatcher.getInstance().registerPreFilter(ScriptManager.getInstance(), -1);

        // Initiate match types
        MatchReplaceRegistry matchRegistry = MatchReplaceRegistry.getInstance();
        matchRegistry.register(
                () -> null,
                "none",
                "advancedchatfilters.config.replacetype.none",
                "advancedchatfilters.config.replacetype.info.none",
                true,
                true);
        matchRegistry.register(
                FullMessageTextReplace::new,
                "fullmessage",
                "advancedchatfilters.config.replacetype.fullmessage",
                "advancedchatfilters.config.replacetype.info.fullmessage",
                true,
                false);
        matchRegistry.register(
                OnlyMatchTextReplace::new,
                "onlymatch",
                "advancedchatfilters.config.replacetype.onlymatch",
                "advancedchatfilters.config.replacetype.info.onlymatch",
                true,
                true);

        // Initiate processors
        MatchProcessorRegistry processorRegistry = MatchProcessorRegistry.getInstance();
        processorRegistry.register(
                ForwardProcessor::new,
                "forward",
                "advancedchatfilters.config.processor.forward",
                "advancedchatfilters.config.processor.info.forward",
                true,
                false);
        processorRegistry.register(
                ActionBarProcessor::new,
                "actionbar",
                "advancedchatfilters.config.processor.actionbar",
                "advancedchatfilters.config.processor.info.actionbar",
                false,
                false);
        processorRegistry.register(
                SoundProcessor::new,
                "sound",
                "advancedchatfilters.config.processor.sound",
                "advancedchatfilters.config.processor.info.sound",
                false,
                false);
        processorRegistry.register(
                NarratorProcessor::new,
                "narrator",
                "advancedchatfilters.config.processor.narrator",
                "advancedchatfilters.config.processor.info.narrator",
                false,
                false);
        processorRegistry.register(
                ToastProcessor::new,
                "toast",
                "advancedchatfilters.config.processor.toast",
                "advancedchatfilters.config.processor.info.toast",
                false,
                false);
        processorRegistry.register(
                KonstructProcessor::new,
                "konstruct",
                "advancedchatfilters.config.processor.konstruct",
                "advancedchatfilters.config.processor.info.konstruct",
                false,
                false
        );
    }
}
