package skcc.arch.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import skcc.arch.app.dto.ApiResponse;
import skcc.arch.app.dto.PageInfo;
import skcc.arch.domain.user.model.User;
import skcc.arch.domain.user.dto.request.UserCreateRequestDto;
import skcc.arch.domain.user.dto.response.UserResponseDto;
import skcc.arch.domain.user.service.UserService;


import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;

    @PostMapping
    public ApiResponse<UserResponseDto> createUser(@RequestBody UserCreateRequestDto userCreateRequestDto) {
        User user = userService.create(userCreateRequestDto);
        return ApiResponse.ok(UserResponseDto.fromUserDto(user));
    }

    // 로그인
    @PostMapping("/login")
    public ApiResponse<UserResponseDto> login(@RequestBody User loginRequest) {
        User user = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return ApiResponse.ok(UserResponseDto.fromUserDto(user));
    }

    @GetMapping
    public ApiResponse<List<UserResponseDto>> searchAllUser(
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "10") int size) {
    
        Pageable pageable = PageRequest.of(page, size);
        Page<User> result = userService.findAll(pageable);

        return ApiResponse.ok(result
                .stream()
                .map(UserResponseDto::fromUserDto)
                .toList(), PageInfo.fromPage(result));
    }

    @GetMapping("/all")
    public ApiResponse<List<UserResponseDto>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return ApiResponse.ok(users.stream()
                .map(UserResponseDto::fromUserDto)
                .toList());
    }


}