package io.github.darkkronicle.advancedchatfilters.config.gui;

import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import fi.dy.masa.malilib.gui.wrappers.TextFieldWrapper;
import io.github.darkkronicle.advancedchatfilters.config.FiltersConfigStorage;
import io.github.darkkronicle.advancedchatfilters.scripting.ScriptFilter;
import io.github.darkkronicle.advancedchatfilters.scripting.ScriptManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;

public class WidgetListAdvancedFilters
    extends WidgetListBase<ScriptFilter, WidgetAdvancedFilterEntry> {

    protected final List<TextFieldWrapper<? extends GuiTextFieldGeneric>> textFields = new ArrayList<>();

    @Override
    protected void reCreateListEntryWidgets() {
        this.textFields.clear();
        super.reCreateListEntryWidgets();
    }

    public WidgetListAdvancedFilters(
        int x,
        int y,
        int width,
        int height,
        @Nullable ISelectionListener<ScriptFilter> selectionListener,
        Screen parent
    ) {
        super(x, y, width, height, selectionListener);
        this.browserEntryHeight = 22;
        this.setParent(parent);
    }

    public void addTextField(
        TextFieldWrapper<? extends GuiTextFieldGeneric> text
    ) {
        textFields.add(text);
    }

    @Override
    public void render(
        MatrixStack matrixStack,
        int mouseX,
        int mouseY,
        float partialTicks
    ) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        clearTextFieldFocus();
        return super.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void clearTextFieldFocus() {
        for (TextFieldWrapper<? extends GuiTextFieldGeneric> field : this.textFields) {
            GuiTextFieldGeneric textField = field.getTextField();

            if (textField.isFocused()) {
                textField.setFocused(false);
                break;
            }
        }
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers) {
        for (WidgetAdvancedFilterEntry widget : this.listWidgets) {
            if (widget.onKeyTyped(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return super.onKeyTyped(keyCode, scanCode, modifiers);
    }

    @Override
    protected WidgetAdvancedFilterEntry createListEntryWidget(
        int x,
        int y,
        int listIndex,
        boolean isOdd,
        ScriptFilter entry
    ) {
        return new WidgetAdvancedFilterEntry(
            x,
            y,
            this.browserEntryWidth,
            this.getBrowserEntryHeightFor(entry),
            isOdd,
            entry,
            listIndex,
            this
        );
    }

    @Override
    protected Collection<ScriptFilter> getAllEntries() {
        List<ScriptFilter> filters = new ArrayList<>(
            ScriptManager.getInstance().getFilters()
        );
        filters.addAll(ScriptManager.getInstance().getUnimportedFilters());
        return filters;
    }
}
