package com.atguigu.common.jwt;

import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * Jwt工具类
 */
public class JwtHelper {

    private static long tokenExpiration = 365 * 24 * 60 * 60 * 1000; // 有效时长
    private static String tokenSignKey = "123456"; // 签名：根据它对数据进行编码或加密

    // 根据用户id和用户名称生成token字符串
    public static String createToken(Long userId, String username) {
        String token = Jwts.builder()
                // 分类
                .setSubject("AUTH-USER")

                // 设置token有效时长
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))

                // 主体部分
                .claim("userId", userId)
                .claim("username", username)

                // 签名部分
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }

    // 从生成的token字符串中获取用户id
    public static Long getUserId(String token) {
        try {
            if (StringUtils.isEmpty(token)) return null;

            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
            Claims claims = claimsJws.getBody();
            Integer userId = (Integer) claims.get("userId");
            return userId.longValue();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 从生成的token字符串中获取用户名称
    public static String getUsername(String token) {
        try {
            if (StringUtils.isEmpty(token)) return "";

            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
            Claims claims = claimsJws.getBody();
            return (String) claims.get("username");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 测试
    // 为了做个测试，先把common-utils中的<scope>provided </scope>注释掉，然后刷新一下Maven
    public static void main(String[] args) {
        String token = JwtHelper.createToken(1L,"LUCY");
        System.out.println(token);
        Long userId = JwtHelper.getUserId(token);
        String username = JwtHelper.getUsername(token);
        System.out.println(userId);
        System.out.println(username);
    }
}
