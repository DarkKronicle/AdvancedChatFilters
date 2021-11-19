/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatfilters.filters.matchreplace;

import io.github.darkkronicle.advancedchatcore.util.FluidText;
import io.github.darkkronicle.advancedchatcore.util.RawText;
import io.github.darkkronicle.advancedchatcore.util.SearchResult;
import io.github.darkkronicle.advancedchatcore.util.StringMatch;
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
                toReplace.put(
                        m,
                        (current, match) ->
                                new FluidText(
                                        current.withMessage(
                                                search.getGroupReplacements(
                                                        filter.replaceTo.replaceAll(
                                                                "%MATCH%", match.match),
                                                        getMatchIndex(search, match)))));
            } else {
                toReplace.put(
                        m,
                        (current, match) ->
                                new FluidText(
                                        RawText.withColor(
                                                search.getGroupReplacements(
                                                        filter.replaceTo.replaceAll(
                                                                "%MATCH%", match.match),
                                                        getMatchIndex(search, match)),
                                                filter.color)));
            }
        }
        text.replaceStrings(toReplace);
        return Optional.of(text);
    }
}
