package com.vk.itmo.projecttracker.controller.v1;

import com.vk.itmo.projecttracker.exception.ViewOtherUserBugReportsException;
import com.vk.itmo.projecttracker.exception.ViewOtherUserProjectsException;
import com.vk.itmo.projecttracker.exception.ViewOtherUserTicketsException;
import com.vk.itmo.projecttracker.model.dto.UserDto;
import com.vk.itmo.projecttracker.model.dto.request.LoginUserRequestDto;
import com.vk.itmo.projecttracker.model.dto.request.RegisterUserRequestDto;
import com.vk.itmo.projecttracker.model.dto.response.GetUserBugReportsDto;
import com.vk.itmo.projecttracker.model.dto.response.GetUserProjectsResponseDto;
import com.vk.itmo.projecttracker.model.dto.response.GetUserTicketsResponseDto;
import com.vk.itmo.projecttracker.model.dto.response.LoginUserResponseDto;
import com.vk.itmo.projecttracker.model.mapper.BugReportMapper;
import com.vk.itmo.projecttracker.model.mapper.ProjectMapper;
import com.vk.itmo.projecttracker.model.mapper.TicketMapper;
import com.vk.itmo.projecttracker.model.mapper.UserMapper;
import com.vk.itmo.projecttracker.service.AuthService;
import com.vk.itmo.projecttracker.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/v1/users")
public class UserControllerV1 {

    AuthService authService;
    UserService userService;
    UserMapper userMapper;
    ProjectMapper projectMapper;
    TicketMapper ticketMapper;
    BugReportMapper bugReportMapper;

    @PostMapping(value = "/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody RegisterUserRequestDto request) {
        var user = authService.registerUser(request.username(), request.password(), request.email());
        var response = userMapper.fromUserToUserDto(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginUserResponseDto> loginUser(@RequestBody LoginUserRequestDto request) {
        var token = authService.getToken(request.username(), request.password());
        return ResponseEntity.ok(new LoginUserResponseDto(token));
    }

    @GetMapping("/{userId}/projects")
    public ResponseEntity<Set<GetUserProjectsResponseDto>> getUserProjects(@PathVariable("userId") String username) {
        var currentUserUsername = userService.getCurrentUser().getUsername();
        if (!Objects.equals(currentUserUsername, username)) {
            throw new ViewOtherUserProjectsException();
        }

        var user = userService.getUserByUsername(username);
        var userProjectsRoles = user.getUserProjectRoles();
        var response = userProjectsRoles.entrySet()
                .stream()
                .map(projectMapper::fromProjectProjectRoleEntryToGetUserProjectsResponseDto)
                .collect(Collectors.toSet());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{userId}/tickets")
    public ResponseEntity<Set<GetUserTicketsResponseDto>> getUserTickets(@PathVariable("userId") String username) {
        var currentUserUsername = userService.getCurrentUser().getUsername();
        if (!Objects.equals(currentUserUsername, username)) {
            throw new ViewOtherUserTicketsException();
        }

        var user = userService.getUserByUsername(username);
        var tickets = user.getTickets();
        var response =
                tickets.stream().map(ticketMapper::fromTicketToGetUserTicketsResponseDto).collect(Collectors.toSet());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{userId}/bugreports")
    public ResponseEntity<Set<GetUserBugReportsDto>> getUserBugReports(@PathVariable("userId") String username) {
        var currentUserUsername = userService.getCurrentUser().getUsername();
        if (!Objects.equals(currentUserUsername, username)) {
            throw new ViewOtherUserBugReportsException();
        }

        var user = userService.getUserByUsername(username);
        var bugReports = user.getBugReports();
        var response = bugReports.stream()
                .map(bugReportMapper::fromBugReportToGetUserBugReportsDto)
                .collect(Collectors.toSet());
        return ResponseEntity.ok().body(response);
    }
}
