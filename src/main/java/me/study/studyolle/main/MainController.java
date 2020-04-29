package me.study.studyolle.main;

import me.study.studyolle.account.CurrentUser;
import me.study.studyolle.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Objects;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(@CurrentUser Account account, Model model) {
        if (!Objects.isNull(account)) {
            model.addAttribute(account);
        }

        return "index";
    }
}
