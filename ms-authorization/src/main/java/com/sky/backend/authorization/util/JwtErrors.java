package com.sky.backend.authorization.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class JwtErrors {

    public static String codeFrom(JwtException jwtException) {
        if (jwtException instanceof ExpiredJwtException) return "TOKEN_EXPIRED";
        return "TOKEN_INVALID";
    }
}
