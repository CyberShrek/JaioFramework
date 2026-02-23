package com.cybershrek.jaio.agent.http;

import com.cybershrek.jaio.agent.StreamingAgent;
import com.cybershrek.jaio.exception.AgentException;
import com.cybershrek.jaio.exception.HttpAgentException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class StreamingHttpAgent<I, O, C> {

    private Consumer<C> chunkConsumer;


    public synchronized O prompt(I input, Consumer<C> chunkConsumer) throws AgentException {
        this.chunkConsumer = chunkConsumer;
        return null;
    }


    protected O readOkBody(InputStream body) throws IOException, HttpAgentException {
        var consumer = this.chunkConsumer;
        var chunks   = new ArrayList<C>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(body))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String chunkData = prepareChunkData(line);
                if (chunkData != null) {
                    C chunk = readOkChunk(chunkData);
                    if (chunk != null) {
                        chunks.add(chunk);
                        if (consumer != null) {
                            consumer.accept(chunk);
                        }
                    }
                }
            }
        }
        return mergeChunks(chunks);
    }

    protected abstract C readOkChunk(String data) throws IOException, HttpAgentException;

    protected abstract O mergeChunks(List<C> chunks) throws IOException, HttpAgentException;

    protected String prepareChunkData(String line) {
        if (line.startsWith("data: ")) {
            line = line.substring(6);
        }
        if (line.startsWith("{") && line.endsWith("}")) {
            return line;
        }
        return null;
    }
}