/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatfilters.filters.processors;

import io.github.darkkronicle.advancedchatcore.interfaces.IMatchProcessor;
import io.github.darkkronicle.advancedchatcore.util.FluidText;
import io.github.darkkronicle.advancedchatcore.util.SearchResult;
import javax.annotation.Nullable;

public class ForwardProcessor implements IMatchProcessor {

    @Override
    public Result processMatches(
            FluidText text, @Nullable FluidText unfiltered, @Nullable SearchResult search) {
        return Result.FORCE_FORWARD;
    }
}
