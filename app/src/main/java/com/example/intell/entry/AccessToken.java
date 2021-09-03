package com.example.intell.entry;

import java.io.Serializable;

public class AccessToken implements Serializable {

    public static final String ACCESS_TOKEN = "token";
    public static final String API_URL = "https://open.ys7.com/";
    public static String APP_KEY = "df21b714ee1a4941984137eae76e1245";
    public static String SECRET = "b1be440054c3fabb71d03743c290d99a";

    private static final long serialVersionUID = 6187447685293862071L;

    public final String msg;
    public final int code;
    public final Token data;

    public AccessToken(String msg, int code, Token token) {
        this.msg = msg;
        this.code = code;
        this.data = token;
    }

    public static class Token {
        public final String accessToken;
        public final long expireTime;

        public Token(String accessToken, long expireTime) {
            this.accessToken = accessToken;
            this.expireTime = expireTime;
        }
    }
}
