package skcc.arch.biz.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import skcc.arch.app.dto.ApiResponse;
import skcc.arch.app.dto.PageInfo;
import skcc.arch.biz.user.controller.request.UserAuthRequest;
import skcc.arch.biz.user.controller.request.UserCreateRequest;
import skcc.arch.biz.user.controller.request.UserUpdateRequest;
import skcc.arch.biz.user.controller.response.Token;
import skcc.arch.biz.user.controller.response.UserResponse;
import skcc.arch.biz.user.domain.User;
import skcc.arch.biz.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserRestController {

    private final UserService userService;

    @PostMapping("/signup")
    public ApiResponse<UserResponse> signUp(@Valid @RequestBody UserCreateRequest userCreateRequest) {

        User user = userService.signUp(userCreateRequest.toModel());
        return ApiResponse.ok(UserResponse.fromUser(user));
    }

    // 로그인
    @PostMapping("/authenticate")
    public ApiResponse<Token> authenticate(@RequestBody UserAuthRequest userAuthRequest) {
        String accessToken = userService.authenticate(userAuthRequest.getEmail(), userAuthRequest.getPassword());
        Token jwtToken = new Token(accessToken);
        return ApiResponse.ok(jwtToken);
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> searchAllUser(Pageable pageable) {
    
        Page<User> result = userService.findAll(pageable);

        return ApiResponse.ok(result
                .stream()
                .map(UserResponse::fromUser)
                .toList(), PageInfo.fromPage(result));
    }

    @GetMapping("/all")
    public ApiResponse<List<UserResponse>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return ApiResponse.ok(users.stream()
                .map(UserResponse::fromUser)
                .toList());
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getById(@PathVariable long id) {
        return ApiResponse
                .ok(UserResponse.fromUser(userService.getById(id)));
    }

    @GetMapping("/admin")
    public ApiResponse<List<UserResponse>> getAdminUsers(Pageable pageable) {
        log.info("[Controller] : {}" , pageable);
        Page<User> result = userService.findAdminUsers(pageable);
        return ApiResponse.ok(result
                .stream()
                .map(UserResponse::fromUser)
                .toList(), PageInfo.fromPage(result));
    }

    @PatchMapping
    public ApiResponse<UserResponse> updateUser(@Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        User user = userService.updateUser(userUpdateRequest.toModel());
        return ApiResponse.ok(UserResponse.fromUser(user));
    }

}