package skcc.arch.biz.user.controller;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserViewController {

    // 로그인 화면
    @GetMapping({"/login", "/"})
    public String showLoginForm() {
        return "login";
    }

    // 회원가입 화면
    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }


    // 메인 화면
    @GetMapping("/home")
    public String showHome()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return "home";
    }
}