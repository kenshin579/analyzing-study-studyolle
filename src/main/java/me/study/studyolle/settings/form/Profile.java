package me.study.studyolle.settings.form;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.study.studyolle.domain.Account;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor // 기본생성자가 있어야 Spring MVC에서 파라미터를 받을때 Account가 필요한 생성자를 사용하지 않아 NPE가 나지않는다.
// Spring MVC에선 Post요청에서 기본생성자로 객체를 만든 후 Setter를 사용하여 매핑한다.
public class Profile {
    @Length(max = 35)
    private String bio;

    @Length(max = 50)
    private String url;

    @Length(max = 50)
    private String occupation;

    @Length(max = 50)
    private String location;

    private String profileImage;
}
