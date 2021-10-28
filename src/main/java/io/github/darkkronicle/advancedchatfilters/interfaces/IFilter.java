/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatfilters.interfaces;

import io.github.darkkronicle.advancedchatcore.interfaces.IMessageFilter;
import io.github.darkkronicle.advancedchatcore.util.ColorUtil;
import io.github.darkkronicle.advancedchatcore.util.FluidText;
import io.github.darkkronicle.advancedchatcore.util.SearchResult;
import io.github.darkkronicle.advancedchatfilters.filters.ParentFilter;
import java.util.Optional;

/** An interface for chat filters. Filters can change text content or background color. */
public interface IFilter extends IMessageFilter {
    /**
     * For {@link io.github.darkkronicle.advancedchatfilters.interfaces.IFilter} this is disabled by
     * default.
     *
     * @param text Text to modify
     * @return Modified text
     */
    @Override
    @Deprecated
    default Optional<FluidText> filter(FluidText text) {
        return Optional.empty();
    }

    /**
     * Get's the color that the background should be.
     *
     * @return SimpleColor that the background should be. If empty it won't change the color
     */
    default Optional<ColorUtil.SimpleColor> getColor() {
        return Optional.empty();
    }

    /**
     * Filters text that is
     *
     * @param filter Filter that the text is being filtered from
     * @param text Current text that will be filtered
     * @param unfiltered The unfiltered version of the text
     * @param search Match results
     * @return Modified text. If empty it won't modify the current text.
     */
    Optional<FluidText> filter(
            ParentFilter filter, FluidText text, FluidText unfiltered, SearchResult search);
}
