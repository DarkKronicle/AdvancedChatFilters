/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatfilters.interfaces;

import io.github.darkkronicle.advancedchatcore.interfaces.IMessageFilter;
import io.github.darkkronicle.advancedchatcore.util.FluidText;
import io.github.darkkronicle.advancedchatcore.util.SearchResult;
import io.github.darkkronicle.advancedchatfilters.filters.ReplaceFilter;
import java.util.Optional;

/**
 * An interface to replace message content from a {@link ReplaceFilter}
 *
 * <p>Similar to {@link IMessageFilter} but supports {@link SearchResult}.
 */
public interface IMatchReplace extends IMessageFilter {
    default boolean matchesOnly() {
        return true;
    }

    /**
     * Filter text based off of previous matches.
     *
     * @param filter Filter that triggered the operation
     * @param text Text that was filtered
     * @param search Matches
     * @return Optional of new text. If returned empty the text will not be replaced
     */
    Optional<FluidText> filter(ReplaceFilter filter, FluidText text, SearchResult search);

    @Override
    default Optional<FluidText> filter(FluidText text) {
        return Optional.empty();
    }

    /**
     * Whether to forward details to children as well.
     *
     * @return Value to forward to children
     */
    default boolean useChildren() {
        return false;
    }
}
