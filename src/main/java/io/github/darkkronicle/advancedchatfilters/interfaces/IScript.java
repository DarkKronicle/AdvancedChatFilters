package io.github.darkkronicle.advancedchatfilters.interfaces;

import delight.nashornsandbox.NashornSandbox;

import javax.script.Bindings;
import javax.script.ScriptContext;

public interface IScript<T> {

    T execute(NashornSandbox engine, T input) throws Exception;

    ScriptContext getContext();

}
