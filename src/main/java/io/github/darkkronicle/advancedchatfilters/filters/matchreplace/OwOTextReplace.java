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
import io.github.darkkronicle.advancedchatcore.util.RawText;
import io.github.darkkronicle.advancedchatcore.util.SearchResult;
import io.github.darkkronicle.advancedchatcore.util.SearchUtils;
import io.github.darkkronicle.advancedchatcore.util.StringMatch;
import io.github.darkkronicle.advancedchatfilters.filters.ReplaceFilter;
import io.github.darkkronicle.advancedchatfilters.interfaces.IMatchReplace;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import maow.owo.OwO;
import maow.owo.util.ParsingUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Style;

@Environment(EnvType.CLIENT)
public class OwOTextReplace implements IMatchReplace {

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
                                        current.withMessage(OwO.INSTANCE.translate(match1.match))));
            }
        }
        text.replaceStrings(replaceMatches);
        text.append(
                new RawText(
                        " "
                                + ParsingUtil.parseRandomizedLetters(
                                        ParsingUtil.getRandomElement(OwO.INSTANCE.getSuffixes())),
                        Style.EMPTY),
                true);
        return Optional.of(text);
    }
}
