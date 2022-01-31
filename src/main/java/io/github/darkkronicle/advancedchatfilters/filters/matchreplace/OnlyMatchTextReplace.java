/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatfilters.filters.matchreplace;

import io.github.darkkronicle.Konstruct.parser.ParseContext;
import io.github.darkkronicle.advancedchatcore.util.*;
import io.github.darkkronicle.advancedchatfilters.FiltersHandler;
import io.github.darkkronicle.advancedchatfilters.filters.ReplaceFilter;
import io.github.darkkronicle.advancedchatfilters.interfaces.IMatchReplace;
import java.util.HashMap;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class OnlyMatchTextReplace implements IMatchReplace {

    private static int getMatchIndex(SearchResult result, StringMatch match) {
        for (int i = 0; i < result.size(); i++) {
            if (match.equals(result.getMatches().get(i))) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Optional<FluidText> filter(ReplaceFilter filter, FluidText text, SearchResult search) {
        HashMap<StringMatch, FluidText.StringInsert> toReplace = new HashMap<>();
        for (StringMatch m : search.getMatches()) {
            if (filter.color == null) {
                toReplace.put(m, getReplacement(filter, text, search));
            } else {
                toReplace.put(m, getReplacement(filter, text, search, filter.color));
            }
        }
        text.replaceStrings(toReplace);
        return Optional.of(text);
    }

    public static FluidText.StringInsert getReplacement(ReplaceFilter filter, FluidText text, SearchResult result) {
        return (current, match) -> formatMessage(current, filter, text, result, match);
    }

    public static FluidText formatMessage(RawText current, ReplaceFilter filter, FluidText text, SearchResult result, StringMatch match) {
        ParseContext context = FiltersHandler.getInstance().createFilterContext(filter, text, result, match);
        String message = filter.replaceTo.parse(context).getContent().getString();
        message = result.getGroupReplacements(message, getMatchIndex(result, match));
        return new FluidText(current.withMessage(message));
    }

    private static FluidText.StringInsert getReplacement(ReplaceFilter filter, FluidText text, SearchResult result, Color color) {
        return (current, match) -> formatMessage(RawText.withColor("", color), filter, text, result, match);
    }

}
