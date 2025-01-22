package skcc.arch.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import skcc.arch.app.dto.ApiResponse;
import skcc.arch.app.dto.PageInfo;
import skcc.arch.user.controller.response.Token;
import skcc.arch.user.controller.response.UserResponseDto;
import skcc.arch.user.domain.User;
import skcc.arch.user.domain.UserCreateRequest;
import skcc.arch.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;

    @PostMapping
    public ApiResponse<UserResponseDto> createUser(@RequestBody UserCreateRequest userCreateRequest) {
        User user = userService.create(userCreateRequest);
        return ApiResponse.ok(UserResponseDto.fromUser(user));
    }

    // 로그인
    @PostMapping("/authenticate")
    public ApiResponse<Token> authenticate(@RequestBody User loginRequest) {
        String accessToken = userService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        Token jwtToken = new Token(accessToken);
        return ApiResponse.ok(jwtToken);
    }

    @GetMapping
    public ApiResponse<List<UserResponseDto>> searchAllUser(Pageable pageable) {
    
        Page<User> result = userService.findAll(pageable);

        return ApiResponse.ok(result
                .stream()
                .map(UserResponseDto::fromUser)
                .toList(), PageInfo.fromPage(result));
    }

    @GetMapping("/all")
    public ApiResponse<List<UserResponseDto>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return ApiResponse.ok(users.stream()
                .map(UserResponseDto::fromUser)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable long id) {
        return ResponseEntity
                .ok()
                .body(UserResponseDto.fromUser(userService.getById(id)));
    }

    @GetMapping("/admin")
    public ApiResponse<List<UserResponseDto>> getAdminUsers(Pageable pageable) {
        Page<User> result = userService.findAll(pageable);
        return ApiResponse.ok(result
                .stream()
                .map(UserResponseDto::fromUser)
                .toList(), PageInfo.fromPage(result));
    }


}