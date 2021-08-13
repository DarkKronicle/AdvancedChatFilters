package io.github.darkkronicle.advancedchatfilters.filters.matchreplace;

import io.github.darkkronicle.advancedchatcore.util.FluidText;
import io.github.darkkronicle.advancedchatcore.util.SearchResult;
import io.github.darkkronicle.advancedchatcore.util.StringMatch;
import io.github.darkkronicle.advancedchatfilters.filters.ParentFilter;
import io.github.darkkronicle.advancedchatfilters.filters.ReplaceFilter;
import io.github.darkkronicle.advancedchatfilters.interfaces.IMatchReplace;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.HashMap;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ChildrenTextReplace implements IMatchReplace {

    @Override
    public Optional<FluidText> filter(ReplaceFilter filter, FluidText text, SearchResult search) {
        // We don't want new filters to modify what old filters would already have done.
        // It would lead to repeats of words, and just other kinds of messes.
        // To combat this we modify all the matches that haven't been matched yet based off of the new string length.
        for (int i = 0; i < search.getMatches().size(); i++) {
            StringMatch match = search.getMatches().get(i);
            FluidText current = text.truncate(match);
            if (current == null) {
                continue;
            }
            for (ParentFilter f : filter.getChildren()) {
                ParentFilter.FilterResult filteredText = f.filter(current, text);
                if (filteredText.getText().isPresent()) {
                    HashMap<StringMatch, FluidText.StringInsert> toReplace = new HashMap<>();

                    // Get old length and new length. As well as modify the message that is currently being modified
                    // in the match
                    int oldLength = current.getString().length();
                    current = filteredText.getText().get();
                    int newLength = current.getString().length();
                    int modifyLength = newLength - oldLength;
                    // Take the new length and figure out how much each match needs to move to have it work.
                    for (int j = i + 1; j < search.getMatches().size(); j++) {
                        StringMatch m = search.getMatches().get(j);
                        m.start += modifyLength;
                        m.end += modifyLength;
                    }
                    // Put in the new simple text for easy use
                    final FluidText toAdd = current;
                    toReplace.put(match, (current1, match1) -> toAdd);
                    // Replace the match
                    text.replaceStrings(toReplace);
                    // Set background color
                    if (filteredText.getColor().isPresent()) {
                        text.setBackgroundColor(filteredText.getColor().get());
                    }
                }
            }
        }
        return Optional.of(text);
    }

    @Override
    public boolean useChildren() {
        return true;
    }
}
