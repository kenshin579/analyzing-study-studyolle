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
