package io.github.darkkronicle.advancedchatfilters.filters;

import io.github.darkkronicle.advancedchatcore.util.FindType;
import io.github.darkkronicle.advancedchatfilters.FiltersHandler;
import io.github.darkkronicle.advancedchatfilters.interfaces.IFilter;
import io.github.darkkronicle.advancedchatcore.util.ColorUtil;
import io.github.darkkronicle.advancedchatcore.util.FluidText;
import io.github.darkkronicle.advancedchatcore.util.SearchResult;
import lombok.AllArgsConstructor;
import lombok.Value;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

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

    public ParentFilter(FindType findType, String findString) {
        filters = new ArrayList<>();
        forwardFilters = new ArrayList<>();
        this.findString = findString;
        this.findType = findType;
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

    public FilterResult filter(FluidText text, FluidText unfiltered) {
        SearchResult search = SearchResult.searchOf(text.getString(), findString, findType);
        if (search.size() == 0) {
            return FilterResult.EMPTY;
        }
        ColorUtil.SimpleColor color = null;
        for (IFilter filter : filters) {
            Optional<FluidText> newtext = filter.filter(this, text, unfiltered, search);
            if (newtext.isPresent()) {
                text = newtext.get();
            }
            Optional<ColorUtil.SimpleColor> c = filter.getColor();
            if (c.isPresent() && color == null) {
                color = c.get();
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
