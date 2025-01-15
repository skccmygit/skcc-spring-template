package skcc.arch.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import skcc.arch.app.dto.ApiResponse;
import skcc.arch.domain.user.dto.UserDto;
import skcc.arch.domain.user.dto.request.UserCreateRequestDto;
import skcc.arch.domain.user.dto.response.UserResponseDto;
import skcc.arch.domain.user.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;

    @PostMapping
    public ApiResponse<UserResponseDto> createUser(@RequestBody UserCreateRequestDto userCreateRequestDto) {
        UserDto userDto = userService.create(userCreateRequestDto);
        return ApiResponse.ok(UserResponseDto.fromUserDto(userDto));
    }

    // 로그인
    @PostMapping("/login")
    public ApiResponse<UserResponseDto> login(@RequestBody UserDto loginRequest) {
        UserDto userDto = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return ApiResponse.ok(UserResponseDto.fromUserDto(userDto));
    }
}