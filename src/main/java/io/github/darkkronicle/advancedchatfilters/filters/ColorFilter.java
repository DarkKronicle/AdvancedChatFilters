/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatfilters.filters;

import io.github.darkkronicle.advancedchatcore.util.Color;
import io.github.darkkronicle.advancedchatcore.util.FluidText;
import io.github.darkkronicle.advancedchatcore.util.SearchResult;
import io.github.darkkronicle.advancedchatfilters.interfaces.IFilter;
import java.util.Optional;
import lombok.NonNull;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** Filter used to change the background color of a message. */
@Environment(EnvType.CLIENT)
public class ColorFilter implements IFilter {

    /** {@link Color} that will change the background color. */
    private final Color color;

    public ColorFilter(@NonNull Color color) {
        this.color = color;
    }

    @Override
    public Optional<FluidText> filter(
            ParentFilter filter, FluidText text, FluidText unfiltered, SearchResult search) {
        return Optional.empty();
    }

    // if returned null it won't do anything, but if not null then it will have the default color.
    @Override
    public Optional<Color> getColor() {
        return Optional.of(color);
    }
}
