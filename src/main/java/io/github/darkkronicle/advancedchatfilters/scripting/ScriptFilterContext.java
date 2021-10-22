package io.github.darkkronicle.advancedchatfilters.scripting;

import io.github.darkkronicle.advancedchatcore.util.ColorUtil;
import io.github.darkkronicle.advancedchatcore.util.FindType;
import io.github.darkkronicle.advancedchatcore.util.FluidText;
import io.github.darkkronicle.advancedchatcore.util.RawText;
import io.github.darkkronicle.advancedchatcore.util.SearchResult;
import io.github.darkkronicle.advancedchatcore.util.SearchUtils;
import io.github.darkkronicle.advancedchatcore.util.StringMatch;
import io.github.darkkronicle.advancedchatfilters.registry.MatchProcessorRegistry;
import io.github.darkkronicle.advancedchatfilters.scripting.util.ReplaceBuilder;
import io.github.darkkronicle.advancedchatfilters.scripting.util.TextBuilder;
import java.util.HashMap;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Style;

@Environment(EnvType.CLIENT)
public class ScriptFilterContext {

    @Getter
    @Setter
    private FluidText text;

    private final FluidText unfiltered;

    public static FindType toFindType(String type) {
        return FindType.fromFindType(type.toLowerCase());
    }

    public ScriptFilterContext(FluidText text) {
        this.text = text;
        this.unfiltered = text.copy();
    }

    /**
     * Checks to see if there is a match in the text
     *
     * @param findType The {@link FindType} name
     * @param matchString Filter string
     * @return If there is a match
     */
    public boolean isMatch(String findType, String matchString) {
        return SearchUtils.isMatch(
            text.getString(),
            matchString,
            toFindType(findType)
        );
    }

    /**
     * Get's the string content of the text
     * @return String content of the text
     */
    public String getString() {
        return text.getString();
    }

    /**
     * Get's matches based off of a {@link FindType} and match from the text
     * @param findType Name of the {@link FindType}
     * @param matchString String to match
     * @return List of {@link StringMatch}'s that matched in the text's string
     */
    public List<StringMatch> getMatches(String findType, String matchString) {
        SearchResult result = SearchResult.searchOf(
            text.getString(),
            matchString,
            toFindType(findType)
        );
        return result.getMatches();
    }

    /**
     * Get's a full {@link SearchResult} from a findtype and input string from the text.
     * @param findType Name of the {@link FindType}
     * @param matchString String to match
     * @return {@link SearchResult} containing all match data
     */
    public SearchResult getSearchResult(String findType, String matchString) {
        return SearchResult.searchOf(
            text.getString(),
            matchString,
            toFindType(findType)
        );
    }

    /**
     * Set's the text. No formatting, just raw string data.
     * @param text Text to set it to.
     */
    public void setTextPlain(String text) {
        this.text = new FluidText(new RawText(text, Style.EMPTY));
    }

    /**
     * Replace's text based on specific string replace.
     * @param start Start position to replace
     * @param end End position to replace
     * @param replace What to replace to
     */
    public void replaceTextWithString(int start, int end, String replace) {
        HashMap<StringMatch, FluidText.StringInsert> toReplace = new HashMap<>();
        StringMatch match = new StringMatch(
            getString().substring(start, end),
            start,
            end
        );
        toReplace.put(
            match,
            (current, match1) -> new FluidText(current.withMessage(replace))
        );
        text.replaceStrings(toReplace);
    }

    /**
     * Replace's text based on specific string replace.
     * @param start Start position to replace
     * @param end End position to replace
     * @param replace What to replace to. {@link FluidText}
     */
    public void replaceTextWithText(int start, int end, FluidText replace) {
        HashMap<StringMatch, FluidText.StringInsert> toReplace = new HashMap<>();
        StringMatch match = new StringMatch(
            getString().substring(start, end),
            start,
            end
        );
        toReplace.put(match, (current, match1) -> replace);
        text.replaceStrings(toReplace);
    }

    /**
     * Send text to specific processor
     * @param processor Processor name in {@link MatchProcessorRegistry}
     */
    public void sendToProcessor(String processor, FluidText text) {
        for (MatchProcessorRegistry.MatchProcessorOption option : MatchProcessorRegistry
            .getInstance()
            .getAll()) {
            if (option.getSaveString().equals(processor)) {
                option.getOption().process(text, unfiltered);
                return;
            }
        }
    }

    /**
     * Instantiates a new {@link TextBuilder}
     * @return New text builder
     */
    public TextBuilder getNewTextBuilder() {
        return new TextBuilder();
    }

    /**
     * Instantiates a new {@link TextBuilder} with a value
     * @return New text builder
     */
    public TextBuilder getNewTextBuilder(String string) {
        return new TextBuilder(string);
    }

    /**
     * Instantiates a new {@link ReplaceBuilder}
     * @return New replace builder
     */
    public ReplaceBuilder getNewReplaceBuilder() {
        return new ReplaceBuilder();
    }

    /**
     * Get's style at specific character position.
     * @param pos Position to get the style
     * @return The style found
     */
    public Style getStyleAt(int pos) {
        int textLength = text.getString().length();
        if (pos >= textLength) {
            // Ensure no oob stuff
            pos = textLength - 1;
        }
        if (pos < 0) {
            pos = 0;
        }
        return text.truncate(new StringMatch("pos", pos, pos + 1)).getStyle();
    }

    /**
     * Get's the {@link io.github.darkkronicle.advancedchatcore.util.ColorUtil.SimpleColor} of a color
     * @param style Style to grab color from
     * @return Color if it exists, null if there is no color
     */
    public ColorUtil.SimpleColor getColor(Style style) {
        if (style.getColor() == null) {
            return null;
        }
        return new ColorUtil.SimpleColor(style.getColor().getRgb());
    }
}
