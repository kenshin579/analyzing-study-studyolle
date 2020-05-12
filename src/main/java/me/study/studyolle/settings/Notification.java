package me.study.studyolle.settings;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.study.studyolle.domain.Account;

@Data
@NoArgsConstructor
public class Notification {
    private boolean studyCreatedByEmail;

    private boolean studyCreatedByWeb;

    private boolean studyEnrollmentResultByEmail;

    private boolean studyEnrollmentResultByWeb;

    private boolean studyUpdateByEmail;

    private boolean studyUpdateByWeb;

    public Notification(Account account) {
        this.studyCreatedByEmail = account.isStudyCreatedByEmail();
        this.studyCreatedByWeb = account.isStudyCreatedByWeb();
        this.studyEnrollmentResultByEmail = account.isStudyEnrollmentResultByEmail();
        this.studyEnrollmentResultByWeb = account.isStudyEnrollmentResultByWeb();
        this.studyUpdateByEmail = account.isStudyUpdateByEmail();
        this.studyUpdateByWeb = account.isStudyUpdateByWeb();
    }
}
