package io.github.darkkronicle.advancedchatfilters.interfaces;

import delight.nashornsandbox.NashornSandbox;
import javax.script.ScriptContext;

/**
 * An interface for easy a filter type of script. It takes in an input and then returns it filtered.
 * @param <T> Object type that will be filtered
 */
public interface IScript<T> {
    /**
     * Executes and filters an input object.
     * @param engine Engine to run the code
     * @param input {@link T} to filter
     * @return Filtered {@link T}
     * @throws Exception If something went wrong while evaluating
     */
    T execute(NashornSandbox engine, T input) throws Exception;
}
