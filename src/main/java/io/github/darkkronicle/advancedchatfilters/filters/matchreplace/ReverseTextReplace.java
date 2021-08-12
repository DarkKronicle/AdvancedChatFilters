package io.github.darkkronicle.advancedchatfilters.filters.matchreplace;

import io.github.darkkronicle.advancedchatcore.util.FluidText;
import io.github.darkkronicle.advancedchatcore.util.SearchResult;
import io.github.darkkronicle.advancedchatcore.util.StringMatch;
import io.github.darkkronicle.advancedchatfilters.filters.ReplaceFilter;
import io.github.darkkronicle.advancedchatfilters.interfaces.IMatchReplace;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.HashMap;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ReverseTextReplace implements IMatchReplace {

    @Override
    public Optional<FluidText> filter(ReplaceFilter filter, FluidText text, SearchResult search) {
        HashMap<StringMatch, FluidText.StringInsert> replaceMatches = new HashMap<>();
        for (StringMatch match : search.getMatches()) {
            if (match.match.length() <= 1) {
                // Can't reverse < 1
                continue;
            }
            replaceMatches.put(match, (current, match1) -> new FluidText(current.withMessage(new StringBuilder(match.match).reverse().toString())));
        }
        if (replaceMatches.size() == 0) {
            return Optional.empty();
        }
        text.replaceStrings(replaceMatches);
        return Optional.of(text);
    }

}
