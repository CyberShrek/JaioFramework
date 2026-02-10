package com.cybershrek.jaio.agent;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

abstract interface StreamingAgent<I, O> {

    void prompt(I input, Consumer<Object> consumer);
}