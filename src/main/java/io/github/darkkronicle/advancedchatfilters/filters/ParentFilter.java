/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatfilters.filters;

import io.github.darkkronicle.advancedchatcore.util.*;
import io.github.darkkronicle.advancedchatfilters.FiltersHandler;
import io.github.darkkronicle.advancedchatfilters.interfaces.IFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ParentFilter {

    @Value
    @AllArgsConstructor
    public static class FilterResult {

        Optional<Text> text;
        Optional<Color> color;

        public static FilterResult EMPTY = new FilterResult(Optional.empty(), Optional.empty());
    }

    private List<IFilter> filters;
    private List<ForwardFilter> forwardFilters;

    @Getter
    private final FindType findType;

    @Getter
    private final String findString;

    @Getter
    private final boolean stripColors;

    public ParentFilter(FindType findType, String findString, boolean stripColors) {
        filters = new ArrayList<>();
        forwardFilters = new ArrayList<>();
        this.findString = findString;
        this.findType = findType;
        this.stripColors = stripColors;
    }

    public List<IFilter> getFilters() {
        List<IFilter> all = new ArrayList<>(filters);
        all.addAll(forwardFilters);
        return all;
    }

    public void addFilter(IFilter filter) {
        filters.add(filter);
    }

    public void addForwardFilter(ForwardFilter forwardFilter) {
        forwardFilters.add(forwardFilter);
    }

    public FilterResult filter(Text text, Text unfiltered) {
        String searchString;
        String original = text.getString();
        SearchResult search;
        search = SearchResult.searchOf(text, findString, findType);
        if (search.size() == 0) {
            return FilterResult.EMPTY;
        }
        Color color = null;
        for (IFilter filter : filters) {
            Optional<Text> newtext = filter.filter(this, text, unfiltered, search);
            if (newtext.isPresent()) {
                text = StyleFormatter.formatText(newtext.get());
                if (color != null) {
                    // Make sure forward filter gets the correct background color
                    // TODO fix this
                    // text.setBackground(color);
                }
            }
            Optional<Color> c = filter.getColor();
            if (c.isPresent() && color == null) {
                color = c.get();
                // TODO fix this
                // text.setBackground(color);
            }
        }
        boolean forward = true;
        for (ForwardFilter filter : forwardFilters) {
            if (filter.filter(this, text, unfiltered, search).isPresent()) {
                forward = false;
            }
        }
        if (!forward) {
            return new FilterResult(Optional.of(FiltersHandler.TERMINATE), Optional.empty());
        }
        return new FilterResult(Optional.of(text), Optional.ofNullable(color));
    }
}
