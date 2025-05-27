package ru.itmo.credithistory.scoringservice.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.itmo.credithistory.commons.controller.ConstraintViolationExceptionHandler;
import ru.itmo.credithistory.commons.controller.HttpStatusCodeExceptionHandler;

@Configuration
@Import({HttpStatusCodeExceptionHandler.class, ConstraintViolationExceptionHandler.class})
public class ControllerExceptionHandlerConfiguration {}
