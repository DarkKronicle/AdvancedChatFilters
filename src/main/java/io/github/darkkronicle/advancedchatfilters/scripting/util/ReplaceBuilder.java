package io.github.darkkronicle.advancedchatfilters.scripting.util;

import io.github.darkkronicle.advancedchatcore.util.FluidText;
import io.github.darkkronicle.advancedchatcore.util.StringMatch;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ReplaceBuilder {

    private final Map<StringMatch, FluidText.StringInsert> replacements = new HashMap<>();

    public ReplaceBuilder() {}

    /**
     * Applies replacements to a {@link FluidText}
     * @param filter Text to apply it to
     * @return Filtered {@link FluidText}
     */
    public FluidText build(FluidText filter) {
        FluidText text = filter.copy();
        text.replaceStrings(replacements);
        return text;
    }

    /**
     * Add's a replacement to trigger on build.
     *
     * Replacement will inherit the style of whatever it is replacing
     * @param match {@link StringMatch} match data
     * @param replacement String to replace to
     */
    public ReplaceBuilder addReplacement(
        StringMatch match,
        String replacement
    ) {
        replacements.put(
            match,
            (current, match1) -> new FluidText(current.withMessage(replacement))
        );
        return this;
    }

    /**
     * Add's a replacement to trigger on build.
     * @param match {@link StringMatch} match data
     * @param text Text to replace to
     */
    public ReplaceBuilder addReplacement(StringMatch match, FluidText text) {
        replacements.put(match, (current, match1) -> text);
        return this;
    }
}
