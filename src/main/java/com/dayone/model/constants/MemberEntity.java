package com.dayone.model.constants;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "MEMBER")
public class MemberEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;

    private String password;

    //List<String> roles;라고 코딩했더니 'Basic' attribute type should not be a container 라는 에러가 떴다.
    //@Transient를 쓰니까 롤 이 persist하게 처리되지 않아서 요청 때마다 롤 부분이 널이 떠서 작도을 하지 않았다.
    //구글링 한 결과 @OneToMany를 붙이면 된다고 했지만 이 엔티티가 다른 테이블과 조인을 하지는 않기 때문이 유효하지 않은 해법이었다.
    //무슨 짓을 해도 List, Set, 등의 컨테이너를 이 엔티티가 받아들이지를 못해서 할 수 없이 아래의 방법으로 처리했다.

    //결국 리스트 스트링이 아니라 그냥 스트링으로 롤을 정의하되, ROLE_READ,ROLE_WRITE ... 이런 식으로 쉼표로 파싱해서
    //다수의 롤들을 하나의 스트링 안에 집어넣게 하고, getAuthorities()메서드에서는 파싱된 롤 스트링들을 전달하게 만들어서 해결했다.
    private String rolesString;

    //스프링이 지원해주는 기능을 사용하기 위해서 구현한 UserDetails 인터페이스 메서드들을 구현해준다.
    //필요한 메서드만 구현해준다.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        /*
            강의에서 등장했던 원래 코드
        return this.roles.stream()
            .map(x -> new SimpleGrantedAuthority(x))
            .collect(Collectors.toList());*/

        return Arrays.stream(rolesString.split(",")).map(x -> new SimpleGrantedAuthority(x)).collect(
            Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
