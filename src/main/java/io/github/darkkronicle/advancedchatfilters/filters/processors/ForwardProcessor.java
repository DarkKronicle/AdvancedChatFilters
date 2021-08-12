package io.github.darkkronicle.advancedchatfilters.filters.processors;

import io.github.darkkronicle.advancedchatcore.interfaces.IMatchProcessor;
import io.github.darkkronicle.advancedchatcore.util.FluidText;
import io.github.darkkronicle.advancedchatcore.util.SearchResult;

import javax.annotation.Nullable;

public class ForwardProcessor implements IMatchProcessor {
    @Override
    public Result processMatches(FluidText text, @Nullable FluidText unfiltered, @Nullable SearchResult search) {
        return Result.FORCE_FORWARD;
    }
}
