/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatfilters;

import io.github.darkkronicle.Konstruct.functions.Variable;
import io.github.darkkronicle.Konstruct.parser.NodeProcessor;
import io.github.darkkronicle.Konstruct.parser.ParseContext;
import io.github.darkkronicle.Konstruct.type.KonstructObject;
import io.github.darkkronicle.Konstruct.type.ListObject;
import io.github.darkkronicle.advancedchatcore.interfaces.IMessageFilter;
import io.github.darkkronicle.advancedchatcore.konstruct.AdvancedChatKonstruct;
import io.github.darkkronicle.advancedchatcore.konstruct.StringMatchObject;
import io.github.darkkronicle.advancedchatcore.util.Color;
import io.github.darkkronicle.advancedchatcore.util.FluidText;
import io.github.darkkronicle.advancedchatcore.util.SearchResult;
import io.github.darkkronicle.advancedchatcore.util.StringMatch;
import io.github.darkkronicle.advancedchatfilters.config.Filter;
import io.github.darkkronicle.advancedchatfilters.config.FiltersConfigStorage;
import io.github.darkkronicle.advancedchatfilters.filters.ColorFilter;
import io.github.darkkronicle.advancedchatfilters.filters.ForwardFilter;
import io.github.darkkronicle.advancedchatfilters.filters.ParentFilter;
import io.github.darkkronicle.advancedchatfilters.filters.ReplaceFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.github.darkkronicle.advancedchatfilters.filters.processors.*;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class FiltersHandler implements IMessageFilter {

    private static final FiltersHandler INSTANCE = new FiltersHandler();

    /**
     * The "Terminate" text. It has a length of zero and is non-null so it will stop the process if
     * returned by the main filter.
     */
    public static final FluidText TERMINATE = new FluidText();

    private FiltersHandler() {}

    public static FiltersHandler getInstance() {
        return INSTANCE;
    }

    @Getter private ArrayList<ColorFilter> colorFilters = new ArrayList<>();

    private ArrayList<ParentFilter> filters = new ArrayList<>();

    @Getter
    private NodeProcessor processor;

    @Override
    public Optional<FluidText> filter(FluidText text) {
        FluidText unfiltered = text;

        Color backgroundColor = null;
        // Filter text
        for (ParentFilter filter : filters) {
            ParentFilter.FilterResult result = filter.filter(text, unfiltered);
            if (result.getColor().isPresent()) {
                backgroundColor = result.getColor().get();
            }
            if (result.getText().isPresent()) {
                text = result.getText().get();
            }
        }
        text.setBackground(backgroundColor);

        if (text.getString().length() != 0) {
            return Optional.of(text);
        }
        return Optional.of(TERMINATE);
    }

    public void loadFilters() {
        setupProcessor();
        filters = new ArrayList<>();
        colorFilters = new ArrayList<>();
        for (Filter filter : FiltersConfigStorage.FILTERS) {
            // If it replaces anything.
            ParentFilter filt = createFilter(filter);
            if (filt != null) {
                filters.add(filt);
            }
        }
    }

    public void setupProcessor() {
        processor = AdvancedChatKonstruct.getInstance().copy();
        processor.addFunction(new ForwardProcessor.ForwardFunction());
        processor.addFunction(new ActionBarProcessor.ActionBarFunction());
        processor.addFunction(new SoundProcessor.SoundFunction());
        processor.addFunction(new NarratorProcessor.NarratorFunction());
        processor.addFunction(new ToastProcessor.ToastFunction());
    }

    public static ParentFilter createFilter(Filter filter) {
        if (!filter.getActive().config.getBooleanValue()) {
            return null;
        }
        ParentFilter filt =
                new ParentFilter(
                        filter.getFind(),
                        filter.getFindString().config.getStringValue().replace("&", "§"),
                        filter.getStripColors().config.getBooleanValue());
        if (filter.getReplace() != null) {
            if (filter.getReplaceTextColor().config.getBooleanValue()) {
                filt.addFilter(
                        new ReplaceFilter(
                                filter.getReplaceTo().config.getStringValue().replaceAll("&", "§"),
                                filter.getReplace(),
                                filter.getTextColor().config.get()));
            } else {
                filt.addFilter(
                        new ReplaceFilter(
                                filter.getReplaceTo().config.getStringValue().replaceAll("&", "§"),
                                filter.getReplace(),
                                null));
            }
        }
        if (filter.getReplaceBackgroundColor().config.getBooleanValue()) {
            filt.addFilter(new ColorFilter(filter.getBackgroundColor().config.get()));
        }
        if (filter.getProcessors().activeAmount() > 0) {
            if (filter.getProcessors().activeAmount() == 1) {
                // If it's only the default, don't do anything
                if (!filter.getProcessors().getDefaultOption().isActive()) {
                    filt.addFilter(new ForwardFilter(filter.getProcessors()));
                }
            } else {
                filt.addForwardFilter(new ForwardFilter(filter.getProcessors()));
            }
        }
        return filt;
    }

    public ParseContext createFilterContext(ReplaceFilter filter, FluidText text, SearchResult result, StringMatch match) {
        ParseContext context = processor.createContext();
        context.addLocalVariable("input", Variable.of(text.getString()));
        context.addLocalVariable("match", Variable.of(new StringMatchObject(match)));
        List<KonstructObject<?>> list = new ArrayList<>();
        for (StringMatch m : result.getMatches()) {
            list.add(new StringMatchObject(m));
        }
        context.addLocalVariable("matches", Variable.of(
                new ListObject(list)
        ));
        return context;
    }

    public ParseContext createTextContext(FluidText text, SearchResult search) {
        ParseContext context = processor.createContext();
        context.addLocalVariable("input", Variable.of(text.getString()));
        List<KonstructObject<?>> list = new ArrayList<>();
        for (StringMatch match : search.getMatches()) {
            list.add(new StringMatchObject(match));
        }
        context.addLocalVariable("matches", Variable.of(
                new ListObject(list)
        ));
        return context;
    }
}
