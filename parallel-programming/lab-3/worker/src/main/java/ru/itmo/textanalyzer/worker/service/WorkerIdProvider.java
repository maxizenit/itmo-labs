package ru.itmo.textanalyzer.worker.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class WorkerIdProvider {

    private Integer workerId;

    public Integer getWorkerId() {
        return Objects.requireNonNull(workerId);
    }

    public void setWorkerId(Integer workerId) {
        this.workerId = workerId;
        log.info("ID set to {}", workerId);
    }
}
