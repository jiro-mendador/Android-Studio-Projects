package com.mendador.numberpairgame;

public class Player {
    private static String playerName;

    public Player(String name) {
        playerName = name;
    }

    public static String getName() {
        return playerName;
    }
}
