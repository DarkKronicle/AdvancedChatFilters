package io.github.darkkronicle.advancedchatfilters.interfaces;

import javax.script.Bindings;
import javax.script.ScriptEngine;

public interface IScript<T> {

    T execute(ScriptEngine engine, T input) throws Exception;

    Bindings getBindings();

    void setBindings(Bindings bindings);

}
