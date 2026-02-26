package com.aw.login;

public class UserContext {

    private static final ThreadLocal<LoginUserInfo> CONTEXT = new ThreadLocal<>();

    public static void set(LoginUserInfo user) {
        CONTEXT.set(user);
    }

    public static LoginUserInfo get() {
        return CONTEXT.get();
    }

    public static void remove() {
        CONTEXT.remove();
    }

}