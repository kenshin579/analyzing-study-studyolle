package me.study.studyolle.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.study.studyolle.WithAccount;
import me.study.studyolle.account.AccountRepository;
import me.study.studyolle.account.AccountService;
import me.study.studyolle.domain.Account;
import me.study.studyolle.domain.Tag;
import me.study.studyolle.settings.form.TagForm;
import me.study.studyolle.tag.TagRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    AccountService accountService;

    @AfterEach
    void afterAll() {
        accountRepository.deleteAll();
    }

    @WithAccount("dongchul")
    @DisplayName("계정의 태그 수정폼 ")
    @Test
    void updateTagForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_TAGS_URL))
                .andExpect(view().name(SettingsController.SETTINGS_TAGS_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"));
    }

    @WithAccount("dongchul")
    @DisplayName("계정의 태그 추가 ")
    @Test
    void addTag() throws Exception {
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(SettingsController.SETTINGS_TAGS_URL + "/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle("newTag").get();
        assertNotNull(newTag);
        accountRepository.findByNickname("dongchul").getTags().contains(newTag);
    }

    @WithAccount("dongchul")
    @DisplayName("계정의 태그 삭제 ")
    @Test
    void removeTag() throws Exception {
        Account dongchul = accountRepository.findByNickname("dongchul");
        Tag newTag = tagRepository.save(Tag.builder().title("newTag").build());
        accountService.addTag(dongchul, newTag);

        assertTrue(dongchul.getTags().contains(newTag));

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(SettingsController.SETTINGS_TAGS_URL + "/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(tagForm))
                .with(csrf()))
                .andExpect(status().isOk());

        assertFalse(dongchul.getTags().contains(newTag));
    }

    @WithAccount("dongchul")
    @DisplayName("프로필 수정폼 ")
    @Test
    void updateProfileForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @WithAccount("dongchul")
    @DisplayName("프로필 수정하기 - 입력값 정상")
    @Test
    void updateProfile() throws Exception {
        String bio = "소개 수정";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));
        Account account = accountRepository.findByNickname("dongchul");
        assertEquals(bio, account.getBio());
    }

    @WithAccount("dongchul")
    @DisplayName("프로필 수정하기 - 입력값 에러")
    @Test
    void updateProfile_error() throws Exception {
        String bio = "소개 수정 소개 수정 소개 수정 소개 수정 소개 수정 소개 수정 소개 수정 소개 수정 소개 수정 소개 수정 소개 수정 소개 수정 소개 수정 소개 수정";
        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());
        Account account = accountRepository.findByNickname("dongchul");
        assertNull(account.getBio());
    }

    @WithAccount("dongchul")
    @DisplayName("패스워드 수정폼 ")
    @Test
    void updatePasswordForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_PASSWORD_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithAccount("dongchul")
    @DisplayName("패스워드 수정하기 - 입력값 정상")
    @Test
    void updatePassword() throws Exception {
        String newPassword = "12345678";
        String newPasswordConfirm = "12345678";
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
                .param("newPassword", newPassword)
                .param("newPasswordConfirm", newPasswordConfirm)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PASSWORD_URL))
                .andExpect(flash().attributeExists("message"));
        Account account = accountRepository.findByNickname("dongchul");
        assertTrue(passwordEncoder.matches(newPassword, account.getPassword()));
    }

    @WithAccount("dongchul")
    @DisplayName("패스워드 수정하기 - 패스워드 불일치")
    @Test
    void updatePasswordFail() throws Exception {
        String newPassword = "12345678";
        String newPasswordConfirm = "123456782";
        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
                .param("newPassword", newPassword)
                .param("newPasswordConfirm", newPasswordConfirm)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PASSWORD_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().hasErrors());
    }
}
