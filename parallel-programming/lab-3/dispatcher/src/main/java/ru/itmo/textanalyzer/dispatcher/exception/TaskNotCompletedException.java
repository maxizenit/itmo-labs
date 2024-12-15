package ru.itmo.textanalyzer.dispatcher.exception;

public class TaskNotCompletedException extends RuntimeException {

    private static final String MESSAGE = "Task not completed";

    public TaskNotCompletedException() {
        super(MESSAGE);
    }
}
