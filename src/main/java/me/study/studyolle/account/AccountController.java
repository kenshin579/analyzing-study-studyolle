package me.study.studyolle.account;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        //model.addAttribute("signUpForm", new SignUpForm());
        model.addAttribute(new SignUpForm()); // class 이름과 동일한 attribute를 사용할 경우 생략 가능
        return "account/sign-up";
    }
}
