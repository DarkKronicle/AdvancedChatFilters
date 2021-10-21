package io.github.darkkronicle.advancedchatfilters.config.gui;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchatfilters.config.FiltersConfigStorage;
import io.github.darkkronicle.advancedchatfilters.scripting.ScriptFilter;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Util;

import javax.annotation.Nullable;

public class GuiAdvancedFilterManager extends GuiListBase<ScriptFilter, WidgetAdvancedFilterEntry, WidgetListAdvancedFilters> implements ISelectionListener<ScriptFilter> {

    public GuiAdvancedFilterManager(Screen parent) {
        super(10, 60);
        this.setParent(parent);
        this.title = StringUtils.translate("advancedchat.screen.main");
    }

    @Override
    protected WidgetListAdvancedFilters createListWidget(int listX, int listY) {
        return new WidgetListAdvancedFilters(listX, listY, this.getBrowserWidth(), this.getBrowserHeight(), this, this);
    }

    @Override
    protected int getBrowserWidth() {
        return this.width - 20;
    }

    @Override
    public void initGui() {
        if (!FiltersConfigStorage.ADVANCED_ON.config.getBooleanValue()) {
            GuiBase.openGui(new GuiAdvancedFilterDisabled(getParent()));
            return;
        }
        super.initGui();

        int x = 10;
        int y = 26;

        String backText = ButtonListener.Type.BACK.getDisplayName();
        int backWidth = StringUtils.getStringWidth(backText) + 10;
        ButtonGeneric back = new ButtonGeneric(x + backWidth, y, backWidth, true, backText);
        this.addButton(back, new ButtonListener(ButtonListener.Type.BACK, this));
        x += back.getWidth() + 2;

        String folderText = ButtonListener.Type.OPEN_FOLDER.getDisplayName();
        int folderWidth = StringUtils.getStringWidth(folderText) + 10;
        ButtonGeneric folder = new ButtonGeneric(x + folderWidth, y, folderWidth, true, folderText);
        this.addButton(folder, new ButtonListener(ButtonListener.Type.OPEN_FOLDER, this));

        this.setListPosition(this.getListX(), 68);
        this.reCreateListWidget();

        this.getListWidget().refreshEntries();

        y += 24;
        x = this.width - 10;
    }

    @Override
    protected int getBrowserHeight() {
        return this.height - 6 - this.getListY();
    }

    @Override
    public void onSelectionChange(@Nullable ScriptFilter entry) {

    }

    public void save() {
        FiltersConfigStorage.saveFromFile();
        FiltersConfigStorage.loadFromFile();
    }

    @Override
    public void onClose() {
        save();
        super.onClose();
    }

    @Override
    protected void closeGui(boolean showParent) {
        save();
        super.closeGui(showParent);
    }

    public void back() {
        closeGui(true);
    }

    public static class ButtonListener implements IButtonActionListener {

        private final GuiAdvancedFilterManager parent;
        private final ButtonListener.Type type;

        public ButtonListener(ButtonListener.Type type, GuiAdvancedFilterManager parent) {
            this.type = type;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            if (this.type == ButtonListener.Type.BACK) {
                parent.back();
            } else if (this.type == Type.OPEN_FOLDER) {
                Util.getOperatingSystem().open(FileUtils.getConfigDirectory().toPath().resolve("advancedchat").resolve("filters").toFile());
            }
        }

        public enum Type {
            BACK("back"),
            OPEN_FOLDER("openfolder")
            ;
            private final String translation;

            private static String translate(String key) {
                return "advancedchatfilters.gui.button." + key;
            }

            Type(String key) {
                this.translation = translate(key);
            }

            public String getDisplayName() {
                return StringUtils.translate(translation);
            }

        }

    }

}

