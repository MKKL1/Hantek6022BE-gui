package com.mkkl.hantekgui.commands;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CommandDispatcher {
    private final BlockingQueue<Command> executionQueue = new LinkedBlockingQueue<>();
    private final Thread dispatcherThread;
    private static CommandDispatcher instance;

    private CommandDispatcher() {
        this.dispatcherThread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    Objects.requireNonNull(executionQueue.take()).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        dispatcherThread.start();
    }

    public void submitCommand(Command command) {
        executionQueue.add(command);
    }

    public void stop() {
        dispatcherThread.interrupt();
    }

    public static CommandDispatcher getInstance() {
        if(instance==null) instance = new CommandDispatcher();
        return instance;
    }
}
