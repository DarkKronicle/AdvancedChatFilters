/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatfilters.filters;

import io.github.darkkronicle.advancedchatcore.interfaces.IMatchProcessor;
import io.github.darkkronicle.advancedchatcore.util.SearchResult;
import io.github.darkkronicle.advancedchatfilters.FiltersHandler;
import io.github.darkkronicle.advancedchatfilters.interfaces.IFilter;
import io.github.darkkronicle.advancedchatfilters.registry.MatchProcessorRegistry;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ForwardFilter implements IFilter {

    private final MatchProcessorRegistry registry;

    public ForwardFilter(MatchProcessorRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Optional<Text> filter(ParentFilter filter, Text text, Text unfiltered, SearchResult search) {
        IMatchProcessor.Result result = null;
        for (MatchProcessorRegistry.MatchProcessorOption p : registry.getAll()) {
            if (!p.isActive()) {
                continue;
            }
            IMatchProcessor.Result r = null;
            if (!p.getOption().matchesOnly() && !search.getMatches().isEmpty()) {
                r = p.getOption().processMatches(text, unfiltered, null);
            } else if (!search.getMatches().isEmpty()) {
                r = p.getOption().processMatches(text, unfiltered, search);
            }
            if (r != null) {
                if (result == null || r.force) {
                    result = r;
                }
            }
        }
        if (result != null && !result.forward) {
            return Optional.of(FiltersHandler.TERMINATE);
        }
        return Optional.empty();
    }
}
