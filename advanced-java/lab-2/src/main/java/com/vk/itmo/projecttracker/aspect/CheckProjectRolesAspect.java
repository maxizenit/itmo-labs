package com.vk.itmo.projecttracker.aspect;

import com.vk.itmo.projecttracker.annotation.CheckProjectRoles;
import com.vk.itmo.projecttracker.exception.NoRightsForOperationException;
import com.vk.itmo.projecttracker.model.enm.ProjectRole;
import com.vk.itmo.projecttracker.model.entity.*;
import com.vk.itmo.projecttracker.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CheckProjectRolesAspect {

    UserService userService;

    @Around("@annotation(checkProjectRoles)")
    public Object checkUserProjectRoles(ProceedingJoinPoint joinPoint, CheckProjectRoles checkProjectRoles) throws
            Throwable {
        User currentUser = userService.getCurrentUser();

        Object[] args = joinPoint.getArgs();
        Project project = null;
        for (Object arg : args) {
            if (arg instanceof Project) {
                project = (Project) arg;
                break;
            } else if (arg instanceof Milestone) {
                project = ((Milestone) arg).getProject();
                break;
            } else if (arg instanceof Ticket) {
                project = ((Ticket) arg).getMilestone().getProject();
                break;
            } else if (arg instanceof BugReport) {
                project = ((BugReport) arg).getProject();
                break;
            }
        }

        if (project == null) {
            throw new IllegalArgumentException("Project must be provided as a method argument");
        }

        ProjectRole currentUserRoleOnProject = project.getUserProjectRoles().get(currentUser);
        boolean hasRequiredRole = Arrays.asList(checkProjectRoles.value()).contains(currentUserRoleOnProject);
        if (!hasRequiredRole) {
            throw new NoRightsForOperationException();
        }

        return joinPoint.proceed();
    }
}
