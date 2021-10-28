/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatfilters.filters.matchreplace;

import io.github.darkkronicle.advancedchatcore.util.FindType;
import io.github.darkkronicle.advancedchatcore.util.FluidText;
import io.github.darkkronicle.advancedchatcore.util.SearchResult;
import io.github.darkkronicle.advancedchatcore.util.SearchUtils;
import io.github.darkkronicle.advancedchatcore.util.StringMatch;
import io.github.darkkronicle.advancedchatfilters.filters.ReplaceFilter;
import io.github.darkkronicle.advancedchatfilters.interfaces.IMatchReplace;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class RainbowTextReplace implements IMatchReplace {

    private static final TextColor[] COLORS =
            new TextColor[] {
                TextColor.fromRgb(Formatting.RED.getColorValue()),
                TextColor.fromRgb(Formatting.DARK_RED.getColorValue()),
                TextColor.fromRgb(Formatting.GOLD.getColorValue()),
                TextColor.fromRgb(Formatting.YELLOW.getColorValue()),
                TextColor.fromRgb(Formatting.GREEN.getColorValue()),
                TextColor.fromRgb(Formatting.DARK_GREEN.getColorValue()),
                TextColor.fromRgb(Formatting.DARK_BLUE.getColorValue()),
                TextColor.fromRgb(Formatting.BLUE.getColorValue()),
                TextColor.fromRgb(Formatting.LIGHT_PURPLE.getColorValue()),
                TextColor.fromRgb(Formatting.DARK_PURPLE.getColorValue()),
            };
    private static int current = 0;

    public static TextColor next() {
        current++;
        if (current >= COLORS.length) {
            current = 0;
        }
        return COLORS[current];
    }

    @Override
    public Optional<FluidText> filter(ReplaceFilter filter, FluidText text, SearchResult search) {
        HashMap<StringMatch, FluidText.StringInsert> toReplace = new HashMap<>();
        for (StringMatch m : search.getMatches()) {
            List<StringMatch> charMatches =
                    SearchUtils.findMatches(m.match, "(?<!§)[^§]", FindType.REGEX).orElse(null);
            if (charMatches == null) {
                continue;
            }
            for (StringMatch match : charMatches) {
                toReplace.put(
                        new StringMatch(match.match, match.start + m.start, match.end + m.start),
                        (current1, match1) ->
                                new FluidText(
                                        current1.withMessage(match1.match)
                                                .withStyle(current1.getStyle().withColor(next()))));
            }
        }
        text.replaceStrings(toReplace);
        return Optional.of(text);
    }
}
