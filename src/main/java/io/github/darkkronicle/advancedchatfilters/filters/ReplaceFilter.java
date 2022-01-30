/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatfilters.filters;

import io.github.darkkronicle.Konstruct.NodeException;
import io.github.darkkronicle.Konstruct.nodes.LiteralNode;
import io.github.darkkronicle.Konstruct.nodes.Node;
import io.github.darkkronicle.Konstruct.reader.builder.InputNodeBuilder;
import io.github.darkkronicle.advancedchatcore.util.Color;
import io.github.darkkronicle.advancedchatcore.util.FluidText;
import io.github.darkkronicle.advancedchatcore.util.SearchResult;
import io.github.darkkronicle.advancedchatfilters.AdvancedChatFilters;
import io.github.darkkronicle.advancedchatfilters.interfaces.IFilter;
import io.github.darkkronicle.advancedchatfilters.interfaces.IMatchReplace;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.Level;

/** Filter used for replacing matches in a Text */
@Environment(EnvType.CLIENT)
public class ReplaceFilter implements IFilter {

    public final Node replaceTo;
    public final IMatchReplace type;
    public final Color color;

    public ReplaceFilter(String replaceTo, IMatchReplace type, Color color) {
        Node node;
        try {
            node = new InputNodeBuilder(replaceTo).build();
        } catch (NodeException e) {
            AdvancedChatFilters.LOGGER.log(Level.WARN, "Error setting up replace filter.", e);
            node = new LiteralNode(replaceTo);
        }
        this.replaceTo = node;
        this.type = type;
        this.color = color;
    }

    @Override
    public Optional<FluidText> filter(
            ParentFilter filter, FluidText text, FluidText unfiltered, SearchResult search) {
        // Grabs FluidText for easy mutability.
        if (type == null) {
            return Optional.empty();
        }
        if (type.matchesOnly()) {
            if (search.size() == 0) {
                return Optional.empty();
            }
            return type.filter(this, text, search);
        } else {
            return type.filter(this, text, null);
        }
    }
}
