package io.github.darkkronicle.advancedchatfilters.filters.processors;

import io.github.darkkronicle.advancedchatcore.interfaces.IMatchProcessor;
import io.github.darkkronicle.advancedchatcore.util.FluidText;
import io.github.darkkronicle.advancedchatcore.util.SearchResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.MessageType;

@Environment(EnvType.CLIENT)
public class ActionBarProcessor implements IMatchProcessor {

    @Override
    public Result processMatches(
        FluidText text,
        FluidText unfiltered,
        SearchResult matches
    ) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return Result.PROCESSED;
        }
        client.inGameHud.addChatMessage(
            MessageType.GAME_INFO,
            text,
            client.player.getUuid()
        );
        return Result.PROCESSED;
    }
}
