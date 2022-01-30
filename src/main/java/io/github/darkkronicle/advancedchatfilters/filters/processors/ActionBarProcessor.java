/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatfilters.filters.processors;

import fi.dy.masa.malilib.util.InfoUtils;
import io.github.darkkronicle.Konstruct.functions.Function;
import io.github.darkkronicle.Konstruct.functions.NamedFunction;
import io.github.darkkronicle.Konstruct.nodes.Node;
import io.github.darkkronicle.Konstruct.parser.IntRange;
import io.github.darkkronicle.Konstruct.parser.ParseContext;
import io.github.darkkronicle.Konstruct.parser.Result;
import io.github.darkkronicle.Konstruct.type.NullObject;
import io.github.darkkronicle.advancedchatcore.interfaces.IMatchProcessor;
import io.github.darkkronicle.advancedchatcore.util.FluidText;
import io.github.darkkronicle.advancedchatcore.util.RawText;
import io.github.darkkronicle.advancedchatcore.util.SearchResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.MessageType;
import net.minecraft.text.Style;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ActionBarProcessor implements IMatchProcessor {

    public static class ActionBarFunction implements NamedFunction {

        @Override
        public String getName() {
            return "toActionBar";
        }

        @Override
        public io.github.darkkronicle.Konstruct.parser.Result parse(ParseContext context, List<Node> input) {
            io.github.darkkronicle.Konstruct.parser.Result r1 = Function.parseArgument(context, input, 0);
            FluidText text = new FluidText(new RawText(r1.getContent().getString(), Style.EMPTY));
            MinecraftClient.getInstance().inGameHud.addChatMessage(MessageType.GAME_INFO, text, MinecraftClient.getInstance().player.getUuid());
            return io.github.darkkronicle.Konstruct.parser.Result.success(new NullObject());
        }

        @Override
        public IntRange getArgumentCount() {
            return IntRange.of(1);
        }
    }

    @Override
    public Result processMatches(FluidText text, FluidText unfiltered, SearchResult matches) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return Result.PROCESSED;
        }
        client.inGameHud.addChatMessage(MessageType.GAME_INFO, text, client.player.getUuid());
        return Result.PROCESSED;
    }
}
