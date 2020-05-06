package me.study.studyolle.settings;

import me.study.studyolle.account.CurrentUser;
import me.study.studyolle.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SettingsController {
    @GetMapping("/settings/profile")
    public String profileForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new Profile(account));
        return "settings/profile";
    }
}
