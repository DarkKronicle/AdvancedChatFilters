/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatfilters.filters.matchreplace;

import dev.maow.owo.api.OwO;
import dev.maow.owo.api.OwOProvider;
import dev.maow.owo.util.OwOFactory;
import io.github.darkkronicle.advancedchatcore.util.FindType;
import io.github.darkkronicle.advancedchatcore.util.FluidText;
import io.github.darkkronicle.advancedchatcore.util.RawText;
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
import net.minecraft.text.Style;

@Environment(EnvType.CLIENT)
public class OwOTextReplace implements IMatchReplace {

    private final OwO owo;

    public OwOTextReplace() {
        this.owo = OwOFactory.INSTANCE.create();
    }

    @Override
    public Optional<FluidText> filter(ReplaceFilter filter, FluidText text, SearchResult search) {
        HashMap<StringMatch, FluidText.StringInsert> replaceMatches = new HashMap<>();
        for (StringMatch match : search.getMatches()) {
            Optional<List<StringMatch>> omatches =
                    SearchUtils.findMatches(match.match, "(?<!§)([A-Za-z]+)", FindType.REGEX);
            if (!omatches.isPresent()) {
                continue;
            }
            List<StringMatch> foundMatches = omatches.get();
            foundMatches.forEach(
                    stringMatch -> {
                        stringMatch.start += match.start;
                        stringMatch.end += match.start;
                    });
            for (StringMatch m : foundMatches) {
                replaceMatches.put(
                        m,
                        (current, match1) ->
                                new FluidText(
                                        current.withMessage(owo.translate(OwO.TranslateMode.PLAIN, match1.match))));
            }
        }
        text.replaceStrings(replaceMatches);
        return Optional.of(text);
    }
}
