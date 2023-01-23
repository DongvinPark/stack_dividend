package com.dayone.security;

import com.dayone.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    @Value("${spring.jwt.secret}")
    private String secretKey;
    private static final String KEY_ROLES = "roles";
    private static final int TOKEN_EXPIRE_TIME = 1000*60*60; // 1 hour

    private final MemberService memberService;


    /**
     * 토큰 생성
     * @Param username
     * @Param roles
     * @return String
     * */
    public String generateToken(String username, String roles){

        List<String> roleList = Arrays.stream(roles.split(",")).collect(Collectors.toList());

        Claims claims = Jwts.claims().setSubject(username);
        claims.put(KEY_ROLES, roleList);

        var now = new Date();
        var expiredDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS512, this.secretKey) // 사용할 암호화 알고리즘 및 비밀키
                .compact();
    }


    public Authentication getAuthentication(String jwt){
        UserDetails userDetails = this.memberService.loadUserByUsername(this.getUsername(jwt));

        //스프링에서 지원하는 토큰형식으로 변환한다.
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }


    public String getUsername(String token){
        return this.parseClaims(token).getSubject();
    }


    public boolean validateToken(String token){
        if(!StringUtils.hasText(token)) return false;

        var claims = this.parseClaims(token);
        return !claims.getExpiration().before(new Date());
    }


    //토큰을 검증하는 기능에서 필요로 하는 메서드다.
    private Claims parseClaims(String token){
        try {
            return Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token).getBody();
        } catch(ExpiredJwtException e){
            return e.getClaims();
        }
    }

}
