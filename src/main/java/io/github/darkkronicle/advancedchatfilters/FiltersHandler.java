package io.github.darkkronicle.advancedchatfilters;

import io.github.darkkronicle.advancedchatcore.interfaces.IMessageFilter;
import io.github.darkkronicle.advancedchatcore.util.ColorUtil;
import io.github.darkkronicle.advancedchatcore.util.FluidText;
import io.github.darkkronicle.advancedchatfilters.config.Filter;
import io.github.darkkronicle.advancedchatfilters.config.FiltersConfigStorage;
import io.github.darkkronicle.advancedchatfilters.filters.ColorFilter;
import io.github.darkkronicle.advancedchatfilters.filters.ForwardFilter;
import io.github.darkkronicle.advancedchatfilters.filters.ParentFilter;
import io.github.darkkronicle.advancedchatfilters.filters.ReplaceFilter;
import java.util.ArrayList;
import java.util.Optional;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class FiltersHandler implements IMessageFilter {

    private static final FiltersHandler INSTANCE = new FiltersHandler();

    /**
     * The "Terminate" text. It has a length of zero and is non-null so it will stop the process if
     * returned by the main filter.
     */
    public static final FluidText TERMINATE = new FluidText();

    private FiltersHandler() {}

    public static FiltersHandler getInstance() {
        return INSTANCE;
    }

    @Getter
    private ArrayList<ColorFilter> colorFilters = new ArrayList<>();

    private ArrayList<ParentFilter> filters = new ArrayList<>();

    @Override
    public Optional<FluidText> filter(FluidText text) {
        FluidText unfiltered = text;

        ColorUtil.SimpleColor backgroundColor = null;
        // Filter text
        for (ParentFilter filter : filters) {
            ParentFilter.FilterResult result = filter.filter(text, unfiltered);
            if (result.getColor().isPresent()) {
                backgroundColor = result.getColor().get();
            }
            if (result.getText().isPresent()) {
                text = result.getText().get();
            }
        }
        text.setBackgroundColor(backgroundColor);

        if (text.getString().length() != 0) {
            return Optional.of(text);
        }
        return Optional.of(TERMINATE);
    }

    public void loadFilters() {
        filters = new ArrayList<>();
        colorFilters = new ArrayList<>();
        for (Filter filter : FiltersConfigStorage.FILTERS) {
            // If it replaces anything.
            ParentFilter filt = createFilter(filter);
            if (filt != null) {
                filters.add(filt);
            }
        }
    }

    public static ParentFilter createFilter(Filter filter) {
        if (!filter.getActive().config.getBooleanValue()) {
            return null;
        }
        ParentFilter filt = new ParentFilter(
            filter.getFind(),
            filter.getFindString().config.getStringValue().replace("&", "§"),
            filter.getStripColors().config.getBooleanValue()
        );
        if (filter.getReplace() != null) {
            if (filter.getReplaceTextColor().config.getBooleanValue()) {
                filt.addFilter(
                    new ReplaceFilter(
                        filter
                            .getReplaceTo()
                            .config.getStringValue()
                            .replaceAll("&", "§"),
                        filter.getReplace(),
                        filter.getTextColor().config.getSimpleColor()
                    )
                );
            } else {
                filt.addFilter(
                    new ReplaceFilter(
                        filter
                            .getReplaceTo()
                            .config.getStringValue()
                            .replaceAll("&", "§"),
                        filter.getReplace(),
                        null
                    )
                );
            }
        }
        if (filter.getReplaceBackgroundColor().config.getBooleanValue()) {
            filt.addFilter(
                new ColorFilter(
                    filter.getBackgroundColor().config.getSimpleColor()
                )
            );
        }
        if (filter.getProcessors().activeAmount() > 0) {
            if (filter.getProcessors().activeAmount() == 1) {
                // If it's only the default, don't do anything
                if (!filter.getProcessors().getDefaultOption().isActive()) {
                    filt.addFilter(new ForwardFilter(filter.getProcessors()));
                }
            } else {
                filt.addForwardFilter(
                    new ForwardFilter(filter.getProcessors())
                );
            }
        }
        return filt;
    }
}
