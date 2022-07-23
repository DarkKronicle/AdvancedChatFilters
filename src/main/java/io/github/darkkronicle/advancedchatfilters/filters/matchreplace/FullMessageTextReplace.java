/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatfilters.filters.matchreplace;

import io.github.darkkronicle.advancedchatcore.util.Color;
import io.github.darkkronicle.advancedchatcore.util.SearchResult;
import io.github.darkkronicle.advancedchatcore.util.StringMatch;
import io.github.darkkronicle.advancedchatfilters.filters.ReplaceFilter;
import io.github.darkkronicle.advancedchatfilters.interfaces.IMatchReplace;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

@Environment(EnvType.CLIENT)
public class FullMessageTextReplace implements IMatchReplace {

    @Override
    public Optional<Text> filter(ReplaceFilter filter, Text text, SearchResult search) {
        StringBuilder totalMatch = new StringBuilder();
        for (StringMatch m : search.getMatches()) {
            totalMatch.append(m.match);
        }
        MutableText base = Text.literal("");
        Color c = filter.color;
        if (c == null) {
            base.fillStyle(text.getStyle());
        } else {
            Style original = Style.EMPTY;
            TextColor textColor = TextColor.fromRgb(c.color());
            original = original.withColor(textColor);
            base = base.setStyle(original);
        }
        return Optional.of(OnlyMatchTextReplace.formatMessage(base, filter, text, search, search.getMatches().get(0)));
    }
}
