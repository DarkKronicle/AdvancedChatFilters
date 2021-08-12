package io.github.darkkronicle.advancedchatfilters.filters.matchreplace;

import io.github.darkkronicle.advancedchatcore.util.FindType;
import io.github.darkkronicle.advancedchatcore.util.FluidText;
import io.github.darkkronicle.advancedchatcore.util.SearchResult;
import io.github.darkkronicle.advancedchatcore.util.SearchUtils;
import io.github.darkkronicle.advancedchatcore.util.StringMatch;
import io.github.darkkronicle.advancedchatfilters.filters.ReplaceFilter;
import io.github.darkkronicle.advancedchatfilters.interfaces.IMatchReplace;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class RomanNumeralTextReplace implements IMatchReplace {

    @Override
    public Optional<FluidText> filter(ReplaceFilter filter, FluidText text, SearchResult search) {
        HashMap<StringMatch, FluidText.StringInsert> replaceMatches = new HashMap<>();
        for (StringMatch match : search.getMatches()) {
            List<StringMatch> matches = SearchUtils.findMatches(match.match, "[0-9]+", FindType.REGEX).orElse(null);
            if (matches == null) {
                continue;
            }
            matches.forEach(stringMatch -> {
                stringMatch.start += match.start;
                stringMatch.end += match.start;
            });
            for (StringMatch m : matches) {
                try {
                    replaceMatches.put(m, (current, match1) -> new FluidText(current.withMessage(SearchUtils.toRoman(Integer.parseInt(m.match)))));
                } catch (Exception e) {
                    // Not an integer
                }
            }
        }
        if (replaceMatches.size() == 0) {
            return Optional.empty();
        }
        text.replaceStrings(replaceMatches);
        return Optional.of(text);
    }

}
