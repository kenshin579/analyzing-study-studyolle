# Inflearn 스프링과 JPA기반 웹 애플리케이션 개발 - 백기선님

* 회원가입
    * 회원가입 폼 검증
        * JSR 303 애노테이션 검증
            * 값의 길이, 필수값
        * 커스텀 검증
            * 중복 이메일, 닉네임 여부 확인 - CustomValidator 사용
        * 폼 에러 있을시 다시 보여주기 
    * 패스워드 인코더
        * 패스워드를 평문으로 저장해서는 안된다. 
            * Account 엔티티를 저장할 때 패스워드 인코딩 하기
        * 스프링 시큐리티 권장 Password Encoder
            * PasswordEncoderFactories.createDelegatingPasswordEncoder()
            * 여러 해시 알고리즘을 지원하는 패스워드 인코더
                * 복호화가 가능한 알고리즘을 사용하지 않고 단방향 알고리즘인 해싱 알고리즘을 사용해야 한다.
            * 기본 알고리즘 bcrypt
        * 해싱 알고리즘(bcrypt)과 솔트(salt)
            * 해싱 알고리즘을 쓰는 이유 ? dongchul@email.com / 12345678 + salt => aaaabbbbbdfadf
            * 솔트를 쓰는 이유?
                * 매번 동일하지 않은 값을 입력하더라도 문제 되지 않도록. 
                * 솔트 값은 인코딩을 할 때만 사용한다.
    * 인증 메일 확인
    * 현재 인증된 사용자 정보 참조
        * @AuthenticationPrincipal
        * SpEL
        * Custom Annotation (@CurrentUser)
* 로그인 & 로그아웃
    * Spring Security Form Login 사용
        * UserDetailsService 등록
            * UserDetailsService의 빈이 하나만 등록되어 있으면 SecurityConfig에 별다른 설정하지 않아도 자동으로 사용한다.(PasswordEncoder도 마찬가지)
        * UserDetailsService에서 UserDetails 타입의 객체를 리턴
    * Form Login parameter 네이밍변경은 security 설정에서 변경가능.
    * RememberMe
        * 세션이 만료되더라도 쿠키에 인증정보를 담아두어 인증이 될 수 있도록.            
* RememberMe
    * 세션이 만료 되더라도 로그인을 유지하고 싶을 때 사용하는 방법  
      쿠키에 인증 정보를 남겨두고 세션이 만료 됐을 때에는 쿠키에 남아있는 정보로 인증한다.
    * 해시 기반의 쿠키
        * Username
        * Password
        * 만료 기간
        * Key (애플리케이션 마다 다른 값을 줘야 한다.)
        * 치명적인 단점, 쿠키를 다른 사람이 가져가면... 그 계정은 탈취당한 것과 같다.
    
    * 조금 더 안전한 방법은?
        * 쿠키안에 랜덤한 문자열(토큰)을 만들어 같이 저장하고 매번 인증할 때마다 바꾼다.
        * Username, 토큰
        * 문제는, 이 방법도 취약하다. 쿠키를 탈취 당하면, 해커가 쿠키로 인증을 할 수 있고, 희생자는 쿠키로 인증하지 못한다.
    
    * 조금 더 개선한 방법
        * https://www.programering.com/a/MDO0MzMwATA.html
        * Username + 토큰(랜덤, 매번 바뀜) + 시리즈(랜덤, 고정)
        * 쿠키를 탈취 당한 경우, 희생자는 유효하지 않은 토큰과 유효한 시리즈와 Username으로 접속하게 된다.
        * 이 경우, 모든 토큰을 삭제하여 해커가 더이상 탈취한 쿠키를 사용하지 못하도록 방지할 수 있다.
        * jdbc Token 저장소 사용
            ```java
              http.rememberMe()
                      .userDetailsService(accountService)
                      .tokenRepository(tokenRepository());
              
              @Bean
              public PersistentTokenRepository tokenRepository() {
                  JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
                  jdbcTokenRepository.setDataSource(dataSource);
                  return jdbcTokenRepository;
              }
            ```         
        * persistent_logins 테이블 만들기
            * create table persistent_logins (username varchar(64) not null, series varchar(64) primary key, token varchar(64) not null, last_used timestamp not null)
            * Entity 맵핑으로도 사용 가능.
* Redirect시 간단한 데이터를 전달하고 싶을경우 **RedirectAttributes.addFlashAttribute()** 사용            
* 프로필 수정 테스트
    * 인증된 사용자만 접근할 수 있는 기능을 테스트 해야한다.
        * 실제 DB에 저장되어 있는 정보에 대응하는 인증된 Authentication이 필요.
        * @WithMockUser로는 처리 할 수 없다.
    * 인증된 사용자를 제공할 커스텀 애노테이션 만들기
        * @WithAccount
        * https://docs.spring.io/spring-security/site/docs/current/reference/html/test.html
* ModelMapper 설정
    * 객체의 프로퍼티를 다른 객체의 프로퍼티로 맵핑해주는 유틸리티.
    * 토크나이저 설정
        * CamelCase -> UnderScore
            * UNDERSCORE(_)를 사용했을 때에만 nested 객체를 참조하는 것으로 간주하고 그렇지 않은 경우에는 해당 객체의 직속 프로퍼티에 바인딩 한다.
            * http://modelmapper.org/user-manual/configuration/#matching-strategies
* 지역 도메인
지역도 벨류가 아닌 엔티티

* Zone
    * City
    * LocalNameOfCity
    * Province
* Account 와 Zone의 객체지향적인 관계
    * 다대다 단방향 관계

* SMTP 설정
    * properties에 spring에서 제공하는 mailsender 설정값을 정의하여 실제 Mail이 전송되는 Mailsender 주입받음.  
    * 개인 개정 정보가 들어가 있기때문에 properties 분리
    * 대체 서비스 (Gmail을 그대로 서비스에 사용할 수 없다.)
          * https://sendgrid.com/
          * https://www.mailgun.com/
          * https://aws.amazon.com/ses/
          * https://gsuite.google.com/
          * ...
* EmailService 추상화
    * HTML 이메일 전송
        * MimeMessage 전송
        * MimeMessageHelper : MimeMessage 만들 때 사용할 수 있는 유틸리티
    * EmailService 인터페이스
        * void sendEmail(EmailMessage emailMessage);
    * EmailService 인터페이스 구현체
        * ConsoleEmailService : 콘솔 출력
        * HtmlEmailService : JavaMailSender를 사용한 Html 이메일 전송
