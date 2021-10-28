/*
 * Copyright (C) 2021 DarkKronicle
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.darkkronicle.advancedchatfilters.interfaces;

import delight.nashornsandbox.NashornSandbox;

/**
 * An interface for easy a filter type of script. It takes in an input and then returns it filtered.
 *
 * @param <T> Object type that will be filtered
 */
public interface IScript<T> {
    /**
     * Executes and filters an input object.
     *
     * @param engine Engine to run the code
     * @param input {@link T} to filter
     * @return Filtered {@link T}
     * @throws Exception If something went wrong while evaluating
     */
    T execute(NashornSandbox engine, T input) throws Exception;
}
