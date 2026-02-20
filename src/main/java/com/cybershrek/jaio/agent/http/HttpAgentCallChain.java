package com.cybershrek.jaio.agent.http;

import com.cybershrek.jaio.agent.context.AgentContext;

import java.io.IOException;

public class HttpAgentCallChain {

    AgentContext context;

    public HttpAgentCallChain systemMessage(Object content) {
        context.addSystemMessage(content);
        return this;
    }

    public HttpAgentCallChain onInput(ThrowingConsumer<OnInputChain> consumer) throws IOException {
        consumer.apply(new OnInputChain());
        return this;
    }

    public HttpAgentCallChain onOutput(ThrowingConsumer<OnOutputChain> consumer) throws IOException {
        consumer.apply(new OnOutputChain());
        return this;
    }

    public class OnInputChain {

        public OnInputChain userMessage(ThrowingFunction<I, Object> inputTransformer){
            return this;
        }
    }

    public class OnOutputChain {

    }


    protected class UserLink {

    }



    @FunctionalInterface
    public interface ThrowingConsumer<I> {
        void apply(I i) throws IOException;
    }
    @FunctionalInterface
    public interface ThrowingFunction<I, O> {
        O apply(I i) throws IOException;
    }
}
