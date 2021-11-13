/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatfilters.filters;

import io.github.darkkronicle.advancedchatcore.util.ColorUtil;
import io.github.darkkronicle.advancedchatcore.util.FindType;
import io.github.darkkronicle.advancedchatcore.util.FluidText;
import io.github.darkkronicle.advancedchatcore.util.RawText;
import io.github.darkkronicle.advancedchatcore.util.SearchResult;
import io.github.darkkronicle.advancedchatcore.util.StringMatch;
import io.github.darkkronicle.advancedchatfilters.FiltersHandler;
import io.github.darkkronicle.advancedchatfilters.interfaces.IFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Value;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Style;

@Environment(EnvType.CLIENT)
public class ParentFilter {

    @Value
    @AllArgsConstructor
    public static class FilterResult {

        Optional<FluidText> text;
        Optional<ColorUtil.SimpleColor> color;

        public static FilterResult EMPTY = new FilterResult(Optional.empty(), Optional.empty());
    }

    private List<IFilter> filters;
    private List<ForwardFilter> forwardFilters;
    private final FindType findType;
    private final String findString;
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

    public static String getWithColors(FluidText text) {
        StringBuilder builder = new StringBuilder("§[ffffff]");
        Style previous = Style.EMPTY;
        for (RawText t : text.getRawTexts()) {
            if ((previous.getColor() != null
                            && !previous.equals(Style.EMPTY)
                            && t.getStyle().equals(Style.EMPTY))
                    || (t.getStyle().getColor() != null
                            && !t.getStyle().getColor().equals(previous.getColor()))) {
                previous = t.getStyle();
                int iColor;
                if (previous.getColor() != null) {
                    iColor = previous.getColor().getRgb();
                } else {
                    iColor = ColorUtil.WHITE.color();
                }
                ColorUtil.SimpleColor color = new ColorUtil.SimpleColor(iColor);
                String hex =
                        String.format("%02x%02x%02x", color.red(), color.green(), color.blue());
                builder.append("§[").append(hex).append(']');
            }
            builder.append(t.getMessage());
        }
        builder.append("§[ffffff]");
        return builder.toString();
    }

    public static SearchResult getOffsetMatch(SearchResult result, String original) {
        SearchResult hex =
                SearchResult.searchOf(result.getInput(), "§\\[[a-fA-F0-9]{6}\\]", FindType.REGEX);
        List<StringMatch> matches = new ArrayList<>();
        for (StringMatch match : result.getMatches()) {
            int depth = 0;
            boolean started = false;
            boolean ended = false;
            int start = -1;
            int end = original.length();
            for (StringMatch hexMatch : hex.getMatches()) {
                if (started && ended) {
                    break;
                }
                if (!started && match.start <= hexMatch.start) {
                    start = match.start - depth;
                    started = true;
                }
                if (match.end <= hexMatch.start) {
                    ended = true;
                    end = match.end - depth;
                } else if (match.end <= hexMatch.end) {
                    ended = true;
                    end = match.end - depth - 9;
                } else {
                    end = match.end - depth - 9;
                }
                depth += 9;
            }
            if (start == -1) {
                start = match.start - depth;
            }
            matches.add(new StringMatch(original.substring(start, end), start, end));
        }
        return new SearchResult(original, result.getSearch(), result.getFinder(), matches);
    }

    public FilterResult filter(FluidText text, FluidText unfiltered) {
        String searchString;
        String original = text.getString();
        if (!stripColors) {
            searchString = getWithColors(text);
        } else {
            searchString = original;
        }
        SearchResult search = SearchResult.searchOf(searchString, findString, findType);
        if (search.size() == 0) {
            return FilterResult.EMPTY;
        }
        if (!stripColors) {
            // Offset the search results based off of colors
            search = getOffsetMatch(search, original);
        }
        ColorUtil.SimpleColor color = null;
        for (IFilter filter : filters) {
            Optional<FluidText> newtext = filter.filter(this, text, unfiltered, search);
            if (newtext.isPresent()) {
                text = newtext.get();
                if (color != null) {
                    // Make sure forward filter gets the correct background color
                    text.setBackgroundColor(color);
                }
            }
            Optional<ColorUtil.SimpleColor> c = filter.getColor();
            if (c.isPresent() && color == null) {
                color = c.get();
                text.setBackgroundColor(color);
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
