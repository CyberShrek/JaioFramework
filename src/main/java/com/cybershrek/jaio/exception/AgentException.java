package com.cybershrek.jaio.exception;

public class AgentException extends Exception {
    public AgentException(String message) {
        super(message);
    }
    public AgentException(Exception e) {
        super(e);
    }
    public AgentException(Throwable throwable) {
        super(throwable);
    }
}