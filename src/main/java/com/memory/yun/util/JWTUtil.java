package com.memory.yun.util;

import com.memory.yun.model.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @Author NJUPT wly
 * @Date 2021/9/15 6:52 下午
 * @Version 1.0
 */
@Slf4j
public class JWTUtil {

    /**
     * 过期时间
     */
    private static final long EXPIRE = 1000 * 60 * 60 * 24 * 7;

    /**
     * 密钥
     */
    private static final String SECRET = "stone.net666";

    /**
     * 密钥前缀
     */
    private static final String TOKEN_PREFIX = "stoneshop";

    /**
     * subject
     */
    private static final String SUBJECT = "stone";

    /**
     * 根据用户信息，生成令牌
     *
     * @param user
     * @return
     */
    public static String geneJsonWebToken(LoginUser user) {

        if (user == null){
            throw new NullPointerException();
        }
        Long userId = user.getId();
        String token = Jwts.builder().setSubject(SUBJECT)
                .claim("head_img", user.getHeadImg())
                .claim("id", userId)
                .claim("name", user.getName())
                .claim("openid",user.getOpenId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(SignatureAlgorithm.HS256, SECRET).compact();

        token = TOKEN_PREFIX + token;

        return token;
    }

    /**
     * 校验token的方法
     *
     * @param token
     * @return
     */
    public static Claims checkJWT(String token) {

        try {

            final Claims claims = Jwts.parser().setSigningKey(SECRET)
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody();
            return claims;

        } catch (Exception e) {
            log.error("jwt 解密失败");
            return null;
        }

    }



}
