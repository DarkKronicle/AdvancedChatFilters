package io.github.darkkronicle.advancedchatfilters.filters;

import io.github.darkkronicle.advancedchatfilters.interfaces.IFilter;
import io.github.darkkronicle.advancedchatfilters.interfaces.IMatchReplace;
import io.github.darkkronicle.advancedchatcore.util.ColorUtil;
import io.github.darkkronicle.advancedchatcore.util.FluidText;
import io.github.darkkronicle.advancedchatcore.util.SearchResult;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Filter used for replacing matches in a Text
 */
@Environment(EnvType.CLIENT)
public class ReplaceFilter implements IFilter {

    public final String replaceTo;
    public final IMatchReplace type;
    public final ColorUtil.SimpleColor color;

    @Getter
    private ArrayList<ParentFilter> children = new ArrayList<>();

    public void addChild(ParentFilter filter) {
        children.add(filter);
    }

    public ReplaceFilter(String replaceTo, IMatchReplace type, ColorUtil.SimpleColor color) {
        this.replaceTo = replaceTo;
        this.type = type;
        this.color = color;
    }

    @Override
    public Optional<FluidText> filter(ParentFilter filter, FluidText text, FluidText unfiltered, SearchResult search) {
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
