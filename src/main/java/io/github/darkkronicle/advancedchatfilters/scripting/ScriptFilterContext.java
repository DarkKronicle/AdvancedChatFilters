package io.github.darkkronicle.advancedchatfilters.scripting;

import io.github.darkkronicle.advancedchatcore.util.FindType;
import io.github.darkkronicle.advancedchatcore.util.FluidText;
import io.github.darkkronicle.advancedchatcore.util.RawText;
import io.github.darkkronicle.advancedchatcore.util.SearchResult;
import io.github.darkkronicle.advancedchatcore.util.SearchUtils;
import io.github.darkkronicle.advancedchatcore.util.StringMatch;
import io.github.darkkronicle.advancedchatfilters.registry.MatchProcessorRegistry;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Style;

import java.util.HashMap;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ScriptFilterContext {

    @Getter
    private FluidText text;
    private final FluidText unfiltered;

    public static FindType toFindType(String type) {
        return FindType.fromFindType(type.toLowerCase());
    }

    public ScriptFilterContext(FluidText text) {
        this.text = text;
        this.unfiltered = text.copy();
    }

    public boolean isMatch(String findType, String matchString) {
        return SearchUtils.isMatch(text.getString(), matchString, toFindType(findType));
    }

    public String getString() {
        return text.getString();
    }

    public List<StringMatch> getMatches(String findType, String matchString) {
        SearchResult result = SearchResult.searchOf(text.getString(), matchString, toFindType(findType));
        return result.getMatches();
    }

    public SearchResult getSearchResult(String findType, String matchString) {
        return SearchResult.searchOf(text.getString(), matchString, toFindType(findType));
    }

    public void setText(String text) {
        this.text = new FluidText(new RawText(text, Style.EMPTY));
    }

    public void replaceText(int start, int end, String replace) {
        HashMap<StringMatch, FluidText.StringInsert> toReplace = new HashMap<>();
        StringMatch match = new StringMatch(getString().substring(start, end), start, end);
        toReplace.put(match, (current, match1) -> new FluidText(current.withMessage(replace)));
        text.replaceStrings(toReplace);
    }

    public void sendToProcessor(String processor) {
        for (MatchProcessorRegistry.MatchProcessorOption option : MatchProcessorRegistry.getInstance().getAll()) {
            if (option.getSaveString().equals(processor)) {
                option.getOption().process(text, unfiltered);
                return;
            }
        }
    }

}
