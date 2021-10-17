package io.github.darkkronicle.advancedchatfilters.filters;

import io.github.darkkronicle.advancedchatcore.util.FindType;
import io.github.darkkronicle.advancedchatcore.util.RawText;
import io.github.darkkronicle.advancedchatcore.util.StringMatch;
import io.github.darkkronicle.advancedchatfilters.FiltersHandler;
import io.github.darkkronicle.advancedchatfilters.interfaces.IFilter;
import io.github.darkkronicle.advancedchatcore.util.ColorUtil;
import io.github.darkkronicle.advancedchatcore.util.FluidText;
import io.github.darkkronicle.advancedchatcore.util.SearchResult;
import lombok.AllArgsConstructor;
import lombok.Value;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Style;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    private static String getWithColors(FluidText text) {
        StringBuilder builder = new StringBuilder();
        Style previous = Style.EMPTY;
        for (RawText t : text.getRawTexts()) {
            if (t.getStyle().getColor() != null && !t.getStyle().getColor().equals(previous.getColor())) {
                previous = t.getStyle();
                String hex = Integer.toHexString(previous.getColor().getRgb());
                builder.append(hex);
            }
            builder.append(t.getMessage());
        }
        return builder.toString();
    }

    private static String getWithoutColors(String input) {
        SearchResult hex = SearchResult.searchOf(input, "§\\[[a-fA-F0-9]{6}\\]", FindType.REGEX);
        int last = -1;
        StringBuilder builder = new StringBuilder();
        for (StringMatch match : hex.getMatches()) {
            if (last <= -1) {
                last = match.end;
                continue;
            }
            builder.append(input, last, match.start);
            last = match.end;
        }
        builder.append(input, last, input.length());
        return builder.toString();
    }

    public FilterResult filter(FluidText text, FluidText unfiltered) {
        String searchString;
        if (stripColors) {
            searchString = getWithColors(text);
        } else {
            searchString = text.getString();
        }
        SearchResult search = SearchResult.searchOf(searchString, findString, findType);
        if (search.size() == 0) {
            return FilterResult.EMPTY;
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
