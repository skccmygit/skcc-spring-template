package skcc.arch.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import skcc.arch.app.dto.ApiResponse;
import skcc.arch.app.dto.PageInfo;
import skcc.arch.user.controller.response.UserResponseDto;
import skcc.arch.user.domain.User;
import skcc.arch.user.domain.UserCreateRequest;
import skcc.arch.user.service.UserServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserServiceImpl userServiceImpl;

    @PostMapping
    public ApiResponse<UserResponseDto> createUser(@RequestBody UserCreateRequest userCreateRequest) {
        User user = userServiceImpl.create(userCreateRequest);
        return ApiResponse.ok(UserResponseDto.fromUser(user));
    }

    // 로그인
    @PostMapping("/login")
    public ApiResponse<UserResponseDto> login(@RequestBody User loginRequest) {
        User user = userServiceImpl.login(loginRequest.getEmail(), loginRequest.getPassword());
        return ApiResponse.ok(UserResponseDto.fromUser(user));
    }

    @GetMapping
    public ApiResponse<List<UserResponseDto>> searchAllUser(Pageable pageable) {
    
//        Pageable pageable = PageRequest.of(page, size);
        Page<User> result = userServiceImpl.findAll(pageable);

        return ApiResponse.ok(result
                .stream()
                .map(UserResponseDto::fromUser)
                .toList(), PageInfo.fromPage(result));
    }

    @GetMapping("/all")
    public ApiResponse<List<UserResponseDto>> getAllUsers() {
        List<User> users = userServiceImpl.findAllUsers();
        return ApiResponse.ok(users.stream()
                .map(UserResponseDto::fromUser)
                .toList());
    }


}