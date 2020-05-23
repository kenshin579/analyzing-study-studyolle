package me.study.studyolle.account;

import lombok.RequiredArgsConstructor;
import me.study.studyolle.domain.Account;
import me.study.studyolle.domain.Tag;
import me.study.studyolle.settings.form.Notification;
import me.study.studyolle.settings.form.Profile;
import org.modelmapper.ModelMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService implements UserDetailsService { // UserDetails Bean 이 하나만 있으면 자동으로 사용한다.

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Transactional
    public Account processNewAccount(@Valid SignUpForm signUpForm) {
        //회원 가입 처리
        Account newAccount = saveNewAccount(signUpForm);
        newAccount.generateEmailCheckToken();
        //Mail Send
        sendSignUpConfirmEmail(newAccount);
        return newAccount;
    }

    private Account saveNewAccount(@Valid SignUpForm signUpForm) {
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(passwordEncoder.encode(signUpForm.getPassword())) // TODO Encoding 해야함
                .studyCreatedByWeb(true)
                .studyEnrollmentResultByWeb(true)
                .studyUpdateByWeb(true)
                .build();

        return accountRepository.save(account);
    }

    public void sendSignUpConfirmEmail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("스터디올래, 회원 가입 인증");
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());
        javaMailSender.send(mailMessage);
    }

    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        // 정석적으로 인증을 하려면 AuthenticationManager에서 authenticate 를 통해 token을 만들어야 한다.
        // 정석적 방법을 쓰지 않는 이유는 정석적인 방법을 사용한다면 plain password가 token에 저장되기 때문인데 형재는 plain password를 사용하지 않을 것이기 때문에
        // 이 방법을 사용한다.
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException { // 로그인 처리, Spring Security에서 자동으로 처리함.
        Account account = accountRepository.findByEmail(emailOrNickname);
        if (account == null) {
            account = accountRepository.findByNickname(emailOrNickname);
        }

        if (account == null) {
            throw new UsernameNotFoundException(emailOrNickname);
        }
        return new UserAccount(account);  // Principal 객체 리턴
    }

    @Transactional
    public void completeSignUp(Account account) {
        account.completeSignUp();
        login(account);
    }

    public void updateProfile(Account account, Profile profile) {
        modelMapper.map(profile, account);
//        account.setUrl(profile.getUrl());
//        account.setOccupation(profile.getOccupation());
//        account.setBio(profile.getBio());
//        account.setLocation(profile.getLocation());
//        account.setProfileImage(profile.getProfileImage());
        accountRepository.save(account);
    }

    @Transactional
    public void updatePassword(Account account, String newPassword) {
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
    }

    public void updateNotification(Account account, Notification notification) {
        modelMapper.map(notification, account);
//        account.setStudyCreatedByWeb(notification.isStudyCreatedByWeb());
//        account.setStudyCreatedByEmail(notification.isStudyCreatedByEmail());
//        account.setStudyUpdateByWeb(notification.isStudyUpdateByWeb());
//        account.setStudyUpdateByEmail(notification.isStudyUpdateByEmail());
//        account.setStudyEnrollmentResultByEmail(notification.isStudyEnrollmentResultByEmail());
//        account.setStudyEnrollmentResultByWeb(notification.isStudyEnrollmentResultByWeb());
        accountRepository.save(account);
    }

    public void updateNickname(Account account, String nickname) {
        account.setNickname(nickname);
        accountRepository.save(account);
        login(account);
    }

    public void sendLoginLink(Account account) {
        account.generateEmailCheckToken();
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(account.getEmail());
        mailMessage.setSubject("스터디올래, 로그인 링크");
        mailMessage.setText("/login-by-email?token=" + account.getEmailCheckToken() +
                "&email=" + account.getEmail());
        javaMailSender.send(mailMessage);
    }

    public void addTag(Account account, Tag tag) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        byId.ifPresent(a -> a.getTags().add(tag));

        // accountRepository.getOne() LazyLoding

    }

    public Set<Tag> getTags(Account account) {
        Optional<Account> byId = accountRepository.findById(account.getId());
        return byId.orElseThrow().getTags();
    }
}
