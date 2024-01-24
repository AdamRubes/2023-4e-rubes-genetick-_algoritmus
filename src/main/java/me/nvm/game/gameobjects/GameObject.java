package me.nvm.game.gameobjects;

import me.nvm.game.GameState;

public class GameObject {
    GameState gameState = GameState.getInstance();
    public double coordinateX;
    public double coordinateY;

    public GameObject(double coordinateX, double coordinateY) {
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
    }
}