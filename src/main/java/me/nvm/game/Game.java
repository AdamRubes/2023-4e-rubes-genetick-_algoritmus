package me.nvm.game;

import me.nvm.GNN.Client;
import me.nvm.MainApp.Resolution;

import java.util.HashMap;

public class Game {
    GameState gameState;
    GameBackendSuper mainBackend;
    GraphicsInterface mainGraphics;
    double speedMultiplier;
    boolean isHeadless;
    Resolution resolution;


    public Game(double gravity, double speed, double powerOfJump, int sizeOfHole, int sizeOfGaps, boolean isHeadless, double speedMultiplier, Resolution resolution) {
        this.isHeadless = isHeadless;
        this.speedMultiplier = speedMultiplier;
        this.resolution = resolution;

        this.gameState = GameState.getInstance(gravity, speed, powerOfJump, sizeOfHole, sizeOfGaps);
    }

    public void startPlayerGameReworked(){
        restartReworked();
        mainBackend = new GameBackendPlayer();
        changeSpeedOfGame(speedMultiplier);
        mainGraphics = new GameGraphics((GameBackendPlayer) mainBackend, resolution);
        mainBackend.linkToGraphics(mainGraphics);

        mainBackend.start();
    }

    public void startTrainingGame(HashMap<Integer, Client> clientHashMap) {
        restartReworked();
        mainBackend = new GameBackendAI(clientHashMap);
        changeSpeedOfGame(speedMultiplier);
        mainGraphics = new GameGraphicsTraining((GameBackendAI) mainBackend, resolution);
        mainBackend.linkToGraphics(mainGraphics);

        mainBackend.start();
    }


    public void restartReworked(){
        this.gameState.reset();
    }

    public void stopGame() {
        gameState.setGameOver(true);
    }

    public void waitOnGame(){
        try {
            mainBackend.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void changeResolution(Resolution resolution){
        this.resolution = resolution;
        mainGraphics.setResolution(resolution);
    }

    public void changeSpeedOfGame(double speedMultiplier){
        mainBackend.setSpeedMultiplier(speedMultiplier);
    }


}
