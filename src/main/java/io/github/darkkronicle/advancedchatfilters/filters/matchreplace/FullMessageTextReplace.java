package io.github.darkkronicle.advancedchatfilters.filters.matchreplace;

import io.github.darkkronicle.advancedchatcore.util.ColorUtil;
import io.github.darkkronicle.advancedchatcore.util.FluidText;
import io.github.darkkronicle.advancedchatcore.util.RawText;
import io.github.darkkronicle.advancedchatcore.util.SearchResult;
import io.github.darkkronicle.advancedchatcore.util.StringMatch;
import io.github.darkkronicle.advancedchatfilters.filters.ReplaceFilter;
import io.github.darkkronicle.advancedchatfilters.interfaces.IMatchReplace;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;

@Environment(EnvType.CLIENT)
public class FullMessageTextReplace implements IMatchReplace {

    @Override
    public Optional<FluidText> filter(
        ReplaceFilter filter,
        FluidText text,
        SearchResult search
    ) {
        StringBuilder totalMatch = new StringBuilder();
        for (StringMatch m : search.getMatches()) {
            totalMatch.append(m.match);
        }
        RawText base = new RawText("None", Style.EMPTY);
        ColorUtil.SimpleColor c = filter.color;
        if (c == null) {
            base =
                text.truncate(search.getMatches().get(0)).getRawTexts().get(0);
        } else {
            Style original = Style.EMPTY;
            TextColor textColor = TextColor.fromRgb(c.color());
            original = original.withColor(textColor);
            base = base.withStyle(original);
        }
        RawText toReplace = base.withMessage(
            search.getGroupReplacements(
                filter.replaceTo.replaceAll("%MATCH%", totalMatch.toString()),
                true
            )
        );
        return Optional.of(new FluidText(toReplace));
    }
}
