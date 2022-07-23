/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatfilters.filters.processors;

import io.github.darkkronicle.Konstruct.functions.Function;
import io.github.darkkronicle.Konstruct.functions.NamedFunction;
import io.github.darkkronicle.Konstruct.nodes.Node;
import io.github.darkkronicle.Konstruct.parser.IntRange;
import io.github.darkkronicle.Konstruct.parser.ParseContext;
import io.github.darkkronicle.Konstruct.type.NullObject;
import io.github.darkkronicle.advancedchatcore.interfaces.IMatchProcessor;
import io.github.darkkronicle.advancedchatcore.util.SearchResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

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
            Text text = Text.literal(r1.getContent().getString());
            MinecraftClient.getInstance().player.sendMessage(text, true);
            return io.github.darkkronicle.Konstruct.parser.Result.success(new NullObject());
        }

        @Override
        public IntRange getArgumentCount() {
            return IntRange.of(1);
        }
    }

    @Override
    public Result processMatches(Text text, Text unfiltered, SearchResult matches) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return Result.PROCESSED;
        }
        client.player.sendMessage(text, true);
        return Result.PROCESSED;
    }
}
