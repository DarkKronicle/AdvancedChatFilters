package io.github.darkkronicle.advancedchatfilters;

import fi.dy.masa.malilib.event.InitializationHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AdvancedChatFilters implements ClientModInitializer {

    public static final String MOD_ID = "advancedchatfilters";

    @Override
    public void onInitializeClient() {
        // This will run after AdvancedChatCore's because of load order
        InitializationHandler
            .getInstance()
            .registerInitializationHandler(new FiltersInitHandler());
    }
}
