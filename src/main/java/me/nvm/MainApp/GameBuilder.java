package me.nvm.MainApp;

import me.nvm.game.Game;
import me.nvm.game.GameState;

public class GameBuilder {
    private double gravity, speed, powerOfJump, speedMultiplier;
    private int sizeOfHole, sizeOfGaps;
    private boolean isHeadless = false;

    Resolution resolution;

    public GameBuilder setPredefinedRules(){
        return this
                .setGravity(600)
                .setSpeed(200)
                .setPowerOfJump(300)
                .setSizeOfHole(200)
                .setSizeOfGaps(450)
                .setSpeedMultiplier(1);
    }


    public GameBuilder setGravity(double gravity) {
        this.gravity = gravity;
        return this;
    }

    public GameBuilder setSpeed(double speed) {
        this.speed = speed;
        return this;
    }

    public GameBuilder setPowerOfJump(double powerOfJump) {
        this.powerOfJump = powerOfJump;
        return this;
    }

    public GameBuilder setSizeOfHole(int sizeOfHole) {
        this.sizeOfHole = sizeOfHole;
        return this;
    }

    public GameBuilder setSizeOfGaps(int sizeOfGaps) {
        this.sizeOfGaps = sizeOfGaps;
        return this;
    }

    public GameBuilder setSpeedMultiplier(double speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
        return this;
    }

    public GameBuilder setHeadless(boolean headless) {
        isHeadless = headless;
        return this;
    }

    public GameBuilder setResolution(Resolution resolution){
        this.resolution = resolution;
        return this;
    }

    public Game build(){




        Game game = new Game(gravity, speed, powerOfJump, sizeOfHole, sizeOfGaps, isHeadless, speedMultiplier, resolution);
        return game;
    }
}
