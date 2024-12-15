package ru.itmo.textanalyzer.dispatcher.exception;

public class TaskNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Task not found";

    public TaskNotFoundException() {
        super(MESSAGE);
    }
}
