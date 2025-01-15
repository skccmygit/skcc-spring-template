package skcc.arch.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import skcc.arch.app.dto.ApiResponse;
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
    public ApiResponse<List<UserResponseDto>> searchAllUser() {
        List<UserResponseDto> result = userService.findAllUsers()
                .stream()
                .map(UserResponseDto::fromUserDto)
                .toList();
        return ApiResponse.ok(result);
    }
}