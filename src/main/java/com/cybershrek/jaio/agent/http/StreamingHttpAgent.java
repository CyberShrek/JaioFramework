package com.cybershrek.jaio.agent.http;

import com.cybershrek.jaio.exception.AgentException;

import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class StreamingHttpAgent<I, O> extends HttpAgent<I, O> {

    protected StreamingHttpAgent(Function<I, HttpRequest.Builder> reqBuilderFn,
                                 Function<InputStream,         O> resBodyFn) {

        super(reqBuilderFn, resBodyFn);
    }

    public void prompt(I input, Consumer<InputStream> chunkCr) throws AgentException {
        client.sendAsync(reqBuilderFn.apply(input).build(), new HttpResponse.BodyHandler<Void>() {
            @Override
            public HttpResponse.BodySubscriber<Void> apply(HttpResponse.ResponseInfo info) {

                return HttpResponse.BodySubscribers.fromSubscriber(
                        new Flow.Subscriber<>() {
                            private Flow.Subscription subscription;

                            @Override
                            public void onSubscribe(Flow.Subscription subscription) {
                                this.subscription = subscription;
                                subscription.request(1);
                            }

                            @Override
                            public void onNext(List<ByteBuffer> item) {
                                System.out.println(item);
                                subscription.request(1);
                            }

                            @Override
                            public void onComplete() {
                            }

                            @Override
                            public void onError(Throwable t) {
                                t.printStackTrace();
                            }
                        });
            }
        });
    }

}