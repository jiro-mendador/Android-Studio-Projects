package com.mendador.depantenumberpairgame;

public class User {
    private static String playerName;

    public User(String name) {
        playerName = name;
    }

    public static String getName() {
        return playerName;
    }
}
