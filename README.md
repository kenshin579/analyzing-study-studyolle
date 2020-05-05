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

