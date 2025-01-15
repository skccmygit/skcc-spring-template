package skcc.arch.domain.user.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserViewController {

    // 회원가입 화면
    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    // 로그인 화면
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    // 메인 화면
    @GetMapping("/home")
    public String showHome() {
        return "home";
    }
}