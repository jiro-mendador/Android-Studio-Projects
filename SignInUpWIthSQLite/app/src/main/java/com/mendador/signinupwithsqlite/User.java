package com.mendador.signinupwithsqlite;

public class User {
    static String username;

    public User(String username) {
        this.username = username;
    }

    public static String getUsername() {
        return username;
    }

}
