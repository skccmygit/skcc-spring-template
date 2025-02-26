package skcc.arch.biz.sample;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/view")
public class ViewController {

    // 메인 화면
    @GetMapping("/home")
    public String showHome()
    {
        return "home";
    }
}
