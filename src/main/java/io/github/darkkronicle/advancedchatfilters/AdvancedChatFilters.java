package io.github.darkkronicle.advancedchatfilters;

import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.gui.GuiBase;
import io.github.darkkronicle.advancedchatcore.config.gui.GuiConfigHandler;
import io.github.darkkronicle.advancedchatfilters.config.gui.GuiFilterManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class AdvancedChatFilters implements ClientModInitializer {

    public static final String MOD_ID = "advancedchatfilters";

    @Override
    public void onInitializeClient() {
        // This will run after AdvancedChatCore's because of load order
        InitializationHandler.getInstance().registerInitializationHandler(new FiltersInitHandler());
        KeyBinding keyBinding = new KeyBinding(
                "advancedchat.key.opendumbstuff",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_U,
                "advancedchat.category.keys"
        );
        KeyBindingHelper.registerKeyBinding(keyBinding);
        ClientTickEvents.START_CLIENT_TICK.register(s -> {
            if (keyBinding.wasPressed()) {
                GuiBase.openGui(new GuiFilterManager(GuiConfigHandler.getInstance().getButtons()));
            }
        });
    }

}
