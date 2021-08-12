package io.github.darkkronicle.advancedchatfilters.config.gui;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchatcore.config.gui.GuiConfig;
import io.github.darkkronicle.advancedchatcore.config.gui.GuiConfigHandler;
import io.github.darkkronicle.advancedchatfilters.config.Filter;
import io.github.darkkronicle.advancedchatfilters.config.FiltersConfigStorage;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class GuiFilterManager extends GuiListBase<Filter, WidgetFilterEntry, WidgetListFilters> implements ISelectionListener<Filter> {

    private List<GuiConfigHandler.TabButton> tabButtons;

    public GuiFilterManager(List<GuiConfigHandler.TabButton> tabButtons) {
        super(10, 60);
        this.title = StringUtils.translate("advancedchat.screen.main");
        this.tabButtons = tabButtons;
    }

    @Override
    protected WidgetListFilters createListWidget(int listX, int listY) {
        return new WidgetListFilters(listX, listY, this.getBrowserWidth(), this.getBrowserHeight(), this, null, this);
    }

    @Override
    protected int getBrowserWidth() {
        return this.width - 20;
    }

    @Override
    public void initGui() {
        super.initGui();

        int x = 10;
        int y = 26;

        int rows = 1;

        for (GuiConfigHandler.TabButton tab : tabButtons) {
            int newY = this.createButton(tab, y);
            if (newY != y) {
                rows++;
                y = newY;
            }
        }


        this.setListPosition(this.getListX(), 68 + (rows - 1) * 22);
        this.reCreateListWidget();

        this.getListWidget().refreshEntries();

        y += 24;
        x = this.width - 10;
        x -= this.addButton(x, y, ButtonListener.Type.ADD_FILTER) + 2;
        this.addButton(x, y, ButtonListener.Type.IMPORT);
    }

    private int createButton(GuiConfigHandler.TabButton button, int y) {
        this.addButton(button.getButton(), new ButtonListenerConfigTabs(button));
        return button.getButton().getY();
    }

    protected int addButton(int x, int y, ButtonListener.Type type) {
        ButtonGeneric button = new ButtonGeneric(x, y, -1, true, type.getDisplayName());
        this.addButton(button, new ButtonListener(type, this));

        return button.getWidth();
    }

    private static class ButtonListener implements IButtonActionListener {

        private final Type type;
        private final GuiFilterManager gui;

        public ButtonListener(Type type, GuiFilterManager gui) {
            this.type = type;
            this.gui = gui;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            if (this.type == Type.ADD_FILTER) {
                FiltersConfigStorage.FILTERS.add(Filter.getRandomFilter());
                Collections.sort(FiltersConfigStorage.FILTERS);
                this.gui.getListWidget().refreshEntries();
            } else if (this.type == Type.IMPORT) {
                GuiBase.openGui(new SharingScreen(null, gui));
            }
        }

        public enum Type {
            ADD_FILTER("addfilter"),
            IMPORT("import")
            ;

            private static String translate(String key) {
                return "advancedchatfilters.gui.button." + key;
            }

            private final String translationKey;

            Type(String translationKey) {
                this.translationKey = translate(translationKey);
            }

            public String getDisplayName() {
                return StringUtils.translate(this.translationKey);
            }
        }
    }

    @Override
    protected int getBrowserHeight() {
        return this.height - 6 - this.getListY();
    }

    @Override
    public void onSelectionChange(@Nullable Filter entry) {

    }

    private static class ButtonListenerConfigTabs implements IButtonActionListener {
        private final GuiConfigHandler.TabButton tabButton;

        public ButtonListenerConfigTabs(GuiConfigHandler.TabButton tabButton) {
            this.tabButton = tabButton;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            GuiConfigHandler.getInstance().activeTab = this.tabButton.getTab().getName();
            GuiBase.openGui(this.tabButton.getTab().getScreen(GuiConfigHandler.getInstance().getButtons()));

        }
    }
}
