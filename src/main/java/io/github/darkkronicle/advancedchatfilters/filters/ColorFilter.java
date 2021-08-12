package io.github.darkkronicle.advancedchatfilters.filters;

import io.github.darkkronicle.advancedchatcore.util.ColorUtil;
import io.github.darkkronicle.advancedchatcore.util.FluidText;
import io.github.darkkronicle.advancedchatcore.util.SearchResult;
import io.github.darkkronicle.advancedchatfilters.interfaces.IFilter;
import lombok.NonNull;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Optional;

/**
 * Filter used to change the background color of a message.
 */
@Environment(EnvType.CLIENT)
public class ColorFilter implements IFilter {
    /**
     * {@link ColorUtil.SimpleColor} that will change the background color.
     */
    private final ColorUtil.SimpleColor color;

    public ColorFilter(@NonNull ColorUtil.SimpleColor color) {
        this.color = color;
    }

    @Override
    public Optional<FluidText> filter(ParentFilter filter, FluidText text, FluidText unfiltered, SearchResult search) {
        return Optional.empty();
    }

    // if returned null it won't do anything, but if not null then it will have the default color.
    @Override
    public Optional<ColorUtil.SimpleColor> getColor() {
        return Optional.of(color);
    }
}
