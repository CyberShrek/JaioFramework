package com.cybershrek.jaio.agent.special;

import java.util.concurrent.Flow;

public interface FlowableAgent<I, O> extends Agent<I, O> {

    Flow<O> promptFlow(I input);
}
