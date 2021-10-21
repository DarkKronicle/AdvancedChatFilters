package io.github.darkkronicle.advancedchatfilters.interfaces;

import io.github.darkkronicle.advancedchatcore.interfaces.IMessageFilter;
import io.github.darkkronicle.advancedchatcore.util.FluidText;
import io.github.darkkronicle.advancedchatcore.util.SearchResult;
import io.github.darkkronicle.advancedchatfilters.filters.ReplaceFilter;
import java.util.Optional;
import javax.annotation.Nullable;

/**
 * An interface to replace message content from a {@link ReplaceFilter}
 *
 * Similar to {@link IMessageFilter} but supports {@link SearchResult}.
 */
public interface IMatchReplace extends IMessageFilter {
    default boolean matchesOnly() {
        return true;
    }

    /**
     * Filter text based off of previous matches.
     *
     * @param filter Filter that triggered the operation
     * @param text Text that was filtered
     * @param search Matches
     * @return Optional of new text. If returned empty the text will not be replaced
     */
    Optional<FluidText> filter(
        ReplaceFilter filter,
        FluidText text,
        @Nullable SearchResult search
    );

    @Override
    default Optional<FluidText> filter(FluidText text) {
        return Optional.empty();
    }

    /**
     * Whether to forward details to children as well.
     * @return Value to forward to children
     */
    default boolean useChildren() {
        return false;
    }
}
